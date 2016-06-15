package tora.parser.tree;


import tora.parser.Tokenizer;

public class FunctionNode extends Node
{
  public FunctionNode( String name, Tokenizer.Token start, Tokenizer.Token end )
  {
    super( name, start, end );
  }

  @Override
  public String genCode()
  {
    return "TODO";
  }
}
