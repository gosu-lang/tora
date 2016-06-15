package tora.parser;


import org.junit.Assert;
import org.junit.Test;
import tora.parser.tree.ClassNode;
import tora.parser.tree.ConstructorNode;
import tora.parser.tree.FunctionNode;

public class CodeGenTest
{

  @Test
  public void testSimpleClassNode()
  {
    Assert.assertEquals("var Foo = {}", new ClassNode("Foo", null, null).genCode());
  }

  @Test
  public void testSimpleConstructorNode() { Assert.assertEquals("function Foo{}", new ConstructorNode("Foo",null, null ).genCode());}

  @Test
  public void testSimpleFunctionNode() {Assert.assertEquals("function Foo(){ System.out.println(\"Foo\")}", new FunctionNode("Foo", null, null).genCode());}

  @Test
  public void testSimplePropertyNode() {}


}
