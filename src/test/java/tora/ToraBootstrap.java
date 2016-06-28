package tora;

import gw.config.CommonServices;
import gw.lang.Gosu;
import gw.lang.parser.ExternalSymbolMapForMap;
import gw.lang.parser.IGosuProgramParser;
import gw.lang.parser.IParseResult;
import gw.lang.parser.ParserOptions;
import gw.lang.parser.StandardSymbolTable;
import gw.lang.reflect.IType;
import gw.lang.reflect.ITypeLoader;
import gw.lang.reflect.TypeSystem;
import gw.util.GosuExceptionUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import tora.plugin.JavascriptPlugin;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class ToraBootstrap
{
  @BeforeClass
  public static void beforeClass() {
    Utils.maybeInit();
  }

  @Test
  public void bootstrapTypeTest() {
    IType result = TypeSystem.getByFullNameIfValid("bootstrap");
    assertNotNull(result);
    assertEquals("bootstrap", result.getName());
  }

  @Test
  public void bootstrapMethodTest() {
    assertEquals("bar", eval("bootstrap.foo()"));
  }

  @Test
  public void bootstrapMethodTest2() {
    assertEquals(3, eval("var str = bootstrap.identity('foo'); return str.length()"));
  }

  @Test
  public void bootstrapMethodTestDefaultVals() {
    assertEquals(null, eval("bootstrap.identity()"));
  }

  @Test
  public void bootstrapPackageResolution() {
    assertEquals( "bar", eval( "example.example.foo()" ) );
  }

  @Test
  public void bootstrapJavascriptExpando() {
    assertEquals(10, eval("bootstrap.returnsJavascriptObject(10).x"));
  }

  @Test
  public void bootstrapJavascriptExpando2() {
    assertEquals(20, eval("bootstrap.returnsJavascriptObject(10).y()"));
  }

  @Test
  public void bootstrapJavascriptExpando3() {
    assertEquals(30, eval("bootstrap.returnsJavascriptObject(30).arg"));
  }

  private Object eval( String program )
  {
    IGosuProgramParser programParser = CommonServices.getGosuParserFactory().createProgramParser();
    try
    {
      IParseResult iParseResult = programParser.parseExpressionOrProgram( program, new StandardSymbolTable( true ), new ParserOptions() );
      return iParseResult.getProgram().evaluate( new ExternalSymbolMapForMap( new HashMap<>() ) );
    }
    catch( Exception e )
    {
      throw GosuExceptionUtil.forceThrow( e );
    }
  }

}
