package tora;


import tora.parser.*;
import tora.parser.tree.*;
import gw.config.CommonServices;
import gw.lang.parser.*;
import gw.lang.reflect.IType;
import gw.lang.reflect.TypeSystem;
import gw.util.GosuExceptionUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by lmeyer-teruel on 7/27/2016.
 */

public class ParameterTypingTest
{
    @BeforeClass
    public static void beforeClass() {
        Utils.maybeInit();
    }

    @Test
    public void basicEval()
    {
        String myStr = "\n" +
                "function testTypes(x: string, y: int, z: int) {\n" +
                "  var b = y + z;\n" +
                "  return x + b;\n" +
                "}\n";
        ProgramNode evalNode = parse(myStr);
        evalNode.genCode();
    }

    @Test
    public void typeLoaderTest() {
        eval("var thing = new TypingClass(); return thing.testTypes(\"Hello\",1.0,2.0)");
    }

    @Test
    public void integerTypeTest() {

    }


    private ProgramNode parse(String code) {
        return new Parser(new Tokenizer(code)).parse();
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
