package tora.parser.tree.template;


import tora.parser.tree.ImportNode;
import tora.parser.tree.Node;
import tora.parser.tree.ParameterNode;

import java.util.LinkedList;
import java.util.List;

/*Serves as the root for template files (.jst) . Generates a function with the template parameters as well as a
placeholder
parameter for the raw strings that will be passed in when called from the call handler
 */

public class JSTNode extends Node
{
  //boiler plate code for constructing string
  private final String STR_BUILDER = "_strTemplateBuilder"; //javascript variable that builds and returns string
  private final String RAW_STR_LIST = "_rawStrList"; //parameter of raw strings inputted into the template

  private String TEMPLATE_HEADER1 = "function renderToString("; //name of function; leaves room for parameters
  private String TEMPLATE_HEADER2 = RAW_STR_LIST + ") {" + STR_BUILDER + " = '';"; //initializes str builder

  private final String TEMPLATE_FOOTER =
          "\n\treturn " + STR_BUILDER + ";\n}";

  public JSTNode()
  {
    super( null );
  }

  @Override
  public String genCode()
  {
    String parameterCode = getFirstChild(ParameterNode.class).genCode();
    //If there are parameters, add a comma since the function always also takes the raw string parameter
    if (parameterCode.length() > 0) parameterCode = parameterCode + ",";

    StringBuilder code = new StringBuilder();
    for (ImportNode node : getChildren(ImportNode.class)) {
      code.append("\n").append(node.genCode());
    }

    //Add the header and parameters for the generated function
    code.append("\n").append(TEMPLATE_HEADER1).append(parameterCode).append(TEMPLATE_HEADER2);

    //Keep track of number of raw strings added to know which index of the raw string array to add
    int rawStringCount = 0;
    for (Node node:getChildren()) {
      if (node instanceof RawStringNode) {
        addRawString(code, rawStringCount);
        rawStringCount++;
      } else if (node instanceof ExpressionNode) {
        addExpression(code, (ExpressionNode) node);
      } else if (node instanceof StatementNode) {
        addStatement(code, (StatementNode) node);
      }
    }
    code.append(TEMPLATE_FOOTER);
    return code.toString();
  }

  //Add whatever the expression evaluates to into the generated code
  private void addExpression(StringBuilder code, ExpressionNode node) {
    code.append("\n\t").append(STR_BUILDER)
            .append(" += ")
            .append(node.genCode());
  }

  //Statement code not directly included in template output; instead is simply added into the genCode logic
  private void addStatement(StringBuilder code, StatementNode node) {
    code.append("\n").append(node.genCode());
  }

  /*raw strings will be passed into the function as a list, so just add the element from the argument list*/
  private void addRawString(StringBuilder code, int count) {
    code.append("\n\t").append(STR_BUILDER)
            .append(" += ")
            .append(RAW_STR_LIST)
            .append("[" + count + "]");
  }
}
