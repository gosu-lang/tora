package tora.parser.tree;

public class PropertyNode extends FunctionNode
{
  private boolean _isSetter;

  public PropertyNode( String name )
  {
    super( name );
  }

  //Test Constructor
  public PropertyNode(String name, boolean isSetter) {
    super(name);
    _isSetter = isSetter;
  }

  public PropertyNode(String name, String className, String args, boolean isSetter) {
    super(name, className, args);
    _isSetter = isSetter;
  }

  public PropertyNode(String name, String className, String args, boolean isStatic, boolean isSetter) {
    super(name, className, args, isStatic);
    _isSetter = isSetter;
  }

  public boolean isSetter() {
    return _isSetter;
  }

  @Override
  public String genCode()
  {
    String functionBodyCode = getChildren().isEmpty()?"{}":getChildren().get(0).genCode();
    return  (_isSetter?"set":"get") +
            ": function " + (_isSetter?"set":"get") + "(" + getParams() + ")" +
            functionBodyCode; //Should have one FunctionBodyNode child
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof PropertyNode)) return false;
    PropertyNode node = (PropertyNode) obj;
    return getName().equals(node.getName()) && isStatic() == node.isStatic() && _isSetter == node.isSetter() ;
  }

}
