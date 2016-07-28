package tora;

import java.util.ArrayList;
import junit.framework.Assert;
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
import gw.lang.parser.exceptions.ParseResultsException;
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
    public void doubleTypeTest() {
        Boolean succesfullyFails = false;
        Assert.assertEquals(50.0, eval("var typeClass = new TypingClass(); return typeClass.doubleTest(25.0, 25.0);"));
        try {
            eval("var typeClass = new TypingClass(); return typeClass.doubleTest(\"hi\", \"hi\");");
        } catch (Exception e) {
            succesfullyFails = true;
        }
        Assert.assertTrue(succesfullyFails);

    }
    @Test
    public void stringTypeTest() {
        Boolean succesfullyFails = false;
        Assert.assertEquals(50.0, eval("var typeClass = new TypingClass(); return typeClass.doubleTest(25.0, 25.0);"));
        try {
            eval("var typeClass = new TypingClass(); return typeClass.doubleTest(\"hi\", \"hi\");");
        } catch (Exception e) {
            succesfullyFails = true;
        }
        Assert.assertTrue(succesfullyFails);
    }
    @Test
    public void javaTypeTest() {
        //javaClassTest passes an arraylist as a parameter to another function which adds an element, and then counts its length
        Assert.assertEquals(1, eval("var typeClass = new TypingClass(); return typeClass.javaClassTest();"));

        Boolean succesfullyFailsInput = false;
        Boolean succesfullyFailsOutput = false;

        try {
            eval("var typeClass = new TypingClass(); return typeClass.failsWhenPassedWrongType(5);");
        } catch (Exception e) {
            succesfullyFailsInput = true;
        }

        try {
            eval("var typeClass = new TypingClass(); return typeClass.failsWhenReturningNonArrayList();");
        } catch (Exception e) {
            succesfullyFailsOutput = true;
        }
        Assert.assertTrue(succesfullyFailsInput);
        Assert.assertTrue(succesfullyFailsOutput);

    }
    @Test
    public void gosuTypeTest() {
        //Not yet set up
    }

    @Test
    public void returnTypeTest() {
        Boolean succesfullyFails = false;
        Assert.assertEquals(25.0, eval("var typeClass = new TypingClass(); return typeClass.returnsDouble(25.0);"));
        try {
            eval("var typeClass = new TypingClass(); return typeClass.returnsWrongType(\"hi\");");
        } catch (Exception e) {
            succesfullyFails = true;
        }
        Assert.assertTrue(succesfullyFails);
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
