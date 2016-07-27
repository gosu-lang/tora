package tora.parser.tree;


import tora.parser.Tokenizer;

public class FunctionBodyNode extends Node
{

  private String _content;
  public FunctionBodyNode( String content )
  {
    super( null );
    _content = content;
  }

}
