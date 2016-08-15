package tora.parser.tree.template;


import tora.parser.tree.ImportNode;
import tora.parser.tree.Node;
import tora.parser.tree.ParameterNode;

/*Holds template literals inside javascript files. Supports interpolation and multiline characters*/

public class TemplateLiteralNode extends Node
{

  public TemplateLiteralNode()
  {
    super( null );
  }

  @Override
  public String genCode()
  {
    StringBuilder string = new StringBuilder();
    for (Node node:getChildren()) {
      if (node != getChildren().get(0)) string.append("+");
      if (node instanceof RawStringNode) {
        string.append("\"").append(node.genCode()).append("\"");
      } else if (node instanceof ExpressionNode) {
        string.append("(").append(node.genCode()).append(")");
      }
    }
    return string.toString();
  }

}
