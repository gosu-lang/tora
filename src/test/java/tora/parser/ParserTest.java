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
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;

public class ParserTest {
    private static ScriptEngine engine;

    @BeforeClass
    public static void beforeClass() {
        engine = new ScriptEngineManager().getEngineByName("nashorn");
    }


    String Edward = "fuck you stanford";
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
        Assert.assertEquals(Edward, "fuck you stanford");
    }

    /*Runs code through tokenizer, parser, and codegen; uses Nashorn to verify results*/
    @Test
    public void endTest() throws ScriptException, FileNotFoundException {
        URL url = getClass().getResource("/DemoClass.js");
        Tokenizer tokenizer = new Tokenizer(new BufferedReader(new FileReader(url.getFile())));
        String genCode = new Parser(tokenizer).parse().genCode();
        System.out.println(genCode);
        engine.eval(genCode);
        engine.eval("var dem = new DemoClass(10,30,2);");
        //Test constructor
        Assert.assertEquals(42, engine.eval("dem.foo"));
        //Test function and static function
        Assert.assertEquals(42, engine.eval("dem.bar()"));
        Assert.assertEquals(42l, engine.eval("dem.sum(22,20)"));
        Assert.assertEquals(42, engine.eval("DemoClass.staticFoo()"));
        //Test properties
        Assert.assertEquals(21,engine.eval("dem.doh"));
        engine.eval("dem.doh = 80");
        Assert.assertEquals(80,engine.eval("dem.doh"));
        Assert.assertEquals(84,engine.eval("dem.poh"));
    }

}













