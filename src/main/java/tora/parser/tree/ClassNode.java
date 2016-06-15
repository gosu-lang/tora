package tora.parser.tree;


import tora.parser.Tokenizer;

public class ClassNode extends Node
{
  public ClassNode( String name, Tokenizer.Token start, Tokenizer.Token end )
  {
    super( name, start, end );
  }

  @Override
  public String genCode()
  {
    return "var " + getName() + " = {}";
  }
}
