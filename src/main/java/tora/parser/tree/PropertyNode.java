package tora.parser.tree;

import tora.parser.Tokenizer;

public class PropertyNode extends Node
{

  private String _args = ""; //Should only ever have one argument for setters
  private boolean _isSetter;
  private boolean _isStatic;

  public PropertyNode( String name )
  {
    super( name );
  }

  //Test Constructor
  public PropertyNode(String name, boolean isSetter) {
    super(name);
    _isSetter = isSetter;
  }

  public PropertyNode(String name, String args, boolean isSetter) {
    super(name);
    _isSetter = isSetter;
    _args = args;
  }

  public PropertyNode(String name, String args, boolean isSetter, boolean isStatic) {
    super(name);
    _isSetter = isSetter;
    _args = args;
    _isStatic = isStatic;
  }

  public boolean isStatic() {
    return _isStatic;
  }

  public boolean isSetter() {
    return _isSetter;
  }

  public void setStatic(boolean isStatic) {
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
    return  (_isSetter?"set":"get") +
            ": function " + (_isSetter?"set":"get") + "(" + _args + ")" +
            functionBodyCode; //Should have one FunctionBodyNode child
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof PropertyNode)) return false;
    PropertyNode node = (PropertyNode) obj;
    return _name.equals(node.getName()) && _isStatic == node.isStatic() && _isSetter == node.isSetter() ;
  }

}
