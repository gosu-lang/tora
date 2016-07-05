package tora.parser;

/**
 * Created by lmeyer-teruel on 6/27/2016.
 */

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import tora.parser.tree.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.List;

import static com.sun.tools.javac.util.Assert.error;
import static org.junit.Assert.assertEquals;

public class ParserTest {
    private static ScriptEngine engine;

    @BeforeClass
    public static void beforeClass() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");
    }


    @Test
    public void parseBasicTest() {
        String testString = "class DemoClass {\n  constructor() {\n    this.foo = 42;\n  }\n" +
                    "\n  bar() {\n    return this.foo;\n  }\n\n" +
                    "  get doh() {\n    return this.foo;\n  }\n" +
                    "\n  static staticFoo() {\n    return 42;\n" +
                    "  }\n}";
        Tokenizer tokenizer = new Tokenizer(testString);
        Parser parser = new Parser(tokenizer);
        parser.parse();
    }

    @Test
    public void parseEmptyClass() {
        ClassNode tree = parse("class DemoClass{}");
        assertEquals(tree.getName(), "DemoClass");
    }

    @Test
    public void parseSimpleConstructor() {
        ClassNode tree = parse("class DemoClass{ constructor(){} }");
        assertHasChildren(tree, new ConstructorNode("DemoClass"));
    }

    @Test
    public void parseSimpleFunction() {
        ClassNode tree = parse("class DemoClass{ bar(){} }");
        assertHasChildren(tree, new FunctionNode("bar", "DemoClass", false));
    }

    @Test
    public void parseSimpleStaticFunction() {
        ClassNode tree = parse("class DemoClass{ static bar(){} }");
        assertHasChildren(tree, new FunctionNode("bar", "DemoClass", true));
    }

    @Test
    public void parseSimpleProperty() {
        ClassNode tree = parse("class DemoClass{ get foo(){} set foo(){}}");
        assertHasChildren(tree,
                new PropertyNode("foo", "DemoClass", "", false, false),
                new PropertyNode("foo", "DemoClass", "", false, true)
                );
    }

    @Test
    public void parseSimpleStaticProperty() {
        ClassNode tree = parse("class DemoClass{ static get foo(){} static set foo(){}}");
        assertHasChildren(tree,
                new PropertyNode("foo", "DemoClass", "", true, false),
                new PropertyNode("foo", "DemoClass", "", true, true)
        );
    }

    //@Test
    //public void parseArgsError() {
    //    ClassNode tree = parse("class DemoClass { bar(a,){} }");
    //    tree = parse("class DemoClass { bar(,){} }");
    //    tree = parse("class DemoClass { bar(a b){} }");
    //}

    /*Runs code through tokenizer, parser, and codegen; uses Nashorn to verify results*/
    @Test
    public void endTest() throws ScriptException, FileNotFoundException {
        URL url = getClass().getResource("/DemoClass.js");
        String genCode = parse(new BufferedReader(new FileReader(url.getFile()))).genCode();
        engine.eval(genCode);
        engine.eval("var dem = new DemoClass(10,30,2);");
        //Test constructor
        assertEquals(42, engine.eval("dem.foo"));
        //Test function and static function
        assertEquals(42, engine.eval("dem.bar()"));
        assertEquals(42l, engine.eval("dem.sum(22,20)"));
        assertEquals(42, engine.eval("DemoClass.staticFoo()"));
        //Test properties
        assertEquals(21,engine.eval("dem.doh"));
        engine.eval("dem.doh = 80");
        assertEquals(80,engine.eval("dem.doh"));
        assertEquals(84,engine.eval("dem.poh"));
        //Test static properties
        engine.eval("DemoClass.staticPoh = 40");
        assertEquals(40, engine.eval("DemoClass.staticPoh"));
    }

    //========================================================================================
    // Test Helpers
    //========================================================================================

    private ClassNode parse(String code) {
        return new Parser(new Tokenizer(code)).parse();
    }

    private ClassNode parse(BufferedReader code) {
        return new Parser(new Tokenizer(code)).parse();
    }


    private void assertHasChildren(ClassNode parent, Node... expectedChildren) {
        List<Node> children = parent.getChildren();
        if (children.size() != expectedChildren.length) error("incorrect number of child nodes");
        for (int i = 0; i < children.size(); i++) {
            assertEquals(children.get(i), expectedChildren[i]);
        }
    }
}













