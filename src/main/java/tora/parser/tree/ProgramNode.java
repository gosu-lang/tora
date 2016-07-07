package tora.parser.tree;


public class ProgramNode extends Node
{

  public ProgramNode()
  {
    super( null );
  }

  @Override
  public String genCode()
  {
    StringBuilder code = new StringBuilder(); //Makes sure constructor is called correctly

    for (ImportNode node : getChildren(ImportNode.class)) {
      code.append("\n").append(node.genCode());
    }

    for (ClassNode node : getChildren(ClassNode.class)) {
      code.append("\n").append(node.genCode()); //Should be limited to one class per program node
    }

    return code.toString();
  }
}
