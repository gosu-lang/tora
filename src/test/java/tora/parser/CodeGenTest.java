package tora.parser;


import gw.config.CommonServices;
import gw.lang.Gosu;
import gw.lang.parser.*;
import gw.lang.reflect.ITypeLoader;
import gw.lang.reflect.TypeSystem;
import gw.util.GosuExceptionUtil;
import gw.util.SystemOutLogger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import tora.ToraBootstrap;
import tora.parser.tree.*;
import tora.plugin.JavascriptPlugin;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;

public class CodeGenTest
{
  private static ScriptEngine engine;
  @BeforeClass
  public static void beforeClass() {
    engine = new ScriptEngineManager().getEngineByName("nashorn");
  }

  @Test
  public void testSimpleClassNode()
  {
    Assert.assertEquals("var Foo = function() { \n\treturn Foo;\n}();", new ClassNode("Foo", null, null).genCode());
  }

  @Test
  public void testSimpleConstructorNode() {
    Assert.assertEquals("function Foo(){}",
            new ConstructorNode("Foo",null, null ).genCode());}

  @Test
  public void testSimpleFunctionBodyNode() {
    Assert.assertEquals("var foo = 15;",
            new FunctionBodyNode("var foo = 15;", null, null).genCode());
  }

  @Test
  public void testSimpleFunctionNode() {
    Assert.assertEquals("%s.prototype.Foo = function(){}",
          new FunctionNode("Foo", null, null).genCode());
  }

  @Test
  public void testSimplePropertyNode() {
    Assert.assertEquals("Object.defineProperty(%s.prototype, \"Foo\",{get:function(){}})",
            new PropertyNode("Foo", null, null).genCode());
  }

  @Test
  public void testClassConstruction() {
    ClassNode demoClass = new ClassNode("DemoClass", null, null);
    ConstructorNode demoConstructor = new ConstructorNode("DemoClass", null, null);
    demoClass.addChild(demoConstructor);
    FunctionBodyNode doh = new FunctionBodyNode("{ this.foo = 42; }", null, null);
    demoConstructor.addChild(doh);
    Assert.assertEquals("var DemoClass = function() { \n" +
            "\tfunction DemoClass(){ this.foo = 42; }\n" +
            "\treturn DemoClass;\n" +
            "}();", demoClass.genCode());
  }

  @Test
  public void testFunctionConstruction() {
    ClassNode demoClass = new ClassNode("DemoClass", null, null);
    FunctionNode bar = new FunctionNode("bar", null, null, false);
    PropertyNode doh = new PropertyNode("doh", null, null, false);
    demoClass.addChild(bar);
    demoClass.addChild(doh);
    bar.addChild(new FunctionBodyNode("{return this.foo;}", null, null));
    doh.addChild(new FunctionBodyNode("{return this.foo;}", null, null));
    Assert.assertEquals("var DemoClass = function() { \n" +
            "\tDemoClass.prototype.bar = function(){return this.foo;}\n" +
            "\tObject.defineProperty(DemoClass.prototype, \"doh\",{get:function(){return this.foo;}})\n" +
            "\treturn DemoClass;\n" +
            "}();", demoClass.genCode());

  }

  public Node makeSampleTree() {
    //Tree that should be generated from
    ClassNode demoClass = new ClassNode("DemoClass", null, null);
    ConstructorNode demoConstructor = new ConstructorNode("DemoClass", null, null);
    FunctionNode bar = new FunctionNode("bar", null, null, false);
    PropertyNode doh = new PropertyNode("doh", null, null, false);
    FunctionNode staticFoo = new FunctionNode("staticFoo", null, null, true);
    demoClass.addChild(demoConstructor);
    demoClass.addChild(bar);
    demoClass.addChild(doh);
    demoClass.addChild(staticFoo);
    demoConstructor.addChild(new FunctionBodyNode("{this.foo = 42;}", null, null));
    bar.addChild(new FunctionBodyNode("{return this.foo;}", null, null));
    doh.addChild(new FunctionBodyNode("{return this.foo;}", null, null));
    staticFoo.addChild(new FunctionBodyNode("{return 42;}",null,null));
    return demoClass;
  }

  @Test
  public void testClassNodeMembers() throws ScriptException, NoSuchMethodException {
    //TODO: write a test that's not just printing it out
    engine.eval(makeSampleTree().genCode());
    Assert.assertEquals(42,engine.eval("DemoClass.staticFoo()"));
    engine.eval("var dem = new DemoClass()");
    Assert.assertEquals(42,engine.eval("dem.bar()"));
    Assert.assertEquals(42,engine.eval("dem.doh"));
  }


}
