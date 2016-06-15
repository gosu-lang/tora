package tora.parser.tree;


import tora.parser.Tokenizer;

public class FunctionNode extends Node
{

  FunctionBodyNode _bodyNode;

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
    return "function " + this._name + "(){ " + this._bodyNode.genCode() + "}";
  }
}
