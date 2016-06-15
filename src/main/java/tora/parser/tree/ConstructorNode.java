package tora.parser.tree;


import tora.parser.Tokenizer;

public class ConstructorNode extends Node
{
  public ConstructorNode( String name, Tokenizer.Token start, Tokenizer.Token end )
  {
    super( name, start, end );
  }

  @Override
  public String genCode()
  {
    return "function " + _name + "(){}";
  }
}
