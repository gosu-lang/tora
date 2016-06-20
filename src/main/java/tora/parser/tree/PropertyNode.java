package tora.parser.tree;

import tora.parser.Tokenizer;

public class PropertyNode extends Node
{

  private String _args = "";
  private boolean _isSetter;

  public PropertyNode( String name, Tokenizer.Token start, Tokenizer.Token end )
  {
    super( name, start, end );
  }

  //Test Constructor
  public PropertyNode(String name, boolean isSetter, Tokenizer.Token start, Tokenizer.Token end) {
    super(name, start, end);
    _isSetter = isSetter;
  }

  public PropertyNode(String name, String args, boolean isSetter, Tokenizer.Token start, Tokenizer.Token end) {
    super(name, start, end);
    _isSetter = isSetter;
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
    return  (_isSetter?"set":"get") +
            ": function " + (_isSetter?"set":"get") + "(" + _args + ")" +
            functionBodyCode; //Should have one FunctionBodyNode child
  }

  public boolean isSetter() {
    return _isSetter;
  }
}
