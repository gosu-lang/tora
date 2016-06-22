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
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "123")), num);
    //Whitespace tests
    Assert.assertEquals(num, tokenize(" 123"));
    Assert.assertEquals(num, tokenize("   123  "));
    //Two tokens
    List<Tokenizer.Token> twoNum = tokenize("123  456");
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "123"),
                                      new Tokenizer.Token(TokenType.NUMBER, "456")), twoNum);

  }

  @Test
  public void simpleKeywordTest()
  {
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.KEYWORD, "for")),
            tokenize("for"));
  }

  @Test
  public void simpleIdentifierTest()
  {
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.IDENTIFIER, "carson")),
            tokenize("carson"));
  }

  @Test
  public void simpleNullTest()
  {
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NULL, "null")),
            tokenize("null"));
  }

  @Test
  public void simpleBooleanTest()
  {
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.BOOLEAN, "true")),
            tokenize("true"));
  }

  @Test
  public void simplePunctationTest()
  {
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.PUNCTUATION, ",")),
            tokenize(","));
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.PUNCTUATION, "("),
            new Tokenizer.Token(TokenType.PUNCTUATION, ")")),
            tokenize("()"));
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.PUNCTUATION, ".")),
            tokenize("."));
  }

  @Test
  public void simpleOperatorTest()
  {
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.OPERATOR, "*")),
            tokenize("*"));
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.OPERATOR, "/")),
            tokenize("/"));
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.OPERATOR, ">=")),
            tokenize(">="));
    //Error; should be caught as unexpected operator by parser
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.OPERATOR, ">="),
            new Tokenizer.Token(TokenType.OPERATOR, "<")),
            tokenize(">=<"));
  }

  @Test
  public void simpleCommentTest()
  {
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.COMMENT, "//donald")),
            tokenize("//donald"));
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.COMMENT, "/*insurance is fun*/")),
            tokenize("/*insurance is fun*/"));
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.IDENTIFIER, "x"),
            new Tokenizer.Token(TokenType.OPERATOR, "+"),
            new Tokenizer.Token(TokenType.COMMENT, "/*comments are fun*/"),
            new Tokenizer.Token(TokenType.NUMBER, "5")),
            tokenize("x+/*comments are fun*/5"));

  }

  @Test
  public void numberTest()
  {
    //Integer
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "12345")),
            tokenize("12345"));
    //Decimal
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "123.45")),
            tokenize("123.45"));
    //Hex
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "0x123abc")),
            tokenize("0x123abc"));
    //Octal
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "0O1342")),
            tokenize("0O1342"));
    //Implied Octal
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "01323")),
            tokenize("01323"));
    //Implied Octal turned Dec
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "0778.4")),
            tokenize("0778.4"));
    //Exponential
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "123e4")),
            tokenize("123e4"));
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "123e+4")),
            tokenize("123e+4"));
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "12.3e-4")),
            tokenize("12.3e-4"));
    //Binary
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "0b1011")),
            tokenize("0b1011"));

    /*Errors*/
    //Multiple decimal points; should be caught as unexpected number during parsing
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "123.456"),
            new Tokenizer.Token(TokenType.NUMBER, ".789")),
            tokenize("123.456.789"));
    //Decimal point at end; should be caught as unexpected punctuation (or end of input?) by parser
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "123.456"),
            new Tokenizer.Token(TokenType.PUNCTUATION, ".")),
            tokenize("123.456."));
    //Integer with hex numbers; should be caught as unexpected identifier by parser
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "123"),
            new Tokenizer.Token(TokenType.IDENTIFIER, "abc")),
            tokenize("123abc"));
    //Binary with non 0-1 digits; should be caught as unexpected number by parser
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "0b101"),
            new Tokenizer.Token(TokenType.NUMBER, "234")),
            tokenize("0b101234"));
    //Octal with decimal point; should be caught as unexpected number
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "0134"),
            new Tokenizer.Token(TokenType.NUMBER, ".2")),
            tokenize("0134.2"));
    //Exponent with decimal point; should be caught as unexpected number
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.NUMBER, "5e5"),
            new Tokenizer.Token(TokenType.NUMBER, ".5")),
            tokenize("5e5.5"));
    //Marked as binary, with no value following
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.ERROR, "illegal number token")),
            tokenize("0b"));
  }

  @Test
  public void stringEscapeTest()
  {
    //Double quotes
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.STRING, "\"Carson Gross\"")),
            tokenize("\"Carson Gross\""));
    //Single quotes
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.STRING, "'Carson Gross'")),
            tokenize("'Carson Gross'"));
    //Single quotes inside double quotes
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.STRING, "\"Carson Gross is 'nice'\"")),
            tokenize("\"Carson Gross is 'nice'\""));
    //Double quotes inside single quotes
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.STRING, "'Carson Gross is \"nice\"'")),
            tokenize("'Carson Gross is \"nice\"'"));
    //Escaped Single quotes
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.STRING, "'Carson Gross is \\'nice\\''")),
            tokenize("'Carson Gross is \\'nice\\''"));
    //Escaped Double quotes
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.STRING, "\"Carson Gross is \\\"nice\\\" \"")),
            tokenize("\"Carson Gross is \\\"nice\\\" \""));
    //Unterminated quote
    Assert.assertEquals(Arrays.asList(new Tokenizer.Token(TokenType.ERROR, "unterminated string")),
            tokenize("\"Linux"));
  }

  @Test
  public void exampleJSFile()
  {
    try {
      URL url = getClass().getResource("/bootstrap.js");
      List<Tokenizer.Token> exampleJS = tokenize(new BufferedReader(new FileReader(url.getFile())));
      Iterator code = exampleJS.iterator();
      Assert.assertEquals(new Tokenizer.Token(TokenType.KEYWORD, "function"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.IDENTIFIER, "foo"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "("), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, ")"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "{"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.KEYWORD, "return"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.STRING, "\"bar\""), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "}"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.KEYWORD, "function"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.IDENTIFIER, "identity"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "("), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.IDENTIFIER, "x"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, ")"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "{"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.KEYWORD, "return"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.IDENTIFIER, "x"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "}"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.KEYWORD, "function"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.IDENTIFIER, "returnsJavascriptObject"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "("), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.IDENTIFIER, "arg"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, ")"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "{"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.KEYWORD, "var"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.IDENTIFIER, "x"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.OPERATOR, "="), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.NUMBER, "10"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, ";"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.KEYWORD, "var"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.IDENTIFIER, "y"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.OPERATOR, "="), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.KEYWORD, "function"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "("), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, ")"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "{"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.KEYWORD, "return"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.NUMBER, "20"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "}"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.KEYWORD, "return"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "{"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.STRING, "\"x\""), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.OPERATOR, ":"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.IDENTIFIER, "x"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, ","), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.STRING, "\"y\""), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.OPERATOR, ":"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.IDENTIFIER, "y"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, ","), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.STRING, "\"arg\""), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.OPERATOR, ":"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.IDENTIFIER, "arg"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "}"), code.next());
      Assert.assertEquals(new Tokenizer.Token(TokenType.PUNCTUATION, "}"), code.next());
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
}
