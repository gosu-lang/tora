package tora.parser;


import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


public class TokenizerTest
{
  @Test
  public void bootstrapTest()
  {
    List<Tokenizer.Token> num = tokenize("123");
    assertTokenTypesEq( num, t(TokenType.NUMBER, "123" ) );

    //Whitespace tests
    assertTokenTypesEq(tokenize(" 123   \n456  "), t(TokenType.NUMBER, "123"), t(TokenType.NUMBER, "456"));

    //Two tokens
    List<Tokenizer.Token> twoNum = tokenize("123  456");
    assertTokenTypesEq( twoNum, t( TokenType.NUMBER, "123" ), t( TokenType.NUMBER, "456" ) );
  }

  @Test
  public void simpleKeywordTest()
  {
    assertTokenTypesEq(tokenize("for"), t(TokenType.KEYWORD, "for"));
  }

  @Test
  public void simpleIdentifierTest()
  {
    assertTokenTypesEq(tokenize("carson"),(t(TokenType.IDENTIFIER, "carson")));
  }

  @Test
  public void simpleNullTest()
  {
    assertTokenTypesEq(tokenize("null"),t(TokenType.NULL, "null"));
  }

  @Test
  public void simpleBooleanTest()
  {
    assertTokenTypesEq(tokenize("true"),t(TokenType.BOOLEAN, "true"));
  }

  @Test
  public void simplePunctationTest()
  {
    assertTokenTypesEq(tokenize(","),t(TokenType.PUNCTUATION, ","));
    assertTokenTypesEq(tokenize("()"),t(TokenType.PUNCTUATION, "("), t(TokenType.PUNCTUATION, ")"));
    assertTokenTypesEq(tokenize("."),t(TokenType.PUNCTUATION, "."));
  }

  @Test
  public void simpleOperatorTest()
  {
    assertTokenTypesEq(tokenize("*"),t(TokenType.OPERATOR, "*"));
    assertTokenTypesEq(tokenize("/"),t(TokenType.OPERATOR, "/"));
    assertTokenTypesEq(tokenize(">="),t(TokenType.OPERATOR, ">="));
    //Error; should be caught as unexpected operator by parser
    assertTokenTypesEq(tokenize(">=<"),t(TokenType.OPERATOR, ">="), t(TokenType.OPERATOR, "<"));
  }

  @Test
  public void simpleCommentTest()
  {
    assertTokenTypesEq(tokenize("//donald"),t(TokenType.COMMENT, "//donald"));
    assertTokenTypesEq(tokenize("/*insurance is fun*/"),t(TokenType.COMMENT, "/*insurance is fun*/"));
    assertTokenTypesEq(tokenize("x+/*comments are fun*/5"),
            t(TokenType.IDENTIFIER, "x"),
            t(TokenType.OPERATOR, "+"),
            t(TokenType.COMMENT, "/*comments are fun*/"),
            t(TokenType.NUMBER, "5"));

  }

  @Test
  public void numberTest()
  {
    //Integer
    assertTokenTypesEq(tokenize("12345"), t(TokenType.NUMBER, "12345"));
    //Decimal
    assertTokenTypesEq(tokenize("123.45"), t(TokenType.NUMBER, "123.45"));
    //Hex
    assertTokenTypesEq(tokenize("0x123abc"), t(TokenType.NUMBER, "0x123abc"));
    //Octal
    assertTokenTypesEq(tokenize("0O1342"), t(TokenType.NUMBER, "0O1342"));
    //Implied Octal
    assertTokenTypesEq(tokenize("01323"),t(TokenType.NUMBER, "01323"));
    //Implied Octal turned Dec
    assertTokenTypesEq(tokenize("0778.4"),t(TokenType.NUMBER, "0778.4"));
    //Exponential
    assertTokenTypesEq(tokenize("123e4"),t(TokenType.NUMBER, "123e4"));
    assertTokenTypesEq(tokenize("123e+4"),t(TokenType.NUMBER, "123e+4"));
    assertTokenTypesEq(tokenize("12.3e-4"),t(TokenType.NUMBER, "12.3e-4"));
    //Binary
    assertTokenTypesEq(tokenize("0b1011"),t(TokenType.NUMBER, "0b1011"));

    /*Errors*/
    //Multiple decimal points; should be caught as unexpected number during parsing
    assertTokenTypesEq(tokenize("123.456.789"),t(TokenType.NUMBER, "123.456"), t(TokenType.NUMBER, ".789"));
    //Decimal point at end; should be caught as unexpected punctuation (or end of input?) by parser
    assertTokenTypesEq(tokenize("123.456."),t(TokenType.NUMBER, "123.456"), t(TokenType.PUNCTUATION, "."));
    //Integer with hex numbers; should be caught as unexpected identifier by parser
    assertTokenTypesEq(tokenize("123abc"), t(TokenType.NUMBER, "123"), t(TokenType.IDENTIFIER, "abc"));
    //Binary with non 0-1 digits; should be caught as unexpected number by parser
    assertTokenTypesEq(tokenize("0b101234"), t(TokenType.NUMBER, "0b101"),t(TokenType.NUMBER, "234"));
    //Octal with decimal point; should be caught as unexpected number
    assertTokenTypesEq(tokenize("0134.2"), t(TokenType.NUMBER, "0134"), t(TokenType.NUMBER, ".2"));
    //Exponent with decimal point; should be caught as unexpected number
    assertTokenTypesEq(tokenize("5e5.5"), t(TokenType.NUMBER, "5e5"), t(TokenType.NUMBER, ".5"));
    //Marked as binary, with no value following
    assertTokenTypesEq(tokenize("0b"), t(TokenType.ERROR, "illegal number token"));
  }

  @Test
  public void stringEscapeTest()
  {
    //Double quotes
    assertTokenTypesEq(tokenize("\"Carson Gross\""), t(TokenType.STRING, "\"Carson Gross\""));
    //Single quotes
    assertTokenTypesEq(tokenize("'Carson Gross'"), t(TokenType.STRING, "'Carson Gross'"));
    //Single quotes inside double quotes
    assertTokenTypesEq(tokenize("\"Carson Gross is 'nice'\""), t(TokenType.STRING, "\"Carson Gross is 'nice'\""));
    //Double quotes inside single quotes
    assertTokenTypesEq(tokenize("'Carson Gross is \"nice\"'"),t(TokenType.STRING, "'Carson Gross is \"nice\"'"));
    //Escaped Single quotes
    assertTokenTypesEq(tokenize("'Carson Gross is \\'nice\\''"),t(TokenType.STRING, "'Carson Gross is \\'nice\\''"));
    //Escaped Double quotes
    assertTokenTypesEq(tokenize("\"Carson is \\\"nice\\\"\""),t(TokenType.STRING, "\"Carson is \\\"nice\\\"\""));

  }

  @Test
  public void stringErrorsTest()
  {
    //Unterminated quote
    assertTokenTypesEq(tokenize("\"Linux"), t(TokenType.ERROR, "unterminated string"));
    //Unescaped new line
    assertTokenTypesEq(tokenize("'Lin\n"), t(TokenType.ERROR, "newline character in string"));

  }

  //Test bootstrap.js with line numbers
  @Test
  public void exampleJSFile()
  {
    try {
      URL url = getClass().getResource("/bootstrap.js");
      assertTokensEq(tokenize(new BufferedReader(new FileReader(url.getFile()))),
        t(TokenType.KEYWORD, "function",1,1,1),
        t(TokenType.IDENTIFIER, "foo",1,10,10),
        t(TokenType.PUNCTUATION, "(",1,13,13),
        t(TokenType.PUNCTUATION, ")",1,14,14),
        t(TokenType.PUNCTUATION, "{",1,16,16),
        t(TokenType.KEYWORD, "return",2,3,21),
        t(TokenType.STRING, "\"bar\"",2,10,28),
        t(TokenType.PUNCTUATION, "}",3,1,35),
        t(TokenType.KEYWORD, "function",5,1,40),
        t(TokenType.IDENTIFIER, "identity",5,10,49),
        t(TokenType.PUNCTUATION, "(",5,18,57),
        t(TokenType.IDENTIFIER, "x",5,19,58),
        t(TokenType.PUNCTUATION, ")",5,20,59),
        t(TokenType.PUNCTUATION, "{",5,22,61),
        t(TokenType.KEYWORD, "return",6,3,66),
        t(TokenType.IDENTIFIER, "x",6,10,73),
        t(TokenType.PUNCTUATION, "}",7,1,76),
        t(TokenType.KEYWORD, "function",9,1,81),
        t(TokenType.IDENTIFIER, "returnsJavascriptObject",9,10,90),
        t(TokenType.PUNCTUATION, "(",9,33,113),
        t(TokenType.IDENTIFIER, "arg",9,34,114),
        t(TokenType.PUNCTUATION, ")",9,37,117),
        t(TokenType.PUNCTUATION, "{",9,39,119),
        t(TokenType.KEYWORD, "var",10,3,124),
        t(TokenType.IDENTIFIER, "x",10,7,128),
        t(TokenType.OPERATOR, "=",10,9,130),
        t(TokenType.NUMBER, "10",10,11,132),
        t(TokenType.PUNCTUATION, ";",10,13,134),
        t(TokenType.KEYWORD, "var",11,3,139),
        t(TokenType.IDENTIFIER, "y",11,7,143),
        t(TokenType.OPERATOR, "=",11,9,145),
        t(TokenType.KEYWORD, "function",11,11,147),
        t(TokenType.PUNCTUATION, "(",11,19,155),
        t(TokenType.PUNCTUATION, ")",11,20,156),
        t(TokenType.PUNCTUATION, "{",11,22,158),
        t(TokenType.KEYWORD, "return",11,24,160),
        t(TokenType.NUMBER, "20",11,31,167),
        t(TokenType.PUNCTUATION, "}",11,34,170),
        t(TokenType.KEYWORD, "return",12,3,175),
        t(TokenType.PUNCTUATION, "{",12,10,182),
        t(TokenType.STRING, "\"x\"",13,5,189),
        t(TokenType.OPERATOR, ":",13,9,193),
        t(TokenType.IDENTIFIER, "x",13,11,195),
        t(TokenType.PUNCTUATION, ",",13,12,196),
        t(TokenType.STRING, "\"y\"",14,5,203),
        t(TokenType.OPERATOR, ":",14,9,207),
        t(TokenType.IDENTIFIER, "y",14,11,209),
        t(TokenType.PUNCTUATION, ",",14,12,210),
        t(TokenType.STRING, "\"arg\"",15,5,217),
        t(TokenType.OPERATOR, ":",15,11,223),
        t(TokenType.IDENTIFIER, "arg",15,13,225),
        t(TokenType.PUNCTUATION, "}",16,3,232),
        t(TokenType.PUNCTUATION, "}",17,1,235));
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

  //Assert that the type and value are equal, ignoring line numbers
  private void assertTokenTypesEq(List<Tokenizer.Token> actual, Tokenizer.Token... expected ) {
    Assert.assertEquals(actual.size(), expected.length);
    for (int i = 0; i < expected.length; i++) {
      Assert.assertEquals(actual.get(i).getType(), expected[i].getType());
      Assert.assertEquals(actual.get(i).getValue(), expected[i].getValue());
    }
  }

  private void assertTokensEq(List<Tokenizer.Token> actual, Tokenizer.Token... expected) {
    Assert.assertEquals(Arrays.asList(expected), actual);
  }

  private Tokenizer.Token t( TokenType type, String val )
  {
    return new Tokenizer.Token( type, val );
  }

  private Tokenizer.Token t( TokenType type, String val, int lineNumber, int col, int offset)
  {
    return new Tokenizer.Token( type, val, lineNumber, col, offset);
  }

}
