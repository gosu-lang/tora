package tora.parser.tree;


import tora.parser.Tokenizer;

public class ClassNode extends Node
{
  public ClassNode( String name, Tokenizer.Token start, Tokenizer.Token end )
  {
    super( name, start, end );
  }

  @Override
  public String genCode()
  {
    String childrenCode = "";
    for (Node node : this.getChildren()) {
      childrenCode += "\n\t";
      childrenCode += String.format(node.genCode(), _name); //Insert class name if children require it
    }

    return "var " + getName() + " = function() { "
            + childrenCode
            + "\n\treturn " + getName() + ";\n}();" ;
  }
}
