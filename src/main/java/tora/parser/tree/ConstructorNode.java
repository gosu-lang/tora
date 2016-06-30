package tora.parser.tree;


import tora.parser.Tokenizer;

public class ConstructorNode extends Node
{
  private String _args = "";

  public ConstructorNode(String name )
  {
    super( name );
  }

  public ConstructorNode( String name, String args, Tokenizer.Token start, Tokenizer.Token end)
  {
    super( name  );
    _args = args;
  }

  public String getArgs() {
    return _args;
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
    return   "function " + _name + "(" + _args + ")" +
            functionBodyCode.replaceFirst("[{]", "{ _classCallCheck(this," + _name +
            ");" );
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ConstructorNode)) return false;
    ConstructorNode node = (ConstructorNode) obj;
    return _name.equals(node.getName());
  }
}
