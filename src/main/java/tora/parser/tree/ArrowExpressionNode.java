package tora.parser.tree;

import tora.parser.Tokenizer;

public class ArrowExpressionNode extends Node
{

  private boolean _isExpression; //can be expression (implicit return) or statement (has curlies)
  private String _params = "";

  public ArrowExpressionNode()
  {
    super(null);
  }

  public void extractParams(FillerNode fillerNode) {
    Tokenizer.Token backToke = fillerNode.removeLastNonWhitespaceToken();
    //If prev token is not ')', then there is only one parameter
    if (!backToke.getValue().equals(")")) {
      _params = backToke.getValue();
      return;
    }
    //Otherwise, backtrack through list until opening parens
    backToke = fillerNode.removeLastToken();
    while (!(backToke.getValue().equals("("))) {
      _params = backToke.getValue() + _params;
      backToke = fillerNode.removeLastToken();
    }
  }

  @Override
  public String genCode()
  {
    /*For expressions, use Nashorn closure extension function (ex. function square(x) x*x;)*/
    return "function ("  + _params + ")" + super.genCode();
  }
}
