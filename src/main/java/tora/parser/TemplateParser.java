package tora.parser;

import tora.parser.tree.*;
import tora.parser.tree.template.ExpressionNode;
import tora.parser.tree.template.RawStringNode;
import tora.parser.tree.template.StatementNode;
import tora.parser.tree.template.TemplateNode;

public class TemplateParser extends Parser
{
  private TemplateNode _templateNode;

  public TemplateParser(Tokenizer tokenizer) {
    super(tokenizer);
  }

  @Override
  public Node parse() {
    nextToken();
    _templateNode = new TemplateNode();
    while (!match(TokenType.EOF)) {
      if (match(TokenType.RAWSTRING)) {
        _templateNode.addChild(new RawStringNode(currToken().getValue()));
        nextToken();
      } else if (matchTemplatePunc( "<%@")) {
        _templateNode.addChild(parseTemplateParams());
        nextToken();
      } else if (matchTemplatePunc( "<%=") || matchTemplatePunc( "${")) {
        _templateNode.addChild(parseTemplateExpression());
      } else if (matchTemplatePunc( "<%")) {
        _templateNode.addChild(parseTemplateStatement());
      }
    }
    ParameterNode parameterNode = new ParameterNode();
    parameterNode.addParam("sup", null);
    _templateNode.addChild(parameterNode);
    return _templateNode;
  }

  private Node parseTemplateStatement() {
    StatementNode statementNode = new StatementNode();
    Tokenizer.Token startToken = currToken();
    skip(matchTemplatePunc( "<%"));
    statementNode.addChild(parseFillerUntil(() -> matchTemplatePunc("%>")));
    expect(matchTemplatePunc("%>"));
    statementNode.setTokens(startToken, currToken());
    nextToken();
    return statementNode;
  }

  private Node parseTemplateExpression() {
    ExpressionNode expressionNode = new ExpressionNode();
    Tokenizer.Token startToken = currToken();
    //Expressions either start with <% and and with %>; or start with ${ and end with }
    String exitString = matchTemplatePunc( "<%=") ? "%>" : "}";
    skip(matchTemplatePunc( "<%=") || matchTemplatePunc( "${"));
    expressionNode.addChild(parseFillerUntil(() -> matchTemplatePunc( exitString)));
    expect(matchTemplatePunc( "%>"));
    expressionNode.setTokens(startToken, currToken());
    nextToken();
    return expressionNode;
  }

  private Node parseTemplateParams() {
    skip(match(TokenType.TEMPLATEPUNC));
    if (match(TokenType.IDENTIFIER, "params")) {
      nextToken();
      return parseParams();
    } else if (match(TokenType.KEYWORD, "import")) {
      return parseImport();
    }
    return null;
  }

  /*Match template specific punctuation only*/
  private boolean matchTemplatePunc(String val)
  {
    return match(TokenType.TEMPLATEPUNC, val);
  }

}