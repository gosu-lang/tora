package tora.parser.tree;


public class ImportNode extends Node
{

  public ImportNode(String packageName )
  {
    super( packageName );
  }


  @Override
  public String genCode()
  {
    int lastDotIndex = getName().lastIndexOf('.') + 1;
    if (lastDotIndex < 0) lastDotIndex = 0;
    String importedClass = getName().substring(lastDotIndex);
    return "var " + importedClass + " = Java.type(\'" + getName() + "\')";
  }


  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ImportNode)) return false;
    ImportNode node = (ImportNode) obj;
    return getName() != node.getName();
  }
}
