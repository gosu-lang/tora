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
    return   "function " + getName() + "(" + getArgs() + ")" +
            functionBodyCode.replaceFirst("[{]", "{ _classCallCheck(this," + getName() +
            ");" );
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ConstructorNode)) return false;
    ConstructorNode node = (ConstructorNode) obj;
    return getName().equals(node.getName());
  }
}
