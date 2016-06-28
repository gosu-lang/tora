package tora.parser.tree;


import tora.parser.Tokenizer;

public class FunctionNode extends Node
{

  private String _args = "";
  String _className;
  Boolean _isStatic = false;

  public FunctionNode( String name )
  {
    super( name );
  }

  //Test constructor
  public FunctionNode( String name, String className, boolean isStatic )
  {
    super( name );
    _className = className;
    _isStatic = isStatic;
  }

  //Test constructor
  public FunctionNode( String name, String className, String args, boolean isStatic )
  {
    super( name );
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
