package tora.parser.tree;


import tora.parser.Tokenizer;

public class FunctionNode extends Node
{

  FunctionBodyNode _bodyNode;
  Boolean isStatic = false;

  public FunctionNode( String name, Tokenizer.Token start, Tokenizer.Token end )
  {
    super( name, start, end );
    _bodyNode = new FunctionBodyNode("System.out.println(\"Foo\")", null, null);
//    Find body start and end tokens
    //FunctionBodyNode bodyNode = new (String name, startToken, endToken);
    addChild(_bodyNode);

  }

  @Override
  public String genCode()
  {
    String response = "";
    if(isStatic) {
      response += "prototype.";
    }
    response += this._name + " = " + "function(){" + this._bodyNode.genCode() + "}";
    return response;
  }
}
