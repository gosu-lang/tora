package tora.parser;

/**
 * Created by lmeyer-teruel on 6/27/2016.
 */

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ParserTest {
    private static ScriptEngine engine;

    @BeforeClass
    public static void beforeClass() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");
    }


    String Edward = "shit";
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
        Assert.assertEquals(Edward, "shit");
    }

    @Test
    public void parseConstructor() throws ScriptException {
        String testCode =
                "class DemoClass { constructor(a, b, c) {this.foo = a + b + c}   " +
                "static staticFoo() { return 42; } " +
                "bar() { return this.foo; } " +
                "}";
        String genCode = new Parser(new Tokenizer(testCode)).parse().genCode();
        System.out.println(genCode);
        engine.eval(genCode);
        engine.eval("var dem = new DemoClass(10,30,2);");
        Assert.assertEquals(42.0, engine.eval("dem.foo"));
        Assert.assertEquals(42.0, engine.eval("dem.bar()"));
        Assert.assertEquals(42, engine.eval("DemoClass.staticFoo()"));
    }

}













