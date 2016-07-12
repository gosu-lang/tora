package tora;

/**
 * Created by lmeyer-teruel on 7/7/2016.
 */

import gw.config.CommonServices;
import gw.lang.parser.*;
import gw.lang.reflect.IType;
import gw.lang.reflect.TypeSystem;
import gw.util.GosuExceptionUtil;
import org.junit.BeforeClass;
import org.junit.Test;
import tora.parser.Parser;
import tora.parser.Tokenizer;
import tora.parser.tree.*;

import java.util.HashMap;

import static com.sun.tools.javac.util.Assert.error;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class ExtendTest {
    @BeforeClass
    public static void beforeClass() {
        Utils.maybeInit();
    }

    @Test
    public void bootstrapTypeTest() {
        IType result = TypeSystem.getByFullNameIfValid("ExtendTestClass");
        assertNotNull(result);
        assertEquals("ExtendTestClass", result.getName());
    }

    @Test
    public void extendTest() {
        assertEquals(42, eval("print(new ExtendTestClass())"));
    }

    @Test
    public void extendTestClassCanBeInstantiated() {
        assertEquals(42, eval("var ext = new ExtendTestClass(); return ext.bar();"));
    }


    @Test
    public void extendTestInheritsSuperClassMethods() {
        assertEquals(42, eval("ExtendTestClass.run()"));
    }


//    @Test
//    public void parse(String code) {
//        return new Parser(new Tokenizer(code)).parse().getFirstChild(ClassNode.class);
//    }

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
