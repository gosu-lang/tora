package tora;

import gw.config.CommonServices;
import gw.lang.parser.*;
import gw.util.GosuExceptionUtil;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.script.ScriptException;
import java.util.HashMap;

import static junit.framework.TestCase.assertEquals;

public class TemplateTest
{
  @BeforeClass
  public static void beforeClass() {
    Utils.maybeInit();
  }

  @Test
  public void expressionTemplate() {
    String template = (String)
            eval("return templates.ExpressionTemplate.renderToString('foo', 42)");
    template = template.replaceAll("\r\n", "\n");
    assertEquals(template, "\n" +
            "\n" +
            "\tThis is a template which accepts parameters foo and 42. Can use ${ or <%= for expressions.\n" +
            "\n" +
            "\t1 + 2 + 3 = 6.\n" +
            "\t1 + 2 + 3 = 6.\n" +
            "\n" +
            "\tDerp.\n");
  }

  @Test
  public void statementTemplate() {
    String template = (String)
            eval("return templates.StatementTemplate.renderToString({'carson', 'kyle', 'lucca','marcus'})");
    template = template.replaceAll("\r\n", "\n");
    assertEquals (template,"\n" +
            "\n" +
            "\tThis is a template that uses expressions to loop through array.\n" +
            "\n" +
            "\tContents of parameters: \n" +
            "\t    carson\n" +
            "    \n" +
            "\t    kyle\n" +
            "    \n" +
            "\t    lucca\n" +
            "    \n" +
            "\t    marcus\n" +
            "    ");
  }

  @Test
  public void importTemplate() {
    String template = (String)
            eval("return templates.ImportTemplate.renderToString()");
    template = template.replaceAll("\r\n", "\n");
    assertEquals(template, " \n" +
            "\n" +
            "\tThis is a template that imports the java object Integer.\n" +
            "\n" +
            "    The maximum java int is 2147483647");
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
