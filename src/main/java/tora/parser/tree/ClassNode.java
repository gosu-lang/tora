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
    String membersCode = "";
    for (Node node : this.getChildren()) {
      if (node instanceof ConstructorNode) {

      }
      //Add class name to generated code for each child method, except for the constructor
      membersCode += "\n\t" + (node instanceof ConstructorNode?"":(_name + ".")) + node.genCode();
    }

    return "var " + getName() + " = function() { "
            + membersCode
            + "\n\treturn " + _name + ";\n}();" ;
  }
}
