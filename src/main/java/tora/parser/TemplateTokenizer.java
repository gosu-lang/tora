package tora.parser;

public class TemplateTokenizer extends Tokenizer {

  private boolean inRawString;
  private boolean inStatementOrExpression;

  private String expressionOrStatementStart; //token that enters an expressionOrStatement

  public TemplateTokenizer(String source) {
    super(source);
    inRawString = true;
    inStatementOrExpression = false;
  }

  @Override
  public Token next() {
    Token toke;
    if (reachedEOF()) {
      toke = newToken(TokenType.EOF, "EOF");
    } else if (inRawString) {
      toke = consumeRawString();
    } else if (inStatementOrExpression){
      if (checkForExpressionExit()) return consumeTemplatePunc(); //transition from expression to rawstring
      return super.next(); //if in statement, tokenize as normal
    } else {
      toke = consumeTemplatePunc(); //transition from rawstring to expression; ${, <%, <%@, or <%=
    }
    return toke;
  }

  private Token consumeTemplatePunc() {
    String punc = String.valueOf(currChar());
    switch (currChar()) {
      //Entrance punctuations
      case '$': nextChar();
                punc += currChar(); //should be '{'
                nextChar();
                setInStatementOrExpression();
                break;
      case '<': nextChar();
                punc += currChar();
                nextChar();
                if (currChar() == '@' || currChar() == '=') {
                  punc += currChar();
                  nextChar();
                }
                setInStatementOrExpression();
                break;
      //Exit punctuations
      case '%': nextChar();
                punc += currChar(); //should be '>'
                nextChar();
                setInRawString();
                break;
      case '}': nextChar();
                setInRawString();
                break;
    }
    if (inStatementOrExpression) expressionOrStatementStart = punc;
    return newToken(TokenType.TEMPLATEPUNC, punc);
  }

  private Token consumeRawString() {
    StringBuilder val = new StringBuilder();
    while (!reachedEOF()) {
      if (checkForExpressionEnter()) {
        inRawString = false;
        break;
      }
      val.append(currChar());
      nextChar();
    }
    return newToken(TokenType.RAWSTRING, val.toString());
  }

  private boolean checkForExpressionEnter() {
    //If escaped, skip escape character and return false
    if (currChar() == '\\' && (peek() == '<' || peek() == '$')) {
        nextChar(); return false;
    }
    if (currChar() == '<' && peek() == '%') return true;
    if (currChar() == '$' && peek() == '{') return true;
    return false;
  }

  private boolean checkForExpressionExit() {
    if (expressionOrStatementStart.equals("${") && currChar() == '}') return true;
    if (!expressionOrStatementStart.equals("${") && currChar() == '%' && peek() == '>') return true;
    return false;
  }

  private void setInRawString() {
    inRawString = true;
    inStatementOrExpression = false;
  }

  private void setInStatementOrExpression() {
    inStatementOrExpression = true;
    inRawString = false;
  }

}
