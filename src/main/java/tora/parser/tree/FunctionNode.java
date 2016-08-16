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


  public void setReturnType(String returnType){  _returnType = returnType; }

  public String getReturnType() {return _returnType;}


  @Override
  public String genCode()
  {
    String parameterCode = (getFirstChild(ParameterNode.class) == null) ?
            "" : getFirstChild(ParameterNode.class).genCode();
    String functionBodyCode = (getFirstChild(FunctionBodyNode.class) == null) ?
            "{}" : getFirstChild(FunctionBodyNode.class).genCode();
      return "function " + getName() + "(" + parameterCode + ")" + functionBodyCode;
  }


  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FunctionNode)) return false;
    FunctionNode node = (FunctionNode) obj;
    return getName().equals(node.getName());
  }
}
