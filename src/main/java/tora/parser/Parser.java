package tora.parser;

import gw.lang.reflect.IMethodInfo;
import gw.lang.reflect.IType;
import gw.lang.reflect.TypeSystem;
import tora.parser.tree.*;

public class Parser
{
  private ClassNode _classNode;
  private ProgramNode _programNode;
  private Tokenizer _tokenizer;
  private Tokenizer.Token _currentToken, _nextToken;

  //Constructor sets the src from which the parser reads
  public Parser(Tokenizer tokenizer){
    _tokenizer = tokenizer;
    _programNode = new ProgramNode();
  }

  public boolean isES6Class() {
    return _classNode != null;
  }

  public ProgramNode parse() {
    nextToken();
    //Can only import classes at top of program
    parseImports();
    parseClassStatement();
    if (!isES6Class() && !match(TokenType.EOF)) _programNode.addChild(parseRestOfProgram());
    return _programNode;
  }


  private void parseClassStatement()
  {
    if( match(TokenType.CLASS))
    {
      //parse class name
      nextToken();
      Tokenizer.Token className = _currentToken;
      skip(match(TokenType.IDENTIFIER));
      _classNode = new ClassNode( className.getValue() );
      _programNode.addChild(_classNode);
      //parse any super classes
      if(matchKeyword("extends")) {
        skip(matchKeyword("extends"));
        _classNode.setSuperClass(_currentToken.getValue());
        skip(match(TokenType.IDENTIFIER));
      }
      //parse class body
      skip(match('{'));
      parseClassBody(className.getValue());
      skip(match('}'));
      Tokenizer.Token end = _currentToken;
      _classNode.setTokens(className, end);
    }
  }

  private void parseImports() {
    while (matchKeyword("import") && !match(TokenType.EOF)) {
      _programNode.addChild(parseImport());
      if (match(';')) nextToken(); //TODO: learn semi-colon syntax
    }
  }


  private ImportNode parseImport() {
    Tokenizer.Token start = _currentToken;
    skip(matchKeyword("import"));
    StringBuilder packageName = new StringBuilder();
    Matcher matcher = () -> match(TokenType.IDENTIFIER);
    while (matcher.match()) {
      concatToken(packageName);
      if (match(TokenType.IDENTIFIER)) matcher = () -> match('.');
      if (match('.')) matcher = () -> match(TokenType.IDENTIFIER);
      nextToken();
    }
    return new ImportNode(packageName.toString());
  }

  private void parseClassBody(String className)
  {
    while(!match('}') && !match(TokenType.EOF)) {
      if (matchClassKeyword("constructor")) {
        _classNode.addChild(parseConstructor(className));
      } else if (matchClassKeyword("static")) { //properties and functions can both be static
        Tokenizer.Token staticToken = _currentToken;
        nextToken();
        if (matchClassKeyword("get") || matchClassKeyword("set")) {
          _classNode.addChild(parseStaticProperty(className, staticToken));
        } else {
          _classNode.addChild(parseStaticFunction(className, staticToken));
        }
      } else if (matchClassKeyword("get") || matchClassKeyword("set")) {
        _classNode.addChild(parseProperty(className));
      } else if (match(TokenType.IDENTIFIER)) {
        _classNode.addChild(parseFunction(className));
      } else {
        error("Unexpected token: " + _currentToken.toString());
        nextToken();
      }
    }
  }

  private ConstructorNode parseConstructor(String className) {
    Tokenizer.Token start = _currentToken; //'constructor'
    skip(matchClassKeyword("constructor"));

    String args = parseArgs();
    FunctionBodyNode body = parseFunctionBody(className, false);

    Tokenizer.Token end = _currentToken;
    nextToken();
    ConstructorNode node = new ConstructorNode(className, className, args);
    node.setTokens(start, end);
    node.addChild(body);
    return node;
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
    boolean isOverrideFunction = isOverrideFunction(functionName);
    skip(match(TokenType.IDENTIFIER));

    String args = parseArgs();
    FunctionBodyNode body = parseFunctionBody(functionName, isOverrideFunction);

    FunctionNode node = new FunctionNode(functionName, className, args);
    node.setOverride(isOverrideFunction);
    node.setTokens(start, _currentToken);
    node.addChild(body);
    nextToken();
    return node;

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
    skip(matchClassKeyword("get") || matchClassKeyword("set"));
    String functionName = _currentToken.getValue();
    skip(match(TokenType.IDENTIFIER));

    String args = parseArgs();
    FunctionBodyNode body = parseFunctionBody(functionName, false);

    PropertyNode node = new PropertyNode(functionName, className, args, isSetter);
    node.setTokens(start, _currentToken);
    node.addChild(body);
    nextToken();
    return node;
  }

  /*Concats arguments into a comma-separated string*/
  private String parseArgs() {
    skip(match('('));
    StringBuilder val = new StringBuilder();
    Matcher matcher = () -> match(')') || match(TokenType.IDENTIFIER);
    expect(matcher);
    while (!match(')') && !match(TokenType.EOF)) {
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
    skip(match(')'));
    return val.toString();
  }

  private FunctionBodyNode parseFunctionBody(String functionName, boolean isOverrideFunction) {
    FunctionBodyNode bodyNode = new FunctionBodyNode(functionName);
    FillerNode fillerNode = new FillerNode();
    fillerNode.context.inOverrideFunction = isOverrideFunction;
    expect(match('{'));
    fillerNode.concatToken(_currentToken); // '{'
    int curlyCount = 1;
    while (curlyCount > 0 && !match(TokenType.EOF)) {
      nextAnyToken();
      if (match('}')) curlyCount--;
      if (match('{')) curlyCount++;

      if (matchOperator("=>")){
        skip(matchOperator("=>"));
        ArrowExpressionNode arrowNode = new ArrowExpressionNode();
        arrowNode.extractParams(fillerNode);
        //Add filler node and create a new one
        bodyNode.addChild(fillerNode);
        fillerNode = new FillerNode();
        if (match('{')) {
          arrowNode.addChild(parseFunctionBody(null, false));
          expect(match('}'));
        } else {
          arrowNode.addChild(parseExpression());
        }
        bodyNode.addChild(arrowNode);
      } else {
        fillerNode.concatToken(_currentToken);
      }
    }
    bodyNode.addChild(fillerNode);
    return bodyNode;
  }

  private FillerNode parseExpression() {
    FillerNode fillerNode = new FillerNode();
    while (!(match(TokenType.EOF) || match(';'))) {
      fillerNode.concatToken(_currentToken);
      nextAnyToken();
    }
    return fillerNode;
  }

  private FillerNode parseRestOfProgram() {
    FillerNode fillerNode = new FillerNode();
    while (!match(TokenType.EOF)) {
      fillerNode.concatToken(_currentToken);
      nextAnyToken();
    }
    return fillerNode;
  }

  //========================================================================================
  // Utilities
  //========================================================================================

  /*Concats current token to a string builder*/
  private void concatToken (StringBuilder val) {
    val.append(_currentToken.getValue());
  }

  //Used to create lambda functions for matching tokens
  private interface Matcher {
    boolean match();
  }

  private void expect(Matcher matcher) {
    if (!matcher.match()) expect(false);
  }

  private void expect(boolean b) {
    if (!b) error("Unexpected Token: " + _currentToken.toString());
  }

  /*assert an expectation for the current token then skip*/
  private void skip(boolean b) {
    expect(b);
    nextToken();
  }

  private void error(String errorMsg) {
    _programNode.addError(new Error(errorMsg));
  }

  /*Match single character punctuation*/
  private boolean match( char c )
  {
    return match(TokenType.PUNCTUATION, String.valueOf(c));
  }

  /*Match operators*/
  private boolean matchOperator(String val )
  {
    return match(TokenType.OPERATOR, val);
  }


  /*Match reserved keywords only*/
  private boolean matchKeyword(String val)
  {
    return match(TokenType.KEYWORD, val);
  }

  /*Matches conditional keywords such as "constructor", which are sometimes keywords within a class
   and identifiers otherwise*/
  private boolean matchClassKeyword(String val)
  {
    if (!match(TokenType.IDENTIFIER, val)) return false;
    //If these class keywords aren't followed by an identifier, treat them as regular identifiers
    if ((val.equals("static") || val.equals("get") || val.equals("set")) &&
            peekToken().getType() != TokenType.IDENTIFIER) return false;
    return  true;
  }

  private boolean match(TokenType type, String val) {
    return match(type) && _currentToken.getValue().equals(val);
  }

  private boolean match( TokenType type )
  {
    return (_currentToken.getType() == type);
  }


  private Tokenizer.Token peekToken() {
    if (_nextToken == null || _nextToken.getOffset() <= _currentToken.getOffset()) {
      _nextToken = _tokenizer.nextNonWhiteSpace();
    }
    return _nextToken;
  }

  private boolean isOverrideFunction(String functionName) {
    String packageName = _programNode.getPackageFromClassName(_classNode.getSuperClass());
    if (packageName == null) return false;
    IType superType = TypeSystem.getByFullName(packageName);
    if (superType == null) return false;
    for (IMethodInfo method : superType.getTypeInfo().getMethods()) {
      if (method.getDisplayName().equals(functionName)) return true;
    }
    return false;
  }

  /*Move current token to the next token (including whitespace)*/
  private void nextAnyToken() {
    _currentToken = _tokenizer.next();
  }

  /*Move current token to the next non-whitespace token*/
  private void nextToken()
  {
    if (_currentToken == null || _nextToken == null || _currentToken.getOffset() >= _nextToken.getOffset()) {
      _currentToken = _tokenizer.nextNonWhiteSpace();
    } else {
      _currentToken = _nextToken;
    }
  }
}