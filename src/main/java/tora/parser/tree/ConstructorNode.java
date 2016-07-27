package tora.parser.tree;

public class ConstructorNode extends FunctionNode
{

  public ConstructorNode(String name )
  {
    super( name );
  }

  public ConstructorNode(String name, String className)
  {
    super(name, className);
  }

  @Override
  public String genCode()
  {
    String parameterCode = (getFirstChild(ParameterNode.class) == null) ?
                              "" : getFirstChild(ParameterNode.class).genCode();
    String functionBodyCode = (getFirstChild(FunctionBodyNode.class) == null) ?
                              "{}" : getFirstChild(FunctionBodyNode.class).genCode();
    return   "function " + getName() + "(" + parameterCode + ")" +
            functionBodyCode.replaceFirst("[{]", "{\n\t _classCallCheck(this," + getName() +
            ");" );
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ConstructorNode)) return false;
    ConstructorNode node = (ConstructorNode) obj;
    return getName().equals(node.getName());
  }
}
