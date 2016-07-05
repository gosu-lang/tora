package tora.parser.tree;

public class ConstructorNode extends FunctionNode
{

  public ConstructorNode(String name )
  {
    super( name );
  }

  public ConstructorNode( String name, String className, String args)
  {
    super(name, className, args);
  }

  @Override
  public String genCode()
  {
    String functionBodyCode = getChildren().isEmpty()?"{}":getChildren().get(0).genCode();
    return   "function " + _name + "(" + getArgs() + ")" +
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
