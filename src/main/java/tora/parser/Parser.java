package tora.parser;

import com.sun.tools.javac.util.List;
import tora.parser.tree.*;

public class Parser
{
  String _src;
  private ClassNode _classNode;
  private Tokenizer _tokenizer;
  private Tokenizer.Token _currentToken, _nextToken;

  private List<String> _errorList;

  //Constructor sets the src from which the parser reads
  public Parser(Tokenizer tokenizer){
      _tokenizer = tokenizer;
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
    if( match(TokenType.CLASS))
    {
      nextToken();
      if( match(TokenType.IDENTIFIER) )
      {
        Tokenizer.Token className = _currentToken;
        nextToken();
        _classNode = new ClassNode( className.getValue() );
        if (match( '{' ))
        {
          nextToken();
          parseClassBody(className.getValue());
          if (match( '}' )) {
            Tokenizer.Token end = _currentToken;
            _classNode.setTokens(className, end);
          }
        }
      }
    }
  }

  private void parseClassBody(String className)
  {
    while(!match('}')) {
      if (matchClassKeyword("constructor")) {
        _classNode.addChild(parseConstructor(className));
      } else if (matchClassKeyword("static")) { //properties and functions can both be static
        Tokenizer.Token staticToken = _currentToken;
        nextToken();
        if (matchClassKeyword("get") || matchClassKeyword("set")) {
          _classNode.addChild(parseStaticProperty(className, staticToken));
        } else if (match(TokenType.IDENTIFIER)) {
          _classNode.addChild(parseStaticFunction(className, staticToken));
        }
      } else if (matchClassKeyword("get") || matchClassKeyword("set")) {
        _classNode.addChild(parseProperty(className));
      } else if (match(TokenType.IDENTIFIER)) {
        _classNode.addChild(parseFunction(className));
      } else if (match(TokenType.COMMENT)) {
        nextToken(); //ignore comments for now
      } else {
        //TODO: add error
        nextToken();
      }
    }
  }

  private ConstructorNode parseConstructor(String className) {
    Tokenizer.Token start = _currentToken; //'constructor'
    nextToken();
    if (match('(')) {
      String args = parseArgs();
      FunctionBodyNode body = parseFunctionBody();
      if (match('}')) {
        Tokenizer.Token end = _currentToken;
        nextToken();
        ConstructorNode node = new ConstructorNode(className, className, args);
        node.setTokens(start, end);
        node.addChild(body);
        return node;
      }
    }
    return null;
  }

  private FunctionNode parseStaticFunction(String className, Tokenizer.Token staticToken) {
    FunctionNode functionNode = parseFunction(className);
    functionNode.setTokens(staticToken, functionNode.getEnd());
    functionNode.setStatic(true);
    return functionNode;
  }

  private FunctionNode parseFunction(String className) {
    Tokenizer.Token start = _currentToken; //Name of function
    String functionName = start.getValue();
    nextToken();
    if (match('(')) {
      String args = parseArgs();
      FunctionBodyNode body = parseFunctionBody();
      if (match('}')) {
        Tokenizer.Token end = _currentToken;
        nextToken();
        FunctionNode node = new FunctionNode(functionName, className, args);
        node.setTokens(start, end);
        node.addChild(body);
        return node;
      }
    }
    return null;
  }

  private PropertyNode parseStaticProperty(String className, Tokenizer.Token staticToken) {
    PropertyNode propertyNode = parseProperty(className);
    propertyNode.setTokens(staticToken, propertyNode.getEnd());
    propertyNode.setStatic(true);
    return propertyNode;
  }


  private PropertyNode parseProperty(String className) {
    Tokenizer.Token start = _currentToken; //'get' or 'set'
    boolean isSetter = matchClassKeyword("set");
    nextToken();
    Tokenizer.Token functionName = _currentToken;
    nextToken();
    if (match('(')) {
      String args = parseArgs();
      FunctionBodyNode body = parseFunctionBody();
      if (match('}')) {
        Tokenizer.Token end = _currentToken;
        nextToken();
        PropertyNode node = new PropertyNode(functionName.getValue(), className, args, isSetter);
        node.setTokens(start, end);
        node.addChild(body);
        return node;
      }
    }
    return null;
  }

  private String parseArgs() {
    nextToken(); // skip over the '('
    StringBuilder val = new StringBuilder();
    Matcher matcher = () -> match(')') || match(TokenType.IDENTIFIER);
    expect(matcher);
    while (!match(')')) {
      if (match(TokenType.IDENTIFIER)) {
        matcher = () -> match(',') || match(')'); //ending paren or comma can follow identifier
        concatToken(val);
      } else if (match(',')) {
        matcher = () -> match(TokenType.IDENTIFIER); //identifier must follow commas
        concatToken(val);
      }
      nextToken();
      expect(matcher);
    }
    nextToken(); //skip over the ')'
    return val.toString();
  }

  private FunctionBodyNode parseFunctionBody() {
    StringBuilder val = new StringBuilder();
    concatToken(val); // '{'
    int curlyCount = 1;
    while (curlyCount > 0) {
      nextToken();
      if (match('}')) curlyCount--;
      if (match('{')) curlyCount++;
      concatToken(val);
    }
    return new FunctionBodyNode(val.toString());
  }

  //========================================================================================
  // Utilities
  //========================================================================================

  //Used to create lambda functions for matching tokens
  private interface Matcher {
    boolean match();
  }

  private void concatToken (StringBuilder val) {
    if (match(TokenType.NUMBER)) {
      val.append(" ");
    }
    val.append(_currentToken.getValue());
    if (match(TokenType.KEYWORD)) {
      val.append(" ");
    }
  }

  private void expect(Matcher matcher) {
    if (!matcher.match()) error("Unexpected Token: " + _currentToken.toString());
  }

  private void error(String errorMsg) {
    _classNode.addError(new Error(errorMsg));
  }

  private boolean match( char c )
  {
    return match(TokenType.PUNCTUATION, String.valueOf(c));
  }

  private boolean matchKeyword(String val)
  {
    return match(TokenType.KEYWORD, val);
  }

  /*Matches conditional keywords such as "constructor", which are only keywords within a class*/
  private boolean matchClassKeyword(String val)
  {
    if (!match(TokenType.IDENTIFIER, val)) return false;
    //If these class keywords aren't followed by an identifier, treat them as regular identifiers
    if ((val.equals("static") || val.equals("get") || val.equals("set")) &&
            peekToken().getType() != TokenType.IDENTIFIER) return false;
    return  true;
  }

  private boolean match(TokenType type, String val) {
    return (_currentToken.getType() == type &&
            _currentToken.getValue().equals(val));
  }

  private boolean match( TokenType type )
  {
    return (_currentToken.getType() == type);
  }

  private Tokenizer.Token peekToken() {
    return _nextToken;
  }

  //Keep track of next token as well for context-sensitive class keywords
  private void nextToken()
  {
    if (_nextToken == null) _nextToken = _tokenizer.next(); //For the first token
    _currentToken = _nextToken;
    if (_nextToken.getType() != TokenType.EOF)_nextToken = _tokenizer.next();
  }
}