package tora.plugin;

import gw.lang.reflect.BaseTypeInfo;
import gw.lang.reflect.IMethodInfo;
import gw.lang.reflect.IType;
import gw.lang.reflect.ITypeInfo;
import gw.lang.reflect.MethodList;
import tora.parser.Parser;

public class JavascriptClassTypeInfo extends BaseTypeInfo implements ITypeInfo
{
  private final MethodList _methods;
  private final Parser _parser;

  public JavascriptClassTypeInfo( JavascriptClassType javascriptType, tora.parser.Parser parser )
  {
    super( javascriptType );
    _parser = parser;
    _methods = new MethodList();
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