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
    String functionBodyCode;
    try {
      functionBodyCode = this.getChildren().get(0).genCode();
    } catch (IndexOutOfBoundsException e) {
      functionBodyCode = "(){}";
    }
    return   "function " + _name + functionBodyCode;
  }
}
