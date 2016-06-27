package tora.parser.tree;


import tora.parser.Tokenizer;

public class FunctionBodyNode extends Node
{

  private String _content;
  public FunctionBodyNode( String content, Tokenizer.Token start, Tokenizer.Token end )
  {
    super( null, start, end );
    _content = content;
  }

  @Override
  public String genCode()
  {
    return _content;
  }
}
