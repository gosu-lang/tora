package tora.plugin;

import com.sun.xml.internal.rngom.digested.DDataPattern;
import gw.config.CommonServices;
import gw.lang.reflect.*;
import gw.util.GosuExceptionUtil;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import tora.parser.Parser;
import tora.parser.tree.*;

import javax.script.*;
import java.lang.reflect.Method;
import java.util.*;

public class JavascriptClassTypeInfo extends BaseTypeInfo implements ITypeInfo
{
  private final ScriptEngine _engine;
  private IConstructorInfo _constructor;
  private List<IConstructorInfo> _constructorList;
  private final MethodList _methods;
  private ProgramNode _programNode;
  private List<IPropertyInfo> _propertiesList;
  Map<String, IPropertyInfo> _propertiesMap;

  public JavascriptClassTypeInfo( JavascriptTypeBase javascriptType, ProgramNode programNode)
  {
    super( javascriptType );

    _programNode = programNode;
    ClassNode classNode = programNode.getFirstChild(ClassNode.class);
    _constructorList = new ArrayList<>();
    _methods = new MethodList();
    _propertiesList = new ArrayList<>();
    _propertiesMap = new HashMap<String, IPropertyInfo>();
    try {
      _engine = new ScriptEngineManager().getEngineByName("nashorn");
      _engine.eval(programNode.genCode());
      addConstructor(classNode);
      addMethods(classNode);
      addProperties(classNode);
    } catch (ScriptException e) {
      throw GosuExceptionUtil.forceThrow(e);
    }
  }

  private void addConstructor(ClassNode classNode) {
    ConstructorNode constructor = classNode.getFirstChild(ConstructorNode.class);
    ParameterInfoBuilder[] params = (constructor == null)?null:makeParamList(constructor.getArgs());
    _constructor = new ConstructorInfoBuilder()
            .withParameters(params)
            .withConstructorHandler((args) -> {
                try {
                  JSObject classObject =  (ScriptObjectMirror) _engine.eval(classNode.getName());
                  return classObject.newObject(args);
                } catch (ScriptException e) {
                  throw GosuExceptionUtil.forceThrow( e );
                }
            }).build(this);
    _constructorList.add(_constructor);
  }

  private void addProperties(ClassNode classNode) {

    for (PropertyNode node : classNode.getChildren(PropertyNode.class)) {
      IPropertyInfo prop = new PropertyInfoBuilder()
              .withName(node.getName())
              .withStatic(node.isStatic())
              .withType(TypeSystem.getByFullName("dynamic.Dynamic"))
              .withAccessor(new IPropertyAccessor() {
                /*getProperty will accessor for static props only*/
                Object classObject = _engine.get(classNode.getName());
                //Use the classObject as the context for static properties
                @Override
                public Object getValue(Object o) {
                  return ((Bindings) classObject).get(node.getName());
                }
                @Override
                public void setValue(Object ctx, Object value) {
                  ((Bindings) classObject).put(node.getName(), value);
                }
              })
              .build(this);
      _propertiesMap.put(prop.getName(), prop);
      _propertiesList.add(prop);
    }
  }

  private void addMethods(ClassNode classNode) throws ScriptException {
    Object classObject = _engine.get(classNode.getName());
//    IType superType = TypeSystem.getByFullName();
//
//    for (IMethodInfo method : superType.getTypeInfo().getMethods()) {
//       System.out.println(method.toString());
//    }

    for (FunctionNode node : classNode.getChildren(FunctionNode.class)) {
      _methods.add(new MethodInfoBuilder()
              .withName(node.getName())
              .withStatic(node.isStatic())
              .withParameters(makeParamList(node.getArgs()))
              .withReturnType(TypeSystem.getByFullName("dynamic.Dynamic"))
              .withCallHandler((ctx, args) -> {
                try {
                  if (node.isStatic()) ctx = classObject;
                  Object o = ((Invocable) _engine).invokeMethod(ctx, node.getName(), args);
                  return o;
                } catch (Exception e) {
                  throw GosuExceptionUtil.forceThrow(e);
                }

              })
              .build(this));
    }
  }

  private ParameterInfoBuilder[] makeParamList (String args) {
    String[] argList = args.split(",");
    ParameterInfoBuilder[] parameterInfoBuilders = new ParameterInfoBuilder[argList.length];
    for (int i = 0; i < argList.length; i++) {
      parameterInfoBuilders[i] = new ParameterInfoBuilder().withName(argList[i])
              .withDefValue(CommonServices.getGosuIndustrialPark().getNullExpressionInstance())
              .withType(TypeSystem.getByFullName("dynamic.Dynamic"));
    }
    return parameterInfoBuilders;
  }

  @Override
  public List<? extends IConstructorInfo> getConstructors() {
    return _constructorList;
  }

  @Override
  public IConstructorInfo getConstructor( IType... params ) {
    return _constructor;
  }

  @Override
  public IConstructorInfo getCallableConstructor ( IType... params ) {
    return _constructor;
  }

  @Override
  public List<? extends IPropertyInfo> getProperties() {
    return _propertiesList;
  }

  @Override
  public IPropertyInfo getProperty(CharSequence propName) {
    return _propertiesMap.get(propName.toString());
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