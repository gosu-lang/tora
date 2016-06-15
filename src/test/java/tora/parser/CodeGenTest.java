package tora.parser;


import org.junit.Assert;
import org.junit.Test;
import tora.parser.tree.*;

public class CodeGenTest
{
  public Node genSampleTree() {
    ClassNode demoClass = new ClassNode("DemoClass", null, null);
    ConstructorNode demoConstructor = new ConstructorNode("DemoClass", null, null);
    FunctionNode bar = new FunctionNode("bar", null, null);
    PropertyNode doh = new PropertyNode("doh", null, null);
    FunctionNode staticFoo = new FunctionNode("staticFoo", null, null);
    demoClass.addChild(demoConstructor);
    demoClass.addChild(bar);
    demoClass.addChild(doh);
    demoClass.addChild(staticFoo);
    return demoClass;
  }

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

  @Test
  public void testSimpleFunctionBodyNode()
  {
    Assert.assertEquals("var Foo = {}", new FunctionBodyNode("Foo", null, null).genCode());
  }

  @Test
  public void testClassNodeMembers()
  {
    System.out.println(genSampleTree().genCode());
  }

}
