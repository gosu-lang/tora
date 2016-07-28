package tora.parser.tree;


public class FunctionNode extends Node
{

  private String _className;
  private Boolean _isStatic = false;
  private Boolean _isOverride = false;
  private String _returnType = "dynamic.Dynamic";

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

  public FunctionNode( String name, String className)
  {
    super( name );
    _className = className;
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

  public void setReturnType(String returnType){  _returnType = returnType; }

  public String getReturnType() {return _returnType;}


  @Override
  public String genCode()
  {
    String parameterCode = (getFirstChild(ParameterNode.class) == null) ?
            "" : getFirstChild(ParameterNode.class).genCode();
    String functionBodyCode = (getFirstChild(FunctionBodyNode.class) == null) ?
            "{}" : getFirstChild(FunctionBodyNode.class).genCode();

    //If it's an override function, give as key value pair for Java.extend codegen from ClassNode
    if (isOverride()) {
      return getName() + ": function(" + parameterCode + ")" + functionBodyCode;
    }
    else return _className + (_isStatic?".":".prototype.") + //If static, can be method of class directly
            getName() + " = " + "function" + "(" + parameterCode + ")" + functionBodyCode;
  }


  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FunctionNode)) return false;
    FunctionNode node = (FunctionNode) obj;
    return getName().equals(node.getName()) && _isStatic == ((FunctionNode) obj).isStatic();
  }
}
