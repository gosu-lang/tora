package tora.parser.tree;

import tora.parser.Tokenizer;

public class PropertyNode extends Node
{

  boolean _isSetter;

  public PropertyNode( String name, Tokenizer.Token start, Tokenizer.Token end )
  {
    super( name, start, end );
  }

  //Test Constructor
  public PropertyNode(String name, Tokenizer.Token start, Tokenizer.Token end, boolean isSetter) {
    super(name, start, end);
    _isSetter = isSetter;
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
    return "Object.defineProperty(%s.prototype, \"" + _name + "\"," +
            "{" + (_isSetter?"set":"get") +
            ":function()" + functionBodyCode + "})"; //Should have one FunctionBodyNode child
  }
}
