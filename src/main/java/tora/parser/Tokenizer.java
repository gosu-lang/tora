package tora.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class Tokenizer
{
  public static class Token {
    private int _lineNumber;
    private int _col;
    private int _offset;
    private TokenType _type;
    private String _val;

    public Token(TokenType type, String val) {
      _type = type;
      _val = val;
    }

    public Token(TokenType type, String val, int lineNumber, int col, int offset) {
      _type = type;
      _val = val;
      _lineNumber = lineNumber;
      _col = col;
      _offset = offset;
    }

    public TokenType getType() {
      return _type;
    }

    public String getValue() {
      return _val;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Token)) return false;
      Token token = (Token) obj;
      return _type == token.getType() && _val.equals(token.getValue());
    }

    @Override
    public String toString() {
      return "type: " + _type + " val: " + _val + "\n";
    }

  }

  private int _lineNumber;
  private int _col;
  private int _offset;
  private BufferedReader _reader;
  private char _ch;

  public Tokenizer(String text) {
    this(new BufferedReader(new StringReader(text)));
  }

  public Tokenizer(BufferedReader reader) {
    _reader = reader;
    nextChar();
  }

  public Token next() {
    consumeWhitespace();
    Token ret = null;

    if (_ch == '\'' || _ch == '"') {
      ret = consumeString();
    } else if (String.valueOf(_ch).matches("[a-zA-Z$_]")) {
      ret = consumeWord();
    } else if (_ch == '.') {
      //For numbers that start with the decimal point
      if (String.valueOf(peek()).matches("[0-9]")) ret = consumeNumber();
      else ret = consumePunctuation();
    } else if (String.valueOf(_ch).matches("[0-9]")) {
      ret = consumeNumber();
    } else if (TokenType.isPunctuation(_ch)) {
      ret = consumePunctuation();
    } else if (_ch == '/') {
      /*Forward slash will either result in a single line comment, multiline comment, or a operator token*/
      switch (peek()) {
        case '*':
          return consumeMultilineComment();
        case '/':
          return consumeSingleLineComment();
        default:
          return consumeOperator();
      }
    } else if (TokenType.isPartOfOperator(_ch)) {
      ret = consumeOperator();
    } else if (reachedEOF()) {
      ret = newToken(TokenType.EOF, "EOF");
    } else  {
      ret = newToken(TokenType.ERROR, "unknown char");
    }
    return ret; //Make this into an error later?
  }


  //TODO: add decimals and hex and all that
  private Token consumeNumber() {
    boolean isBin = false, isHex = false, hasDecPoint = false;
    String val = "";
    if (_ch == '0' && "xXbB".indexOf(peek()) >= 0) {
      val += _ch;
      nextChar();
      if (_ch == 'b' || _ch == 'B') isBin = true;
      else if (_ch == 'x' || _ch == 'X') isHex = true;
      val += _ch;
      nextChar();
    }
    while ( (!hasDecPoint || _ch != '.') && //One decimal point max for numbers
            (!isHex && !isBin && String.valueOf(_ch).matches("[0-9.]")) ||
            (isHex && String.valueOf(_ch).matches("[0-9a-fA-F.]")) ||
            (isBin && String.valueOf(_ch).matches("[01.]"))) {
      if (_ch == '.') hasDecPoint = true;
      val += _ch;
      nextChar();
    }
    return newToken (TokenType.NUMBER, val);
  }



  /* Consumes and returns an word (either a identifier, keyword, boolean literal, or null)
   * Rules for identifier names from emca-262 11.6.1
   */
  private Token consumeWord() {
    String val = "";
    for (; String.valueOf(_ch).matches("[0-9a-zA-Z$_]"); nextChar()) {
      val += _ch;
    }
    if (TokenType.isKeyword(val)) return newToken(TokenType.KEYWORD, val);
    else if (TokenType.isNull(val)) return newToken(TokenType.NULL, val);
    else if (TokenType.isBoolean(val)) return newToken(TokenType.BOOLEAN, val);
    else if (TokenType.isClass(val)) return newToken(TokenType.CLASS, val);
    else return newToken(TokenType.IDENTIFIER, val);
  }

  private Token consumeString() {
    char enterQuote = _ch; //Can be either ' or "
    String val = String.valueOf(enterQuote);
    nextChar();
    //Consume string until we find a non-escaped quote matching the enter quote
    for (;!(_ch == enterQuote && val.charAt(val.length()-1) != '\\'); nextChar()) {
      val += _ch;
      //error if EOF comes before terminating quote
      if (reachedEOF()) return newToken(TokenType.ERROR, "unterminated string");
    }
    val += _ch; //add closing quote;
    nextChar();
    return newToken(TokenType.STRING, val);
  }

  /*Consumes punctuation, which are all single characters*/
  private Token consumePunctuation() {
    Token tok = newToken(TokenType.PUNCTUATION, String.valueOf(_ch));
    nextChar();
    return tok;
  }

  private Token consumeOperator() {
    String val = "";
    /*Keep consuming until we reach a non operator character or when adding the character makes a
     non-valid operator, since every multi-character operator is built off a shorter operator
      */
    for (; TokenType.isPartOfOperator(_ch) && TokenType.isOperator(val + _ch); nextChar()) {
      val += _ch;
    }
    return newToken(TokenType.OPERATOR, val);
  }

  private Token consumeMultilineComment() {
    String val = "/*";
    nextChar();
    nextChar(); //Consume first two chars, which we know make '/*'
    for (;!(_ch == '/' && val.charAt(val.length()-1) == '*'); nextChar()) {
      val += _ch;
      //error if EOF comes before terminating quote
      if (reachedEOF()) return newToken(TokenType.ERROR, "unterminated multiline comment");
    }
    val += _ch;
    nextChar();
    return newToken(TokenType.COMMENT, val);
  }

  private Token consumeSingleLineComment() {
    String val = "//";
    nextChar();
    nextChar(); //Consume first two chars, which we know make '//'
    for (; !(_ch == '\n' || _ch == '\r' || reachedEOF()); nextChar()) {
      val += _ch;
    }
    return newToken(TokenType.COMMENT, val);
  }

  //========================================================================================
  // Utilities
  //========================================================================================

  private void consumeWhitespace() {
    while (Character.isWhitespace(_ch)) nextChar();
  }

  //Returns the next character in the stream without updating _ch
  private char peek() {
    char ahead = '\0';
    try {
      _reader.mark(1);
      ahead = (char) _reader.read();
      _reader.reset();
    } catch (IOException e) {
      _ch = (char)-1; //go to EOF on exception
    }
    return ahead;
  }

  private void nextChar() {
    try {
      _ch = (char) _reader.read();
    } catch (IOException e){
      _ch = (char) -1; //go to EOF on exception
    }

    _offset++;
    _col++;
    if (_ch == '\n') {
      _col = 0;
      _lineNumber ++;
    }
  }

  private Token newToken(TokenType type, String val) {
    return new Token(type, val, _lineNumber, _col, _offset);
  }

  private boolean reachedEOF() {
    return _ch == (char) -1;
  }

  public List<Token> tokenize() {
    List<Token> tokens = new LinkedList<Token>();
    for (Token token = next(); token.getType() != TokenType.EOF; token = next()) {
      tokens.add(token);
    }
    return tokens;
  }

}
