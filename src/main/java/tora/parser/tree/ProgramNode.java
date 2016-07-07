package tora.parser.tree;


import java.util.LinkedList;
import java.util.List;

public class ProgramNode extends Node
{
  private List<Error> _errorList;

  public void addError(Error error) {
    _errorList.add(error);
  }

  public int errorCount() {
    return _errorList.size();
  }

  public List<Error> getErrorList() { return _errorList; }

  public ProgramNode()
  {
    super( null );
    _errorList = new LinkedList<>();
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
