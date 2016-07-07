package tora;

import gw.config.CommonServices;
import gw.lang.parser.*;
import gw.util.GosuExceptionUtil;
import org.junit.BeforeClass;
import org.junit.Test;


import javax.script.ScriptException;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class ImportingJavaClassTest
{

  public static class JavaClass {
    public static int staticFoo() {
      return 42;
    }

    public String bar() {
      return "bar";
    }
  }

  @BeforeClass
  public static void beforeClass() {
    Utils.maybeInit();
  }

  @Test
  public void testSimpleClassNode() throws ScriptException {
    assertEquals(42, eval("return ImportClass.foo()"));
    assertEquals("bar", eval("var imported = new ImportClass(); return imported.bar()"));
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
