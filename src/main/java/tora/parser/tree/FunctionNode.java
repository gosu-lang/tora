package tora.parser.tree;


import tora.parser.Tokenizer;

public class FunctionNode extends Node
{

  Boolean _isStatic = false;

  public FunctionNode( String name, Tokenizer.Token start, Tokenizer.Token end )
  {
    super( name, start, end );
//    Find body start and end tokens
    //FunctionBodyNode bodyNode = new (String name, startToken, endToken);
  }

  //Test constructor
  public FunctionNode( String name, Tokenizer.Token start, Tokenizer.Token end, boolean isStatic )
  {
    super( name, start, end );
    _isStatic = isStatic;
  }


  @Override
  public String genCode()
  {

    String functionBodyCode;
    try {
      functionBodyCode = this.getChildren().get(0).genCode();
    } catch (IndexOutOfBoundsException e) {
      functionBodyCode = "{}";
    }
    return  "%s." + (_isStatic?"":"prototype.") + //If static, can be method of class directly
            getName() + " = " + "function()" + functionBodyCode;
  }
}
