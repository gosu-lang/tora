package tora.parser.tree;


import tora.parser.Tokenizer;

public class FunctionBodyNode extends Node
{

  public FunctionBodyNode( String name, Tokenizer.Token start, Tokenizer.Token end )
  {
    super( name, start, end );
  }

  @Override
  public String genCode()
  {
    //Does this need a name? Should it just be code?
    return this._name;
  }
}
