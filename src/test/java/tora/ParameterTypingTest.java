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
import static org.junit.Assert.assertTrue;

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
        assertEquals(50.0, eval("var typeClass = new TypingClass(); return typeClass.doubleTest(25.0, 25.0);"));
        try {
            eval("var typeClass = new TypingClass(); return typeClass.doubleTest(\"hi\", \"hi\");");
        } catch (Exception e) {
            succesfullyFails = true;
        }
        assertTrue(succesfullyFails);

        //for programs
        succesfullyFails = false;
        assertEquals(50.0, eval("return TypingProgram.doubleTest(25.0, 25.0);"));
        try {
            eval("return TypingProgram.doubleTest(\"hi\", \"hi\");");
        } catch (Exception e) {
            succesfullyFails = true;
        }
        assertTrue(succesfullyFails);

    }
    @Test
    public void stringTypeTest() {
        Boolean succesfullyFails = false;
        assertEquals(50.0, eval("var typeClass = new TypingClass(); return typeClass.doubleTest(25.0, 25.0);"));
        try {
            eval("var typeClass = new TypingClass(); return typeClass.doubleTest(\"hi\", \"hi\");");
        } catch (Exception e) {
            succesfullyFails = true;
        }
        assertTrue(succesfullyFails);


    }

    @Test
    public void intTypeTest() {
        assertEquals(50, eval("var typeClass = new TypingClass(); return typeClass.intTest(50)"));
        assertEquals(50, eval("return TypingProgram.intTest(50)"));
    }
    @Test
    public void javaTypeTest() {
        //javaClassTest passes an arraylist as a parameter to another function which adds an element, and then counts its length
        assertEquals(1, eval("var typeClass = new TypingClass(); return typeClass.javaClassTest();"));

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
        assertTrue(succesfullyFailsInput);
        assertTrue(succesfullyFailsOutput);

        assertEquals(1, eval("return TypingProgram.javaClassTest();"));

        //test with programs
        succesfullyFailsInput = false;
        succesfullyFailsOutput = false;

        try {
            eval("return TypingProgram.failsWhenPassedWrongType(5);");
        } catch (Exception e) {
            succesfullyFailsInput = true;
        }

        try {
            eval("return TypingProgram.failsWhenReturningNonArrayList();");
        } catch (Exception e) {
            succesfullyFailsOutput = true;
        }
        assertTrue(succesfullyFailsInput);
        assertTrue(succesfullyFailsOutput);

    }
    @Test
    public void gosuTypeTest() {
        //Not yet set up
    }

    @Test
    public void returnTypeTest() {
        Boolean succesfullyFails = false;
        assertEquals(25.0, eval("var typeClass = new TypingClass(); return typeClass.returnsDouble(25);"));
        try {
            eval("var typeClass = new TypingClass(); return typeClass.returnsWrongType(\"hi\");");
        } catch (Exception e) {
            succesfullyFails = true;
        }
        assertTrue(succesfullyFails);
    }

    @Test
    public void coercionJSToJavaTypeTest() {
        assertEquals("168.0", eval("var typeClass = new TypingClass(); return typeClass.doubleToStringCoercionTest(123,45)"));
        assertEquals("168.0", eval("return TypingProgram.doubleToStringCoercionTest(123,45)"));
    }


    @Test
    public void coercionJavaToJSTypeTest() {
        assertEquals(123.0, eval("var typeClass = new TypingClass(); return typeClass.takesInDoubleAndReturns(123)"));
        assertEquals(123.0, eval("return TypingProgram.takesInDoubleAndReturns(123)"));
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
