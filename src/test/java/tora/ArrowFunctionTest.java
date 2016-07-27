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
    assertEquals(15.0, eval("return ArrowClass.arrowReduceStatement()"));
  }

  @Test
  public void singleParamStatement() throws ScriptException {
    Object object = eval("return ArrowClass.arrowFilterStatement()");
  }

  @Test
  public void multipleParamsExpression() throws ScriptException {
    assertEquals(15.0, eval("return ArrowClass.arrowReduceExpression()"));
  }

  @Test
  public void singleParamExpression() throws ScriptException {
    Object object = eval("return ArrowClass.arrowFilterExpression()");
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
