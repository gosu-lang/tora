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
    Assert.assertEquals("function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError(\"Cannot call a class as a function\") } }\n" +
            "var Foo = function() { \n" +
            "\tfunction Foo(){ _classCallCheck(this,Foo);}\n" +
            "\treturn Foo;\n" +
            "}();", new ClassNode("Foo", null, null).genCode());
  }

  @Test
  public void testSimpleConstructorNode() {
    Assert.assertEquals("function Foo(){ _classCallCheck(this,Foo);}",
            new ConstructorNode("Foo",null, null ).genCode());}

  @Test
  public void testSimpleFunctionBodyNode() {
    Assert.assertEquals("var foo = 15;",
            new FunctionBodyNode("var foo = 15;", null, null).genCode());
  }

  @Test
  public void testSimpleFunctionNode() {
    Assert.assertEquals("Bar.prototype.Foo = function(){}",
          new FunctionNode("Foo", "Bar", false, null, null).genCode());
  }

  @Test
  public void testSimplePropertyNode() {
    Assert.assertEquals("get: function get(){}",
            new PropertyNode("Foo", null, null).genCode());
  }

  @Test
  public void testClassConstruction() {
    ClassNode demoClass = new ClassNode("DemoClass", null, null);
    ConstructorNode demoConstructor = new ConstructorNode("DemoClass", null, null);
    demoClass.addChild(demoConstructor);
    FunctionBodyNode doh = new FunctionBodyNode("{ this.foo = 42; }", null, null);
    demoConstructor.addChild(doh);
    Assert.assertEquals("function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError(\"Cannot call a class as a function\") } }\n" +
            "var DemoClass = function() { \n" +
            "\tfunction DemoClass(){ _classCallCheck(this,DemoClass); this.foo = 42; }\n" +
            "\treturn DemoClass;\n" +
            "}();", demoClass.genCode());
  }

  @Test
  public void testFunctionConstruction() {
    ClassNode demoClass = new ClassNode("DemoClass", null, null);
    FunctionNode bar = new FunctionNode("bar", "DemoClass", false, null, null);
    PropertyNode doh = new PropertyNode("doh", false, null, null);
    demoClass.addChild(bar);
    demoClass.addChild(doh);
    bar.addChild(new FunctionBodyNode("{return this.foo;}", null, null));
    doh.addChild(new FunctionBodyNode("{return this.foo;}", null, null));
    Assert.assertEquals("function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError(\"Cannot call a class as a function\") } }\n" +
            "var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if (\"value\" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();\n" +
            "var DemoClass = function() { \n" +
            "\tfunction DemoClass(){ _classCallCheck(this,DemoClass);}\n" +
            "\tDemoClass.prototype.bar = function(){return this.foo;}\n" +
            "\t_createClass(DemoClass, [\n" +
            "\t\t{key: \"doh\",get: function get(){return this.foo;}}\n" +
            "\t]);\n" +
            "\treturn DemoClass;\n" +
            "}();", demoClass.genCode());

  }

  public Node makeSampleTree() {
    //Tree based on Tora Conversion Example

//    class DemoClass {
//
//      // constructor definition
//      constructor() {
//        this.foo = 42;
//      }
//
//      // function definition
//      bar() {
//        return this.foo;
//      }
//
//
//      set doh(d) {
//        this.halfDoh = d/2;
//      }
//
//      // property definition
//      get doh() {
//        return this.foo;
//      }
//
//      set poh(d) {
//        this.doublePoh = d * 2;
//      }
//
//      get poh() {
//        return this.doh;
//      }
//
//
//      // static function definition
//      static staticFoo() {
//        return 42;
//      }
//    }
    ClassNode demoClass = new ClassNode("DemoClass", null, null);
    ConstructorNode demoConstructor = new ConstructorNode("DemoClass", null, null);
    FunctionNode bar = new FunctionNode("bar", "DemoClass", false, null, null);
    PropertyNode dohSet = new PropertyNode("doh", "d", true, null, null);
    PropertyNode dohGet = new PropertyNode("doh", false, null, null);
    PropertyNode pohSet = new PropertyNode("poh", "p", true, null, null);
    PropertyNode pohGet = new PropertyNode("poh", false, null, null);
    FunctionNode staticFoo = new FunctionNode("staticFoo", "DemoClass", true, null, null);
    demoClass.addChild(demoConstructor);
    demoClass.addChild(bar);
    demoClass.addChild(dohSet);
    demoClass.addChild(dohGet);
    demoClass.addChild(pohSet);
    demoClass.addChild(pohGet);
    demoClass.addChild(staticFoo);
    demoConstructor.addChild(new FunctionBodyNode("{this.foo = 42;}", null, null));
    bar.addChild(new FunctionBodyNode("{return this.foo;}", null, null));
    dohGet.addChild(new FunctionBodyNode("{return this.foo;}", null, null));
    dohSet.addChild(new FunctionBodyNode("{this.halfDoh = d/2;}", null, null));
    pohGet.addChild(new FunctionBodyNode("{return this.doh;}", null, null));
    pohSet.addChild(new FunctionBodyNode("{this.doublePoh = p*2;}", null, null));
    staticFoo.addChild(new FunctionBodyNode("{return 42;}",null,null));
    return demoClass;
  }

  @Test
  public void testClassNodeMembers() throws ScriptException, NoSuchMethodException {
    System.out.println(makeSampleTree().genCode());
    engine.eval(makeSampleTree().genCode());
    Assert.assertEquals(42,engine.eval("DemoClass.staticFoo()"));
    engine.eval("var dem = new DemoClass()");
    Assert.assertEquals(42,engine.eval("dem.bar()"));
    Assert.assertEquals(42,engine.eval("dem.doh"));
    engine.eval("dem.doh = 42");
    Assert.assertEquals(21.0,engine.eval("dem.halfDoh"));
    Assert.assertEquals(42,engine.eval("dem.poh"));
    engine.eval("dem.poh = 42");
    Assert.assertEquals(84l,engine.eval("dem.doublePoh"));
  }


}
