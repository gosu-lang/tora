package tora.parser.tree;


import tora.parser.Tokenizer;

public class PropertyNode extends Node
{
  public PropertyNode( String name, Tokenizer.Token start, Tokenizer.Token end )
  {
    super( name, start, end );
  }

  @Override
  public String genCode()
  {
    return "TODO";
  }
}
