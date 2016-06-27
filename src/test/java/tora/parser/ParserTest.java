package tora.parser;

/**
 * Created by lmeyer-teruel on 6/27/2016.
 */

import org.junit.Assert;
import org.junit.Test;

public class ParserTest {
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

}













