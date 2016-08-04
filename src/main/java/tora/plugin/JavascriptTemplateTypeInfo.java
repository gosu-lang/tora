package tora.plugin;

import gw.lang.reflect.*;
import gw.util.GosuExceptionUtil;
import tora.parser.tree.ParameterNode;
import tora.parser.tree.template.RawStringNode;
import tora.parser.tree.template.TemplateNode;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavascriptTemplateTypeInfo extends BaseTypeInfo implements ITypeInfo
{
  private final ScriptEngine _engine;
  private final MethodList _methods;
  private final TemplateNode _templateNode;

  public JavascriptTemplateTypeInfo(JavascriptTemplateType javascriptType, TemplateNode templateNode)
  {
    super( javascriptType );
    _templateNode = templateNode;
    _engine = new ScriptEngineManager().getEngineByName("nashorn");
    try {
      _engine.eval(templateNode.genCode());
    } catch (ScriptException e) {
      throw GosuExceptionUtil.forceThrow( e );
    }
    _methods = new MethodList();
    //Only one method to render template to string
    _methods.add(new MethodInfoBuilder()
            .withName("renderToString")
            .withReturnType(TypeSystem.getByFullName("java.lang.String"))
            .withStatic()
            .withParameters(templateNode.getFirstChild(ParameterNode.class).toParamList())
            .withCallHandler( ( ctx, args ) -> {
                return renderToString(args);
              })
            .build( this ) );
  }

  //Calls the generated renderToString function with raw strings from template
  private String renderToString(Object ...args) {
    try {
      //make argument list including the raw string list
      Object[] argsWithStrings = Arrays.copyOf(args, args.length + 1);
      List rawStrings =  _templateNode.getChildren(RawStringNode.class)
              .stream()
              .map(node -> node.genCode())
              .collect(Collectors.toList());
      argsWithStrings[argsWithStrings.length-1] = rawStrings;
      String  ret = (String) ((Invocable)_engine).invokeFunction( "renderToString", argsWithStrings);
      return ret;
    } catch (Exception e) {
      throw GosuExceptionUtil.forceThrow( e );
    }
  }

  @Override
  public MethodList getMethods()
  {
    return _methods;
  }

  @Override
  public IMethodInfo getCallableMethod(CharSequence strMethod, IType... params) {
    return FIND.callableMethod( getMethods(), strMethod, params );
  }

  @Override
  public IMethodInfo getMethod( CharSequence methodName, IType... params )
  {
    return FIND.method(getMethods(), methodName, params);
  }
}