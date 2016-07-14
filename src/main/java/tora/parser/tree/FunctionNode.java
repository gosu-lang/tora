package tora.parser.tree;


import tora.parser.Tokenizer;

public class FunctionNode extends Node
{

  private String _args = "";
  private String _className;
  private Boolean _isStatic = false;
  private Boolean _isOverride = false;

  public FunctionNode( String name )
  {
    super( name );
  }

  //Test constructors
  public FunctionNode( String name, String className, boolean isStatic )
  {
    super( name );
    _className = className;
    _isStatic = isStatic;
  }

  public FunctionNode( String name, String className, String args)
  {
    super( name );
    _className = className;
    _args = args;
  }

  public FunctionNode( String name, String className, String args, boolean isStatic )
  {
    super( name );
    _className = className;
    _isStatic = isStatic;
    _args = args;
  }

  public boolean isStatic() {
    return _isStatic;
  }

  public void setStatic(boolean isStatic) {
    _isStatic = isStatic;
  }

  public boolean isOverride() {
    return _isOverride;
  }

  public void setOverride(boolean isOverride) {
    _isOverride = isOverride;
  }


  public String getArgs() {
    return _args;
  }

  public void setArgs(String args) {
    _args = args;
  }

  @Override
  public String genCode()
  {
    String functionBodyCode = getChildren().isEmpty()?"{}":getChildren().get(0).genCode();
    //If it's an override function, give as key value pair for Java.extend codegen from ClassNode
    if (isOverride()) {
      return getName() + ": function(" + _args + ")" + functionBodyCode;
    }
    else return _className + (_isStatic?".":".prototype.") + //If static, can be method of class directly
            getName() + " = " + "function" + "(" + _args + ")" + functionBodyCode;
  }


  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FunctionNode)) return false;
    FunctionNode node = (FunctionNode) obj;
    return getName().equals(node.getName()) && _isStatic == ((FunctionNode) obj).isStatic();
  }
}
