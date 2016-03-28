package tora.plugin;

import gw.config.CommonServices;
import gw.lang.reflect.BaseTypeInfo;
import gw.lang.reflect.IExpando;
import gw.lang.reflect.IMethodInfo;
import gw.lang.reflect.IType;
import gw.lang.reflect.ITypeInfo;
import gw.lang.reflect.MethodInfoBuilder;
import gw.lang.reflect.MethodList;
import gw.lang.reflect.ParameterInfoBuilder;
import gw.lang.reflect.TypeSystem;
import gw.util.GosuExceptionUtil;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.ir.Block;
import jdk.nashorn.internal.ir.Expression;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.IdentNode;
import jdk.nashorn.internal.ir.Statement;
import jdk.nashorn.internal.ir.VarNode;
import jdk.nashorn.internal.parser.Parser;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.ScriptEnvironment;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.options.Options;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavascriptTypeInfo extends BaseTypeInfo implements ITypeInfo
{
  private final ScriptEngine _engine;
  private final MethodList _methods;

  public JavascriptTypeInfo( JavascriptType javascriptType )
  {
    super( javascriptType );
    String source = javascriptType.getSource();
    try
    {
      // init runtime
      // TODO cgross - make lazy
      _engine = new ScriptEngineManager().getEngineByName("nashorn");
      _engine.eval( source );

      // parse for type info
      Parser p = new Parser( new ScriptEnvironment( new Options(""),
                                                    new PrintWriter( System.out ),
                                                    new PrintWriter( System.err ) ),
                             Source.sourceFor(javascriptType.getName(), source),
                             new ErrorManager( new PrintWriter( System.err ) ));
      FunctionNode rootFunction = p.parse();
      _methods = new MethodList();
      Block body = rootFunction.getBody();
      for( Statement statement : body.getStatements() )
      {
        if(statement instanceof VarNode) {
          VarNode var = (VarNode) statement;
          Expression init = var.getInit();
          if( init instanceof FunctionNode )
          {
            String name = var.getName().getPropertyName();
            List<IdentNode> parameters = ((FunctionNode)init).getParameters();
            _methods.add( new MethodInfoBuilder()
                            .withName( name )
                            .withParameters( makeParamList( parameters ) )
                            .withStatic()
                            .withReturnType( getDynamicType() )
                            .withCallHandler( ( ctx, args ) -> {
                              try
                              {
                                Object o = ((Invocable)_engine).invokeFunction( name, args );
                                //o = maybeExpand(o);
                                return o;
                              }
                              catch( Exception e )
                              {
                                throw GosuExceptionUtil.forceThrow( e );
                              }
                            } )
                            .build( this ) );

          }
        }
      }
    }
    catch( ScriptException e )
    {
      throw GosuExceptionUtil.forceThrow( e );
    }
  }

//  private static Object maybeExpand( Object o )
//  {
//    if( o instanceof ScriptObjectMirror )
//    {
//      return new ExpandWrapper( (ScriptObjectMirror)o );
//    }
//    else
//    {
//      return o;
//    }
//  }

  private ParameterInfoBuilder[] makeParamList( List<IdentNode> parameters )
  {
    ParameterInfoBuilder[] parameterInfoBuilders = new ParameterInfoBuilder[parameters.size()];
    for( int i = 0; i < parameterInfoBuilders.length; i++ )
    {
      IdentNode identNode = parameters.get( i );
      parameterInfoBuilders[i] = new ParameterInfoBuilder().withName( identNode.getName() )
        .withDefValue( CommonServices.getGosuIndustrialPark().getNullExpressionInstance() )
        .withType( getDynamicType() );
    }
    return parameterInfoBuilders;
  }

  private IType getDynamicType()
  {
    return TypeSystem.getByFullName( "dynamic.Dynamic" );
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

//  private static class ExpandWrapper implements IExpando
//  {
//    private final ScriptObjectMirror _mirror;
//
//    public ExpandWrapper( ScriptObjectMirror o )
//    {
//      _mirror = o;
//    }
//
//    @Override
//    public Object getFieldValue( String field )
//    {
//      return _mirror.getMember( field );
//    }
//
//    @Override
//    public void setFieldValue( String field, Object value )
//    {
//      _mirror.setMember( field, value );
//    }
//
//    @Override
//    public void setDefaultFieldValue( String field )
//    {
//      // ignore
//    }
//
//    @Override
//    public Object invoke( String methodName, Object... args )
//    {
//      return maybeExpand(_mirror.callMember( methodName, args ));
//    }
//
//    @Override
//    public Map getMap()
//    {
//      HashMap map = new HashMap();
//      for( String ownKey : _mirror.getOwnKeys( false ) )
//      {
//        map.put( ownKey, _mirror.getMember( ownKey ) );
//      }
//      return map;
//    }
//  }
}