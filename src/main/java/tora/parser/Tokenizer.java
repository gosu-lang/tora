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
    Token ret;

    if (_ch == '\'' || _ch == '"') {
      ret = consumeString();
    } else if (TokenType.startsIdentifier(_ch)) {
      ret = consumeWord();
    } else if (_ch == '.') {
      //For numbers that start with the decimal point
      if (TokenType.isDigit(peek())) ret = consumeNumber();
      else ret = consumePunctuation();
    } else if (TokenType.isDigit(_ch)) {
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
      nextChar();
    }

    return ret;
  }


  /*Possible numbers are integers (decimal, hex, octal or binary)
    and floating point numbers (can have decimal point and exponent);
    Rules taken from mozilla docs Grammar and Types
    */
  private Token consumeNumber() {
    boolean isDec = true, isBin = false, isHex = false, isOctal = false, isImpliedOctal = false;
    boolean hasDecPoint = false;
    StringBuilder val = new StringBuilder();
    if (_ch == '0') {
      isDec = false;
      //Mark if explicitly hex, octal, or binary
      if( "oOxXbB".indexOf(peek()) >= 0) {
        val.append(_ch);
        nextChar();
        if (_ch == 'b' || _ch == 'B') isBin = true;
        else if (_ch == 'x' || _ch == 'X') isHex = true;
        else if (_ch == 'o' || _ch == 'O') isOctal = true;
        val.append(_ch);
        nextChar();
      } else {
        //Octal is implied if number starts with 0, but can still be dec if a 8 or 9 follows
        isImpliedOctal = true;
      }
    }
    while ( !(isDec && hasDecPoint && _ch == '.') && //Limit one decimal point to floating point num
            (isDec && (TokenType.isDigit(_ch) || _ch == '.') || //Only dec can have decimal points
            (isHex && TokenType.isHexCh(_ch)) ||
            ((isOctal || isImpliedOctal) && TokenType.isOctal(_ch))) ||
            (isBin && TokenType.isBinary(_ch))) {
      if (_ch == '.') hasDecPoint = true;
      val.append(_ch);
      nextChar();
      if (isDec && (_ch == 'e' || _ch == 'E')) return consumeExponent(val);
      //changes octal to dec, so 0777 will be octal and 0778 will be dec
      if (isImpliedOctal && (_ch == '8' || _ch == '9')) {
        isDec = true;
        isImpliedOctal = false;
      }
    }
    //If explicitly starts with 0x, 0o, or 0b; throw an error if nothing after
    if ((isHex || isBin || isOctal) && val.length() <= 2) {
      return newToken(TokenType.ERROR, "illegal number token");
    }
    return newToken (TokenType.NUMBER, val.toString());
  }

  /* Helper for consume number; Consumes the exponent segment of a number, which will start with e (or E),
   * is optionally followed by a sign, and then contains only integers
   */
  private Token consumeExponent(StringBuilder val) {
    val.append(_ch);
    nextChar();
    //Consume optional + or -
    if (_ch == '+' || _ch == '-') {
      val.append(_ch);
      nextChar();
    }
    for (; TokenType.isDigit(_ch); nextChar()) {
      val.append(_ch);
    }
    return newToken (TokenType.NUMBER, val.toString());
  }



  /* Consumes and returns an word (either a identifier, keyword, boolean literal, or null)
   * Rules for identifier names from emca-262 11.6.1
   */
  private Token consumeWord() {
    StringBuilder val = new StringBuilder();
    for (; TokenType.partOfIdentifier(_ch); nextChar()) {
      val.append(_ch);
    }
    String strVal = val.toString();
    if (TokenType.isKeyword(strVal)) return newToken(TokenType.KEYWORD, strVal);
    else if (TokenType.isNull(strVal)) return newToken(TokenType.NULL, strVal);
    else if (TokenType.isBoolean(strVal)) return newToken(TokenType.BOOLEAN, strVal);
    else if (TokenType.isClass(strVal)) return newToken(TokenType.CLASS, strVal);
    else return newToken(TokenType.IDENTIFIER, strVal);
  }

  /* Strings can use ' or " in javascript */
  private Token consumeString() {
    char enterQuote = _ch; //Can be either ' or "
    StringBuilder val = new StringBuilder(String.valueOf(_ch));
    nextChar();
    //Consume string until we find a non-escaped quote matching the enter quote
    for (;!(_ch == enterQuote && val.charAt(val.length()-1) != '\\'); nextChar()) {
      //error if EOF comes before terminating quote
      if (reachedEOF()) return newToken(TokenType.ERROR, "unterminated string");
      //error if line terminator in string
      if (_ch == '\n' || _ch =='\r') return newToken(TokenType.ERROR, "newline character in string");
      val.append(_ch);
    }
    System.out.println("end of consume string");
    val.append(_ch); //add closing quote;
    nextChar();
    return newToken(TokenType.STRING, val.toString());
  }

  /*Consumes punctuation, which are all single characters*/
  private Token consumePunctuation() {
    Token tok = newToken(TokenType.PUNCTUATION, String.valueOf(_ch));
    nextChar();
    return tok;
  }

  private Token consumeOperator() {
    StringBuilder val = new StringBuilder();
    /*Keep consuming until we reach a non operator character or when adding the character makes a
     non-valid operator, since every multi-character operator is built off a shorter operator
      */
    for (; TokenType.isPartOfOperator(_ch) && TokenType.isOperator(val.toString() + _ch); nextChar()) {
      val.append(_ch);
    }
    return newToken(TokenType.OPERATOR, val.toString());
  }

  private Token consumeMultilineComment() {
    StringBuilder val = new StringBuilder("/*");
    nextChar();
    nextChar(); //Consume first two chars, which we know make '/*'
    for (;!(_ch == '/' && val.charAt(val.length()-1) == '*'); nextChar()) {
      val.append(_ch);
      //error if EOF comes before terminating quote
      if (reachedEOF()) return newToken(TokenType.ERROR, "unterminated multiline comment");
    }
    val.append(_ch);
    nextChar();
    return newToken(TokenType.COMMENT, val.toString());
  }

  private Token consumeSingleLineComment() {
    StringBuilder val = new StringBuilder("//");
    nextChar();
    nextChar(); //Consume first two chars, which we know make '//'
    for (; !(_ch == '\n' || _ch == '\r' || reachedEOF()); nextChar()) {
      val.append(_ch);
    }
    return newToken(TokenType.COMMENT, val.toString());
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
