package tora.parser.tree;


import tora.parser.Tokenizer;

public class FunctionNode extends Node
{

  private String _args = "";
  String _className;
  Boolean _isStatic = false;

  public FunctionNode( String name, Tokenizer.Token start, Tokenizer.Token end )
  {
    super( name, start, end );
//    Find body start and end tokens
    //FunctionBodyNode bodyNode = new (String name, startToken, endToken);
  }

  //Test constructor
  public FunctionNode( String name, String className, boolean isStatic, Tokenizer.Token start, Tokenizer.Token end)
  {
    super( name, start, end );
    _className = className;
    _isStatic = isStatic;
  }

  //Test constructor
  public FunctionNode( String name, String className, String args, boolean isStatic, Tokenizer.Token start, Tokenizer
          .Token end)
  {
    super( name, start, end );
    _className = className;
    _isStatic = isStatic;
    _args = args;
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
    return  _className + (_isStatic?".":".prototype.") + //If static, can be method of class directly
            getName() + " = " + "function" + "(" + _args + ")" + functionBodyCode;
  }
}
