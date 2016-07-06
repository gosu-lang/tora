package tora;

import gw.util.GosuExceptionUtil;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JS
{

  public static Object eval(String str) {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName( "nashorn" );
    try
    {
      return engine.eval(str);
    }
    catch( ScriptException e )
    {
      throw GosuExceptionUtil.forceThrow( e );
    }
  }

  public static <R> JSScript0<R> buildScript( String src, Class<R> returnType)
  {
    return new JSScript0<R>( src );
  }

  public static <T> Param<T> param( Class<T> type, String name)
  {
    return new Param<>( type, name );
  }

  public static <R, A1> JSScript1<R, A1> buildScript( String src, Class<R> returnType, Param<A1> p1)
  {
    return new JSScript1<>(src, p1);
  }

  public static <R, A1, A2> JSScript2<R, A1, A2> buildScript( String src, Class<R> returnType, Param<A1> p1, Param<A2> p2)
  {
    return new JSScript2<>(src, p1, p2);
  }

  //================================================================
  //
  //================================================================

  public static class JSScript0<R>
  {
    private final String _src;

    public JSScript0( String src )
    {
      _src = src;
    }

    public R eval() {
      return (R) JS.eval( _src );
    }
  }

  public static class JSScript1<T, A1>
  {
    private final String _src;
    private final Param<A1> _p1;

    public JSScript1( String src, Param<A1> p1 )
    {
      _src = src;
      _p1 = p1;
    }

    public T eval( A1 arg )
    {
      Object result;
      ScriptEngine engine = new ScriptEngineManager().getEngineByName( "nashorn" );
      try
      {
        Bindings bindings = engine.getBindings( ScriptContext.ENGINE_SCOPE );
        bindings.put( _p1.getName(), arg );
        result = engine.eval( _src );
      }
      catch( ScriptException e )
      {
        throw GosuExceptionUtil.forceThrow( e );
      }
      return (T)result;
    }
  }

  public static class JSScript2<T, A1, A2>
  {
    private final String _src;
    private final Param<A1> _p1;
    private final Param<A2> _p2;

    public JSScript2( String src, Param<A1> p1, Param<A2> p2 )
    {
      _src = src;
      _p1 = p1;
      _p2 = p2;
    }

    public T eval( A1 arg, A2 arg2 )
    {
      Object result;
      ScriptEngine engine = new ScriptEngineManager().getEngineByName( "nashorn" );
      try
      {
        Bindings bindings = engine.getBindings( ScriptContext.ENGINE_SCOPE );
        bindings.put( _p1.getName(), arg );
        bindings.put( _p2.getName(), arg2 );
        result = engine.eval( _src );
      }
      catch( ScriptException e )
      {
        throw GosuExceptionUtil.forceThrow( e );
      }
      return (T)result;
    }
  }

  public static class Param<T> {
    private final String _name;
    private final Class<T> _type;
    public Param( Class<T> type, String name )
    {
      _type = type;
      _name = name;
    }

    public String getName()
    {
      return _name;
    }
  }

}
