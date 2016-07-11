package tora.parser;


import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


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
    //Throw away any comment tokens
    assertTokensEq(tokenize("//donald"));
    assertTokensEq(tokenize("/*insurance is fun*/"));
    assertTokensEq(tokenize("x+/*comments are fun*/5"),
            t(TokenType.IDENTIFIER, "x"),
            t(TokenType.OPERATOR, "+"),
            t(TokenType.NUMBER, "5"));

  }

  @Test
  public void numberTest() {
    //Integer
    assertTokensEq(tokenize("12345"), t(TokenType.NUMBER, "12345"));
    //Decimal
    assertTokensEq(tokenize("123.45"), t(TokenType.NUMBER, "123.45"));
    //Hex
    assertTokensEq(tokenize("0x123abc"), t(TokenType.NUMBER, "0x123abc"));
    //Octal
    assertTokensEq(tokenize("0O1342"), t(TokenType.NUMBER, "0O1342"));
    //Implied Octal
    assertTokensEq(tokenize("01323"), t(TokenType.NUMBER, "01323"));
    //Implied Octal turned Dec
    assertTokensEq(tokenize("0778.4"), t(TokenType.NUMBER, "0778.4"));
    //Exponential
    assertTokensEq(tokenize("123e4"), t(TokenType.NUMBER, "123e4"));
    assertTokensEq(tokenize("123e+4"), t(TokenType.NUMBER, "123e+4"));
    assertTokensEq(tokenize("12.3e-4"), t(TokenType.NUMBER, "12.3e-4"));
    //Binary
    assertTokensEq(tokenize("0b1011"), t(TokenType.NUMBER, "0b1011"));
  }

  @Test
  public void numbersErrorsTest() {
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
    assertTokensEq(tokenize("0b"), t(TokenType.ERROR, "0b", "illegal number token"));
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
    //Escaped non-escape characters (which apparently is okay in javascript
    assertTokensEq(tokenize("\"\\Carson\""),t(TokenType.STRING, "\"\\Carson\""));
    //Escaped hex
    assertTokensEq(tokenize("'\\xAF'"),t(TokenType.STRING, "'\\xAF'"));
    //Escaped unicode
    assertTokensEq(tokenize("'\\u1adf'"),t(TokenType.STRING, "'\\u1adf'"));
    assertTokensEq(tokenize("'\\u{10FFFF}'"),t(TokenType.STRING, "'\\u{10FFFF}'"));

    //LineContinuator
    assertTokensEq(tokenize("\"It's okay as long as there's a \\\n before new lines\""),
            t(TokenType.STRING, "\"It's okay as long as there's a \\\n before new lines\""));

  }

  @Test
  public void stringErrorsTest()
  {
    //Unterminated quote
    assertTokensEq(tokenize("\"Linux"), t(TokenType.ERROR, "\"Linux", "unterminated string"));
    //Illegal newline in middle
    assertTokensEq(tokenize("'Lin\nux'"), t(TokenType.ERROR, "'Lin\nux'", "newline character in string"));
    //Illegal hex escape
    assertTokensEq(tokenize("'\\x1'"), t(TokenType.ERROR, "'\\x1'", "non-hex character in hex escape"));
    assertTokensEq(tokenize("'\\x1'"), t(TokenType.ERROR, "'\\x1'", "non-hex character in hex escape"));
    assertTokensEq(tokenize("'\\x1z'"), t(TokenType.ERROR, "'\\x1z'", "non-hex character in hex escape"));
    //Illegal unicode
    assertTokensEq(tokenize("'\\u123'"), t(TokenType.ERROR, "'\\u123'", "non-hex character in unicode escape"));
    assertTokensEq(tokenize("'\\u13?'"), t(TokenType.ERROR, "'\\u13?'", "non-hex character in unicode escape"));
    assertTokensEq(tokenize("'\\u{13:D}'"), t(TokenType.ERROR, "'\\u{13:D}'", "non-hex character in unicode escape"));
    //Unicode too large
    assertTokensEq(tokenize("'\\u{11FFFF}'"), t(TokenType.ERROR, "'\\u{11FFFF}'", "undefined Unicode point"));
  }


  //Test bootstrap.js with line numbers
  @Test
  public void exampleJSFile()
  {
    try {
      URL url = getClass().getResource("/bootstrap.js");
      checkTokenPositions(url.getFile());
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

  private void assertTokensEq(List<Tokenizer.Token> actual, Tokenizer.Token... expected) {
    //compare everything ignoring whitespace
    Assert.assertEquals(Arrays.asList(expected), filterWhitespaceTokens(actual));
  }

  /* Reads through file, keeping track of line number, col, and offset, and ensures that when the reader reaches
   * the offset of a token, that the line number, col, and string value at that position matches the token's.
   * Hardcoding offsets in tests did not work since Windows uses \r\n and Linux uses \n
   */
  private void checkTokenPositions(String filePath) {
    BufferedReader reader = null;
    List <Tokenizer.Token> actual = null;
    try {
      reader = new BufferedReader(new FileReader(filePath));
      actual = new Tokenizer(reader).tokenize();
      //Reset reader to beginning of file
      reader = new BufferedReader(new FileReader(filePath));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    int lineNumber = 1, col = 1, offset = 0;
    try {
      for (Tokenizer.Token toke : filterWhitespaceTokens(actual)) {
        //Move reader to the position of the next token
        for (int i = 0; i < toke.getOffset() - offset; i++) {
          col++;
          if ((char)reader.read() == '\n') {
            lineNumber++;
            col = 1;
          }
        }
        offset = toke.getOffset();
        //Check line number and col
        if (lineNumber != toke.getLineNumber() || col != toke.getCol()) {
          System.out.println(col);
          Assert.fail("Incorrect line position or col: " + toke.toString());
        }
        //Read in the expected token value
        char[] expectedVal = new char[toke.getValue().length()];
        reader.read(expectedVal, 0, toke.getValue().length());
        offset += toke.getValue().length();
        col += toke.getValue().length();
        //Check offset by confirming that string at token offset matches token value
        if (!toke.getValue().equals(new String(expectedVal)))
          Assert.fail("String at token offset (" + new String(expectedVal) + "does not match token value: " + toke.toString());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public List <Tokenizer.Token> filterWhitespaceTokens(List <Tokenizer.Token> tokens) {
    return tokens.stream()
            .filter(t->t.getType() != TokenType.WHITESPACE)
            .collect(Collectors.toList());
  }

  private Tokenizer.Token t( TokenType type, String val )
  {
    return new Tokenizer.Token( type, val );
  }

  private Tokenizer.Token t(TokenType type, String val, String errMsg) {
    return new Tokenizer.Token( type, val, errMsg);
  }

  private Tokenizer.Token t( TokenType type, String val, int lineNumber, int col, int offset)
  {
    return new Tokenizer.Token( type, val, lineNumber, col, offset);
  }

}
