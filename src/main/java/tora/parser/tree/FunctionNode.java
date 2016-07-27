package tora.parser.tree;


public class FunctionNode extends Node
{

  private String _params = "";
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

  public FunctionNode( String name, String className, String params)
  {
    super( name );
    _className = className;
    _params = params;
  }

  public FunctionNode( String name, String className, String params, boolean isStatic )
  {
    super( name );
    _className = className;
    _isStatic = isStatic;
    _params = params;
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


  public String getParams() {
    return _params;
  }

  public void setParams(String params) {
    _params = params;
  }

  @Override
  public String genCode()
  {
    String functionBodyCode = getChildren().isEmpty()?"{}":getChildren().get(0).genCode();
    //If it's an override function, give as key value pair for Java.extend codegen from ClassNode
    if (isOverride()) {
      return getName() + ": function(" + _params + ")" + functionBodyCode;
    }
    else return _className + (_isStatic?".":".prototype.") + //If static, can be method of class directly
            getName() + " = " + "function" + "(" + _params + ")" + functionBodyCode;
  }


  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FunctionNode)) return false;
    FunctionNode node = (FunctionNode) obj;
    return getName().equals(node.getName()) && _isStatic == ((FunctionNode) obj).isStatic();
  }
}
