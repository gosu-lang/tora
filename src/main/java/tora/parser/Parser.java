package tora.parser;

import tora.parser.tree.ClassNode;

public class Parser
{
  String _src;
  ClassNode _classNode;
  private Tokenizer _tokenizer;
  private Tokenizer.Token _currentToken;


  public boolean isES6Class() {
    return _classNode != null;
  }

  public void parse() {
    nextToken();
    parseClassStatement();
  }

  private void parseClassStatement()
  {
    if( match(TokenType.CLASS) != null )
    {
      Tokenizer.Token className = match( TokenType.IDENTIFIER );
      if( className != null )
      {
        _classNode = new ClassNode( className.getValue() );
        if( match( '{' ) != null )
        {
          parseClassBody();
          Tokenizer.Token end = match( '}' );
          _classNode.setTokens( className, end );
        }
      }
    }
  }

  private void parseClassBody()
  {
    // TODO
    // parse constructor
    // parse function definitions
    // parse properties
    // remember the static modifiers
    // include bodies
  }

  private Tokenizer.Token match( TokenType type )
  {
    if( _currentToken.getType() == type )
    {
      Tokenizer.Token t = _currentToken;
      nextToken();
      return t;
    }
    else
    {
      return null;
    }
  }


  private Tokenizer.Token match( char c )
  {
    if( _currentToken.getType() == TokenType.PUNCTUATION &&
        _currentToken.getValue().charAt( 0 ) == c )
    {
      Tokenizer.Token t = _currentToken;
      nextToken();
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