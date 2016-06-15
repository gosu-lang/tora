package tora.parser;


import org.junit.Assert;
import org.junit.Test;
import tora.parser.tree.ClassNode;

public class CodeGenTest
{

  @Test
  public void testSimpleClassNode()
  {
    Assert.assertEquals("var Foo = {}", new ClassNode("Foo", null, null).genCode());
  }

}
