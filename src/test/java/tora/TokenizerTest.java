package tora;

/**
 * Created by lmeyer-teruel on 6/21/2016.
 */

import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;

import tora.parser.Tokenizer;
import tora.parser.TokenType;


public class TokenizerTest {


    @Test
    public void testTokenizerBrackets() {
        //This test assesses whether the braces and parentheses are assessed in the correct order
        Tokenizer toke = new Tokenizer("{[()]}");
        ArrayList<Tokenizer.Token> list = toke.tokenize();
        Assert.assertEquals(TokenType.LBRACE, list.get(0).getTokenType());
        Assert.assertEquals(TokenType.LSQUARE, list.get(1).getTokenType());
        Assert.assertEquals(TokenType.LPAREN, list.get(2).getTokenType());
        Assert.assertEquals(TokenType.RPAREN, list.get(3).getTokenType());
        Assert.assertEquals(TokenType.RSQUARE, list.get(4).getTokenType());
        Assert.assertEquals(TokenType.RBRACE, list.get(5).getTokenType());
    }

    @Test
    public void testTokenizerNumbers() {
        Tokenizer toke = new Tokenizer("54321 0x342 321.543.764 0b1111  0x5855 0o1234 01234");
        ArrayList<Tokenizer.Token> list = toke.tokenize();

        Assert.assertEquals(TokenType.NUMBER, list.get(0).getTokenType());
        Assert.assertTrue(54321.0 == list.get(0).getTokenNumberValue());

        Assert.assertEquals(TokenType.NUMBER, list.get(1).getTokenType());

        //Ensures that the regex correctly isolates the number
        Assert.assertTrue(321.543 == list.get(2).getTokenNumberValue());
        Assert.assertEquals(TokenType.OPERATOR, list.get(3).getTokenType());

        //Tests if the element after is correctly assessed
        Assert.assertEquals(TokenType.NUMBER, list.get(4).getTokenType());

        //Test of whether the expressions of the form 0b***** are interpreted as binary
        Assert.assertTrue(list.get(5).getTokenNumberValue() == 15);

        //Test of whether 0x**** is interpreted as hex
        Assert.assertTrue(list.get(6).getTokenNumberValue() == 22613);

        //Test of both 0o***** and 0**** being intepreted as octal
        Assert.assertTrue(list.get(7).getTokenNumberValue() == list.get(8).getTokenNumberValue());

    }

    @Test
    public void testRemovalOfWhiteSpace() {
        Tokenizer toke = new Tokenizer("    {   [   (    )   ]    }");
        ArrayList<Tokenizer.Token> list = toke.tokenize();
        Assert.assertEquals(TokenType.LBRACE, list.get(0).getTokenType());
        Assert.assertEquals(TokenType.LSQUARE, list.get(1).getTokenType());
        Assert.assertEquals(TokenType.LPAREN, list.get(2).getTokenType());
        Assert.assertEquals(TokenType.RPAREN, list.get(3).getTokenType());
        Assert.assertEquals(TokenType.RSQUARE, list.get(4).getTokenType());
        Assert.assertEquals(TokenType.RBRACE, list.get(5).getTokenType());
    }

    @Test
    public void testTokenizerStrings() {
        Tokenizer toke = new Tokenizer("\"Hello \" " + " \" \n \" " + " \" \\0\\b\\f\\r\\t\\v\\'\\321\\xab \\uabcd \\u{abcde} \"");
        ArrayList<Tokenizer.Token> list = toke.tokenize();
        Assert.assertEquals(TokenType.STRING, list.get(0).getTokenType());
        Assert.assertEquals(TokenType.STRING, list.get(1).getTokenType());
        Assert.assertEquals(TokenType.STRING, list.get(2).getTokenType());

    }

    @Test
    //This test will look at the various values that should generate errors instead of strings
    public void testInvalidTokenizerStrings () {
        Tokenizer toke = new Tokenizer("\" \\543 \"" + "\" \\q \" " + " \" \n \" " + "\" \\xak  \"");
        ArrayList<Tokenizer.Token> list = toke.tokenize();
        Assert.assertEquals(TokenType.ERROR, list.get(0).getTokenType());
        Assert.assertEquals(TokenType.ERROR, list.get(1).getTokenType());
        Assert.assertEquals(TokenType.STRING, list.get(2).getTokenType());
        Assert.assertEquals(TokenType.ERROR, list.get(3).getTokenType());
    }

    @Test
    public void testTokenizerProgram() {
        Tokenizer toke = new Tokenizer("54321");
        ArrayList<Tokenizer.Token> list = toke.tokenize();
        Tokenizer.Token token = list.get(0);
        Assert.assertEquals(TokenType.NUMBER, token.getTokenType());
    }

    @Test
    public void testTokenizerSymbols() {
        Tokenizer toke = new Tokenizer("hi9$a eaihafgda:5464");
        ArrayList<Tokenizer.Token> list = toke.tokenize();
        Assert.assertEquals(TokenType.SYMBOL, list.get(0).getTokenType());
        Assert.assertEquals(TokenType.SYMBOL, list.get(1).getTokenType());
        Assert.assertEquals(TokenType.COLON, list.get(2).getTokenType());
        Assert.assertEquals(TokenType.NUMBER, list.get(3).getTokenType());

    }

    @Test
    public void testTokenizerOperators() {
        Tokenizer tokenizer = new Tokenizer("*=");
        ArrayList<Tokenizer.Token> list = tokenizer.tokenize();
        Tokenizer.Token token = list.get(0);
        Assert.assertEquals(TokenType.OPERATOR, token.getTokenType());
    }

    @Test
    // Final test to see if the general functionallity and tokenization occur properly
    public void testTokenizerComplex() {
        Tokenizer tokenizer = new Tokenizer("function hello() {var stuff = 32; var things = 55; return stuff + things; } class");
        ArrayList<Tokenizer.Token> list = tokenizer.tokenize();

        Assert.assertEquals(TokenType.FUNCTION, list.get(0).getTokenType());
        Assert.assertEquals(TokenType.SYMBOL, list.get(1).getTokenType());
        Assert.assertEquals(TokenType.LPAREN, list.get(2).getTokenType());
        Assert.assertEquals(TokenType.RPAREN, list.get(3).getTokenType());
        Assert.assertEquals(TokenType.LBRACE, list.get(4).getTokenType());
        Assert.assertEquals(TokenType.KEYWORD, list.get(5).getTokenType());
        Assert.assertEquals(TokenType.SYMBOL, list.get(6).getTokenType());
        Assert.assertEquals(TokenType.OPERATOR, list.get(7).getTokenType());
        Assert.assertEquals(TokenType.NUMBER, list.get(8).getTokenType());
        Assert.assertEquals(TokenType.SEMICOLON, list.get(9).getTokenType());
        Assert.assertEquals(TokenType.KEYWORD, list.get(10).getTokenType());
        Assert.assertEquals(TokenType.SYMBOL, list.get(11).getTokenType());
        Assert.assertEquals(TokenType.OPERATOR, list.get(12).getTokenType());
        Assert.assertEquals(TokenType.NUMBER, list.get(13).getTokenType());
        Assert.assertEquals(TokenType.SEMICOLON, list.get(14).getTokenType());
        Assert.assertEquals(TokenType.CONTROL, list.get(15).getTokenType());
        Assert.assertEquals(TokenType.SYMBOL, list.get(16).getTokenType());
        Assert.assertEquals(TokenType.OPERATOR, list.get(17).getTokenType());
        Assert.assertEquals(TokenType.SYMBOL, list.get(18).getTokenType());
        Assert.assertEquals(TokenType.SEMICOLON, list.get(19).getTokenType());
        Assert.assertEquals(TokenType.RBRACE, list.get(20).getTokenType());
        Assert.assertEquals(TokenType.CLASS, list.get(21).getTokenType());
    }

    @Test
    public void testLineNumbers() {
        String testString = "hi9$a eaihafgda\n:5464\n     hello";
        Tokenizer tokenizer = new Tokenizer(testString);

        ArrayList<Tokenizer.Token> list = tokenizer.tokenize();
        Assert.assertEquals(1, list.get(0).getLineNumber());
        Assert.assertEquals(1, list.get(0).getColumn());
        Assert.assertEquals(testString.indexOf(list.get(0).getTokenStringValue()) + 1, list.get(0).getOffset());

        Assert.assertEquals(1, list.get(1).getLineNumber());
        Assert.assertEquals(7, list.get(1).getColumn());
        Assert.assertEquals(testString.indexOf(list.get(1).getTokenStringValue()) + 1, list.get(1).getOffset());


        Assert.assertEquals(2, list.get(2).getLineNumber());
        Assert.assertEquals(1, list.get(2).getColumn());
        Assert.assertEquals(testString.indexOf(list.get(2).getTokenStringValue()) + 1, list.get(2).getOffset());

        Assert.assertEquals(3, list.get(4).getLineNumber());
        Assert.assertEquals(6, list.get(4).getColumn());
    }

    @Test
        public void fullClassTokenization() {
        String testString = "class DemoClass {\n" +
                "  constructor() {\n" +
                "    this.foo = 42;\n" +
                "  }\n" +
                "\n" +
                "  bar() {\n" +
                "    return this.foo;\n" +
                "  }\n" +
                "\n" +
                "  get doh() {\n" +
                "    return this.foo;\n" +
                "  }\n" +
                "\n" +
                "  static staticFoo() {\n" +
                "    return 42;\n" +
                "  }\n" +
                "}";
        Tokenizer tokenizer = new Tokenizer(testString);
        ArrayList<Tokenizer.Token> list = tokenizer.tokenize();

        Assert.assertEquals(1, list.get(0).getLineNumber());
        Assert.assertEquals(1, list.get(0).getColumn());
        Assert.assertEquals(TokenType.CLASS, list.get(0).getTokenType());

        Assert.assertEquals(1, list.get(1).getLineNumber());
        Assert.assertEquals(7, list.get(1).getColumn());
        Assert.assertEquals(TokenType.SYMBOL, list.get(1).getTokenType());
        Assert.assertEquals(testString.indexOf(list.get(1).getTokenStringValue()) + 1, list.get(1).getOffset());

        Assert.assertEquals("this", list.get(7).getTokenStringValue());
        Assert.assertEquals(TokenType.KEYWORD, list.get(7).getTokenType());


        Assert.assertEquals("bar", list.get(14).getTokenStringValue());
        Assert.assertEquals(TokenType.SYMBOL, list.get(14).getTokenType());


        Assert.assertEquals("get", list.get(24).getTokenStringValue());
        Assert.assertEquals(TokenType.PROPERTY, list.get(24).getTokenType());

        Assert.assertEquals("static", list.get(35).getTokenStringValue());
        Assert.assertEquals(TokenType.KEYWORD, list.get(35).getTokenType());

        Assert.assertEquals(testString.indexOf(list.get(35).getTokenStringValue()) + 1, list.get(35).getOffset());

    }
}
