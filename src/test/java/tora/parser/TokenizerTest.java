package tora.parser;


import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class TokenizerTest
{
  @Test
  public void bootstrapTest()
  {
    List<Tokenizer.Token> num = tokenize("123");
    assertTokensEq( num, t(TokenType.NUMBER, "123" ) );

    //Whitespace tests
    assertTokensEq(tokenize(" 123   \n456  "), t(TokenType.NUMBER, "123"), t(TokenType.NUMBER, "456"));

    //Two tokens
    List<Tokenizer.Token> twoNum = tokenize("123  456");
    assertTokensEq( twoNum, t( TokenType.NUMBER, "123" ), t( TokenType.NUMBER, "456" ) );
  }

  @Test
  public void simpleKeywordTest()
  {
    assertTokensEq(tokenize("for"), t(TokenType.KEYWORD, "for"));
  }

  @Test
  public void simpleIdentifierTest()
  {
    assertTokensEq(tokenize("carson"),(t(TokenType.IDENTIFIER, "carson")));
  }

  @Test
  public void simpleNullTest()
  {
    assertTokensEq(tokenize("null"),t(TokenType.NULL, "null"));
  }

  @Test
  public void simpleBooleanTest()
  {
    assertTokensEq(tokenize("true"),t(TokenType.BOOLEAN, "true"));
  }

  @Test
  public void simplePunctationTest()
  {
    assertTokensEq(tokenize(","),t(TokenType.PUNCTUATION, ","));
    assertTokensEq(tokenize("()"),t(TokenType.PUNCTUATION, "("), t(TokenType.PUNCTUATION, ")"));
    assertTokensEq(tokenize("."),t(TokenType.PUNCTUATION, "."));
  }

  @Test
  public void simpleOperatorTest()
  {
    assertTokensEq(tokenize("*"),t(TokenType.OPERATOR, "*"));
    assertTokensEq(tokenize("/"),t(TokenType.OPERATOR, "/"));
    assertTokensEq(tokenize(">="),t(TokenType.OPERATOR, ">="));
    //Error; should be caught as unexpected operator by parser
    assertTokensEq(tokenize(">=<"),t(TokenType.OPERATOR, ">="), t(TokenType.OPERATOR, "<"));
  }

  @Test
  public void simpleCommentTest()
  {
    assertTokensEq(tokenize("//donald"),t(TokenType.COMMENT, "//donald"));
    assertTokensEq(tokenize("/*insurance is fun*/"),t(TokenType.COMMENT, "/*insurance is fun*/"));
    assertTokensEq(tokenize("x+/*comments are fun*/5"),
            t(TokenType.IDENTIFIER, "x"),
            t(TokenType.OPERATOR, "+"),
            t(TokenType.COMMENT, "/*comments are fun*/"),
            t(TokenType.NUMBER, "5"));

  }

  @Test
  public void numberTest()
  {
    //Integer
    assertTokensEq(tokenize("12345"), t(TokenType.NUMBER, "12345"));
    //Decimal
    assertTokensEq(tokenize("123.45"), t(TokenType.NUMBER, "123.45"));
    //Hex
    assertTokensEq(tokenize("0x123abc"), t(TokenType.NUMBER, "0x123abc"));
    //Octal
    assertTokensEq(tokenize("0O1342"), t(TokenType.NUMBER, "0O1342"));
    //Implied Octal
    assertTokensEq(tokenize("01323"),t(TokenType.NUMBER, "01323"));
    //Implied Octal turned Dec
    assertTokensEq(tokenize("0778.4"),t(TokenType.NUMBER, "0778.4"));
    //Exponential
    assertTokensEq(tokenize("123e4"),t(TokenType.NUMBER, "123e4"));
    assertTokensEq(tokenize("123e+4"),t(TokenType.NUMBER, "123e+4"));
    assertTokensEq(tokenize("12.3e-4"),t(TokenType.NUMBER, "12.3e-4"));
    //Binary
    assertTokensEq(tokenize("0b1011"),t(TokenType.NUMBER, "0b1011"));

    /*Errors*/
    //Multiple decimal points; should be caught as unexpected number during parsing
    assertTokensEq(tokenize("123.456.789"),t(TokenType.NUMBER, "123.456"), t(TokenType.NUMBER, ".789"));
    //Decimal point at end; should be caught as unexpected punctuation (or end of input?) by parser
    assertTokensEq(tokenize("123.456."),t(TokenType.NUMBER, "123.456"), t(TokenType.PUNCTUATION, "."));
    //Integer with hex numbers; should be caught as unexpected identifier by parser
    assertTokensEq(tokenize("123abc"), t(TokenType.NUMBER, "123"), t(TokenType.IDENTIFIER, "abc"));
    //Binary with non 0-1 digits; should be caught as unexpected number by parser
    assertTokensEq(tokenize("0b101234"), t(TokenType.NUMBER, "0b101"),t(TokenType.NUMBER, "234"));
    //Octal with decimal point; should be caught as unexpected number
    assertTokensEq(tokenize("0134.2"), t(TokenType.NUMBER, "0134"), t(TokenType.NUMBER, ".2"));
    //Exponent with decimal point; should be caught as unexpected number
    assertTokensEq(tokenize("5e5.5"), t(TokenType.NUMBER, "5e5"), t(TokenType.NUMBER, ".5"));
    //Marked as binary, with no value following
    assertTokensEq(tokenize("0b"), t(TokenType.ERROR, "illegal number token"));
  }

  @Test
  public void stringEscapeTest()
  {
    //Double quotes
    assertTokensEq(tokenize("\"Carson Gross\""), t(TokenType.STRING, "\"Carson Gross\""));
    //Single quotes
    assertTokensEq(tokenize("'Carson Gross'"), t(TokenType.STRING, "'Carson Gross'"));
    //Single quotes inside double quotes
    assertTokensEq(tokenize("\"Carson Gross is 'nice'\""), t(TokenType.STRING, "\"Carson Gross is 'nice'\""));
    //Double quotes inside single quotes
    assertTokensEq(tokenize("'Carson Gross is \"nice\"'"),t(TokenType.STRING, "'Carson Gross is \"nice\"'"));
    //Escaped Single quotes
    assertTokensEq(tokenize("'Carson Gross is \\'nice\\''"),t(TokenType.STRING, "'Carson Gross is \\'nice\\''"));
    //Escaped Double quotes
    assertTokensEq(tokenize("\"Carson is \\\"nice\\\"\""),t(TokenType.STRING, "\"Carson is \\\"nice\\\"\""));

  }

  @Test
  public void stringErrorsTest()
  {
    //Unterminated quote
    assertTokensEq(tokenize("\"Linux"), t(TokenType.ERROR, "unterminated string"));
    //Unescaped new line
    assertTokensEq(tokenize("'Lin\n"), t(TokenType.ERROR, "newline character in string"));

  }

  @Test
  public void exampleJSFile()
  {
    try {
      URL url = getClass().getResource("/bootstrap.js");
      assertTokensEq(tokenize(new BufferedReader(new FileReader(url.getFile()))),
        t(TokenType.KEYWORD, "function"),
        t(TokenType.IDENTIFIER, "foo"),
        t(TokenType.PUNCTUATION, "("),
        t(TokenType.PUNCTUATION, ")"),
        t(TokenType.PUNCTUATION, "{"),
        t(TokenType.KEYWORD, "return"),
        t(TokenType.STRING, "\"bar\""),
        t(TokenType.PUNCTUATION, "}"),
        t(TokenType.KEYWORD, "function"),
        t(TokenType.IDENTIFIER, "identity"),
        t(TokenType.PUNCTUATION, "("),
        t(TokenType.IDENTIFIER, "x"),
        t(TokenType.PUNCTUATION, ")"),
        t(TokenType.PUNCTUATION, "{"),
        t(TokenType.KEYWORD, "return"),
        t(TokenType.IDENTIFIER, "x"),
        t(TokenType.PUNCTUATION, "}"),
        t(TokenType.KEYWORD, "function"),
        t(TokenType.IDENTIFIER, "returnsJavascriptObject"),
        t(TokenType.PUNCTUATION, "("),
        t(TokenType.IDENTIFIER, "arg"),
        t(TokenType.PUNCTUATION, ")"),
        t(TokenType.PUNCTUATION, "{"),
        t(TokenType.KEYWORD, "var"),
        t(TokenType.IDENTIFIER, "x"),
        t(TokenType.OPERATOR, "="),
        t(TokenType.NUMBER, "10"),
        t(TokenType.PUNCTUATION, ";"),
        t(TokenType.KEYWORD, "var"),
        t(TokenType.IDENTIFIER, "y"),
        t(TokenType.OPERATOR, "="),
        t(TokenType.KEYWORD, "function"),
        t(TokenType.PUNCTUATION, "("),
        t(TokenType.PUNCTUATION, ")"),
        t(TokenType.PUNCTUATION, "{"),
        t(TokenType.KEYWORD, "return"),
        t(TokenType.NUMBER, "20"),
        t(TokenType.PUNCTUATION, "}"),
        t(TokenType.KEYWORD, "return"),
        t(TokenType.PUNCTUATION, "{"),
        t(TokenType.STRING, "\"x\""),
        t(TokenType.OPERATOR, ":"),
        t(TokenType.IDENTIFIER, "x"),
        t(TokenType.PUNCTUATION, ","),
        t(TokenType.STRING, "\"y\""),
        t(TokenType.OPERATOR, ":"),
        t(TokenType.IDENTIFIER, "y"),
        t(TokenType.PUNCTUATION, ","),
        t(TokenType.STRING, "\"arg\""),
        t(TokenType.OPERATOR, ":"),
        t(TokenType.IDENTIFIER, "arg"),
        t(TokenType.PUNCTUATION, "}"),
        t(TokenType.PUNCTUATION, "}"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }



  //========================================================================================
  // Test Helpers
  //========================================================================================
  private List<Tokenizer.Token> tokenize(String text) {
    Tokenizer tokenizer = new Tokenizer(text);
    return tokenizer.tokenize();
  }


  private List<Tokenizer.Token> tokenize(BufferedReader reader) {
    Tokenizer tokenizer = new Tokenizer(reader);
    return tokenizer.tokenize();
  }

  private void assertTokensEq( List<Tokenizer.Token> actual, Tokenizer.Token... expected )
  {
    Assert.assertEquals(Arrays.asList( expected ), actual );
  }

  private Tokenizer.Token t( TokenType type, String val )
  {
    return new Tokenizer.Token( type, val );
  }

}
