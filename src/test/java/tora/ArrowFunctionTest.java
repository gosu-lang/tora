package tora;

import gw.config.CommonServices;
import gw.lang.parser.*;
import gw.util.GosuExceptionUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.script.ScriptException;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class ArrowFunctionTest
{


  @BeforeClass
  public static void beforeClass() {
    Utils.maybeInit();
  }

  @Test
  public void multipleParamsStatement() throws ScriptException {
   // assertEquals(15.0, eval("return ArrowClass.arrowReduceStatement()"));
    assertEquals(15.0, eval("return ArrowProgram.arrowReduceStatement()"));
  }

  @Test
  public void singleParamStatement() throws ScriptException {
    assertEquals(3, eval("return ArrowClass.arrowFilterStatement()"));
    assertEquals(3, eval("return ArrowProgram.arrowFilterStatement()"));
  }

  @Test
  public void multipleParamsExpression() throws ScriptException {
    assertEquals(15.0, eval("return ArrowClass.arrowReduceExpression()"));
    assertEquals(15.0, eval("return ArrowProgram.arrowReduceExpression()"));
  }

  @Test
  public void singleParamExpression() throws ScriptException {
    assertEquals(3, eval("return ArrowClass.arrowFilterExpression()"));
    assertEquals(3, eval("return ArrowProgram.arrowFilterExpression()"));
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
