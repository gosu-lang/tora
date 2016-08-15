package tora;

import gw.config.CommonServices;
import gw.lang.parser.*;
import gw.util.GosuExceptionUtil;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class StringInterpolationTest
{
  @BeforeClass
  public static void beforeClass() {
    Utils.maybeInit();
  }

  @Test
  public void Basic() {
  assertEquals("5", eval("return InterpolationProgram.basic()"));
}
  @Test
  public void OneLine() {
    assertEquals("5 + 5 is 10", eval("return InterpolationProgram.oneLine()"));
  }

  @Test
  public void MultiLine() {
    String template = (String)
            eval("return InterpolationProgram.multiLine('hello')");

    template = template.replaceAll("\r\n", "\n");
    assertEquals (template,"this is a\n" +
            "\n" +
            "  multiline\n" +
            "\n" +
            "  example\n" +
            "\n" +
            "  of template strings with the argument hello");
  }

  @Test
  public void CurlyInExpression() {
    assertEquals("making sure we don't exit expressions early is good practice", eval("return InterpolationProgram" +
            ".curlyInExpression()"));
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
