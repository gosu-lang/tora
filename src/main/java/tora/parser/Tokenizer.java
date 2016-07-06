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
    private String _errorMsg;

    public Token(TokenType type, String val) {
      _type = type;
      _val = val;
    }

    public Token(TokenType type, String val, String errorMsg) {
      _type = type;
      _val = val;
      _errorMsg = errorMsg;
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

    public String getErrorMsg() {
      return _errorMsg;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Token)) return false;
      Token token = (Token) obj;
      return _type == token.getType() && _val.equals(token.getValue());
    }

    @Override
    public String toString() {
      return String.format("type: %s val: %s pos: %d:%d:%d\n", _type, _val, _lineNumber, _col, _offset) ;
    }

    public int getLineNumber() {
      return _lineNumber;
    }

    public int getOffset() {
      return _offset;
    }

    public int getCol() {
      return _col;
    }
  }

  private int _bLineNumber, _bCol, _bOffset; //Keeps track of beginning position of tokens
  private int _lineNumber, _col, _offset; //Keeps track of current position of tokenizer
  private BufferedReader _reader;
  private char _ch;

  public Tokenizer(String text) {
    this(new BufferedReader(new StringReader(text)));
  }

  public Tokenizer(BufferedReader reader) {
    _reader = reader;
    //Line number and col are 1 indexed; offset is 0 indexed (nextchar increments col and offset)
    _lineNumber = 1;
    _col = 0;
    _offset = -1;
    nextChar();
  }

  public Token next() {
    consumeWhitespace();
    updatePosition();
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
      ret = errToken(String.valueOf(_ch), "unknown char");
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
      return errToken(val.toString(), "illegal number token");
    }
    return newToken (TokenType.NUMBER, val.toString());
  }

  /* Helper for consume number; Consumes the exponent segment of a number, which will start with e (or E),
   * is optionally followed by a sign, and then contains only integers
   */
  private Token consumeExponent(StringBuilder val) {
    val.append(_ch); //consume 'e' or 'E'
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

  /* Syntax for string literal taken from emca 6 language specs 11.8.4
   * Since tokenizer is for transpiler, do not worry about escaping characters
   */
  private Token consumeString() {
    char enterQuote = _ch; //Can be either ' or "
    String errorMsg = null;
    StringBuilder val = new StringBuilder(String.valueOf(_ch));
    nextChar();
    //Consume string until we find a non-escaped quote matching the enter quote
    while (!(_ch == enterQuote)) {
      //error if EOF comes before terminating quote
      if (reachedEOF()) return errToken(val.toString(), "unterminated string");
      //error if line terminator in string
      if (TokenType.isLineTerminator(_ch)) errorMsg = "newline character in string";

      val.append(_ch);
      //Make sure escape sequences are legal
      if (_ch == '\\') {
        errorMsg = consumeEscapeSequence(val);
      } else nextChar();
    }
    val.append(_ch); //add closing quote
    nextChar();
    if (errorMsg != null) return errToken(val.toString(), errorMsg);
    return newToken(TokenType.STRING, val.toString());
  }

  /*Helper to consume and validate escape sequences;
  *  Marks invalid unicode and hex escapes are illegal; octal escapes should always be legal.
  *  Returns an error message if illegal escape */
  private String consumeEscapeSequence(StringBuilder val) {
    nextChar();
    switch (_ch) {
      case 'u': return consumeUnicodeEscapeSequence(val);
      case 'x': return consumeHexEscapeSequence(val);
      /* Consumes single escapes (' " \ b \f n r t v), and non-escaped (such as 'a' where \ will be ignored)
       * and newlines and ending quotes (for line continuation)
       */
      default:
        val.append(_ch);
        nextChar();
        return null;
    }
  }

  /*Unicode escape sequence are either uHexHexHexHex or u{Hex+}*/
  private String consumeUnicodeEscapeSequence(StringBuilder val) {
    final long MAX_UNICODE_NUM = 0x10FFFF;
    val.append(_ch); //consume 'u'
    nextChar();
    if (_ch == '{') {
      val.append(_ch);
      nextChar();
      StringBuilder num = new StringBuilder(); //keep track of hex number to check if valid unicode
      for (; _ch != '}'; nextChar()) {
        if (!TokenType.isHexCh(_ch)) return "non-hex character in unicode escape";
        num.append(_ch);
        val.append(_ch);
      }
      val.append(_ch); //consume closing }
      nextChar();
      //error if exceeds max unicode number
      if (Long.parseLong(num.toString(), 16) > MAX_UNICODE_NUM) return "undefined Unicode point";
      else return null;
    } else {
      //Must have exactly 4 hex digits in this pattern
      for (int i = 0; i < 4; i++) {
        if (!TokenType.isHexCh(_ch)) return "non-hex character in unicode escape";
        val.append(_ch);
        nextChar();
      }
      return null;
    }
  }

  /*hex escape sequences must be uHexHex*/
  private String consumeHexEscapeSequence(StringBuilder val) {
    val.append(_ch); //consume 'x'
    nextChar();
    for (int i = 0; i < 2; i++) {
      if (!TokenType.isHexCh(_ch)) return "non-hex character in hex escape";
      val.append(_ch);
      nextChar();
    }
    return null;
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
    val.append(_ch); //append closing slash
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

  //Updates the start token position when consuming a new token
  private void updatePosition() {
    _bCol = _col;
    _bLineNumber = _lineNumber;
    _bOffset = _offset;
  }

  private Token newToken(TokenType type, String val) {
    return new Token(type, val, _bLineNumber, _bCol, _bOffset);
  }

  private Token errToken(String val, String errorMsg) {
    return new Token(TokenType.ERROR, val, errorMsg);
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
