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

        Assert.assertEquals(TokenType.NUMBER,  list.get(1).getTokenType());

        //Ensures that the regex correctly isolates the number
        Assert.assertTrue(321.543 == list.get(2).getTokenNumberValue());
        Assert.assertEquals(TokenType.OPERATOR,  list.get(3).getTokenType());

        //Tests if the element after is correctly assessed
        Assert.assertEquals(TokenType.NUMBER,  list.get(4).getTokenType());

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
        Tokenizer toke = new Tokenizer("\"Hello\"");
        ArrayList<Tokenizer.Token> list = toke.tokenize();
        Tokenizer.Token token = list.get(0);
        Assert.assertEquals(TokenType.STRING, token.getTokenType());
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
}
