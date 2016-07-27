package tora;

import gw.config.CommonServices;
import gw.lang.parser.ExternalSymbolMapForMap;
import gw.lang.parser.IGosuProgramParser;
import gw.lang.parser.IParseResult;
import gw.lang.parser.ParserOptions;
import gw.lang.parser.StandardSymbolTable;
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

    public String returnHello() {
      return "hello";
    }
  }

  @BeforeClass
  public static void beforeClass() {
    Utils.maybeInit();
  }

  @Test
  public void testJavaClassImport() throws ScriptException {
    assertEquals(42, eval("return ImportClass.javaFoo()"));
    assertEquals("hello", eval("var imported = new ImportClass(); return imported.javaBar()"));
  }

  @Test
  public void testGosuClassImport() throws ScriptException {
    assertEquals(43, eval("return ImportClass.gosuFoo()"));
    assertEquals(70, eval("var imported = new ImportClass(); return imported.gosuDouble (35)"));
  }

  @Test
  public void testImportsInJavascriptProgram() throws ScriptException {
    assertEquals(43, eval("return ImportProgram.gosuFoo()"));
    assertEquals(42, eval("return ImportProgram.javaFoo()"));
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
