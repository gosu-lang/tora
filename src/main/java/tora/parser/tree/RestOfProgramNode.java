package tora.parser.tree;

/*(Hopefully) Temporary Node to hold parts of javascript code that we don't parse and blindly concatenate*/
public class RestOfProgramNode extends Node
{

  private String _content;
  public RestOfProgramNode(String content )
  {
    super( null );
    _content = content;
  }

  @Override
  public String genCode()
  {
    return _content;
  }
}
