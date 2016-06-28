package tora.parser;

import tora.parser.tree.*;

public class Parser
{
  String _src;
  ClassNode _classNode;
  private Tokenizer _tokenizer;
  private Tokenizer.Token _currentToken;


  //Constructor sets the src from which the parser reads
  public Parser(Tokenizer tokenizer){
      _tokenizer = tokenizer;
      System.out.println(tokenizer);
  }
  public boolean isES6Class() {
    return _classNode != null;
  }

  public ClassNode parse() {
    nextToken();
    parseClassStatement();
    return _classNode;
  }

  private void parseClassStatement()
  {
    if( match(TokenType.CLASS) != null )
    {
      nextToken();
      Tokenizer.Token className = match( TokenType.IDENTIFIER );
      if( className != null )
      {
        nextToken();
        _classNode = new ClassNode( className.getValue() );
        if( match( '{' ) != null )
        {
          nextToken();
          parseClassBody(className.getValue());
          Tokenizer.Token end = match( '}' );
          _classNode.setTokens( className, end );
        }
      }
    }
  }

  private void parseClassBody(String className)
  {
    while(match('}') == null) {
      System.out.println(_currentToken.getValue());
      // TODO
      if (matchClassKeyword("constructor") != null) {
        _classNode.addChild(parseConstructor(className));
      } else if (matchClassKeyword("get") != null || matchClassKeyword("set") != null) {
        _classNode.addChild(parseProperty());
      } else if (matchClassKeyword("static") != null || match(TokenType.IDENTIFIER) != null) {
        _classNode.addChild(parseFunction(className));
      }
      // remember the static modifiers
      // include bodies
    }
  }

  private ConstructorNode parseConstructor(String className) {
    Tokenizer.Token start = _currentToken; //starting token is "constructor"
    nextToken();
    String args = "";
    FunctionBodyNode body = null;
    if (match('(') != null) {
      args = parseArgs();
      body = parseFunctionBody();
    }
    Tokenizer.Token end = match('}');
    nextToken();
    ConstructorNode node = new ConstructorNode(className, args, start, end);
    node.addChild(body);
    return node;
  }

  private FunctionNode parseFunction(String className) {
    boolean isStatic = false;
    String args = "";
    FunctionBodyNode body = null;
    Tokenizer.Token start = _currentToken;
    if (matchClassKeyword("static") != null) {
      isStatic = true;
      nextToken();
    }
    Tokenizer.Token functionName = match(TokenType.IDENTIFIER);
    nextToken();
    if (match('(') != null) {
      args = parseArgs();
      body = parseFunctionBody();
    }
    Tokenizer.Token end = match('}');
    nextToken();
    FunctionNode node = new FunctionNode(functionName.getValue(), className, args, isStatic);
    node.addChild(body);
    return node;
  }

  private PropertyNode parseProperty() {

    return null;
  }

  private String parseArgs() {
    nextToken(); // '('
    StringBuilder val = new StringBuilder();
    while (match(')') == null) {
      if (match(TokenType.IDENTIFIER) != null) {
        concatToken(val);
        nextToken();
      }
      if (match(',') != null) {
        concatToken(val);
        nextToken();
      }
    }
    nextToken();
    return val.toString();
  }
  
  private void concatToken (StringBuilder val) {
    if (match(TokenType.NUMBER) != null) {
      val.append(" ");
    }
    val.append(_currentToken.getValue());
    if (match(TokenType.KEYWORD) != null) {
      val.append(" ");
    }
  }

  private FunctionBodyNode parseFunctionBody() {
    StringBuilder val = new StringBuilder();
    concatToken(val); // '{'
    int curlyCount = 1;
    while (curlyCount > 0) {
      nextToken();
      if (match('}') != null) curlyCount--;
      if (match('{') != null) curlyCount++;
      concatToken(val);
    }
    return new FunctionBodyNode(val.toString());
  }

  private Tokenizer.Token match( char c )
  {
    return match(TokenType.PUNCTUATION, String.valueOf(c));
  }

  private Tokenizer.Token matchKeyword(String val)
  {
    return match(TokenType.KEYWORD, val);
  }

  /*Matches conditional keywords such as "constructor", which are only keywords within a class*/
  private Tokenizer.Token matchClassKeyword(String val)
  {
    return match(TokenType.IDENTIFIER, val);
  }

  private Tokenizer.Token match(TokenType type, String val) {
    if (_currentToken.getType() == type &&
            _currentToken.getValue().equals(val)) {
      Tokenizer.Token t = _currentToken;
      return t;
    } else {
      return null;
    }
  }

  private Tokenizer.Token match( TokenType type )
  {
    if( _currentToken.getType() == type )
    {
      Tokenizer.Token t = _currentToken;
      return t;
    }
    else
    {
      return null;
    }
  }

  private void nextToken()
  {
    _currentToken = _tokenizer.next();
  }
}