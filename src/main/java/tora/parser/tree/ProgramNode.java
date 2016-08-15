package tora.parser.tree;


import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

  /*Returns the full package name of imported class (ex. java.util.ArrayList)
   *from class name (ex. ArrayList)*/
   public String getPackageFromClassName(String packageClass) {
    for (ImportNode node: getChildren(ImportNode.class)) {
      if (node.getPackageClass().equals(packageClass))
        return node.getName();
    }
     return null;
  }

}
