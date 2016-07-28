package tora.parser;


import gw.config.CommonServices;
import gw.lang.Gosu;
import gw.lang.parser.*;
import gw.lang.reflect.ITypeLoader;
import gw.lang.reflect.TypeSystem;
import gw.util.GosuExceptionUtil;
import gw.util.SystemOutLogger;
import jdk.nashorn.api.scripting.JSObject;
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
            "\tfunction Foo(){\n" +
            "\t _classCallCheck(this,Foo);}\n" +
            "\treturn Foo;\n" +
            "}();", new ClassNode("Foo").genCode());
  }

  @Test
  public void testSimpleConstructorNode() {
    Assert.assertEquals("function Foo(){\n" +
                    "\t _classCallCheck(this,Foo);}",
            new ConstructorNode("Foo").genCode());}


  @Test
  public void testSimpleFunctionNode() {
    Assert.assertEquals("Bar.prototype.Foo = function(){}",
          new FunctionNode("Foo", "Bar", false).genCode());
  }

  @Test
  public void testSimplePropertyNode() {
    Assert.assertEquals("get: function get(){}",
            new PropertyNode("Foo").genCode());
  }

  @Test
  public void testSimpleImportNode() {
    Assert.assertEquals("var Class = Java.type(\'package.name.javaclass.Class\')",
            new ImportNode("package.name.javaclass.Class").genCode());
  }

  @Test
  public void testPropertiesInClass() {
    //Make a class with a getter and setter of the same name
    ClassNode demoClass = new ClassNode("DemoClass");
    PropertyNode dohGet = new PropertyNode("doh", false);
    PropertyNode dohSet = new PropertyNode("doh", "DemoClass", false, true);
    ParameterNode dohSetParam = new ParameterNode();
    dohSetParam.addParam("d", null);
    dohSet.addChild(dohSetParam);
    demoClass.addChild(dohGet);
    demoClass.addChild(dohSet);
    dohGet.addChild(new FunctionBodyNode("").withChild(new FillerNode("{return this._doh;}")));
    dohSet.addChild(new FunctionBodyNode("").withChild(new FillerNode("{this._doh = d;}")));
    Assert.assertEquals("function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError(\"Cannot call a class as a function\") } }\n" +
            "var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if (\"value\" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();\n" +
            "var DemoClass = function() { \n" +
            "\tfunction DemoClass(){\n" +
            "\t _classCallCheck(this,DemoClass);}\n" +
            "\t_createClass(DemoClass, [\n" +
            "\t\t{key: \"doh\",set: function set(d){this._doh = d;},get: function get(){return this._doh;}}],null);\n" +
            "\treturn DemoClass;\n" +
            "}();", demoClass.genCode());
  }

  @Test
public void testStaticPropertiesInClass() {
  //Make a class with a getter and setter of the same name
  ClassNode demoClass = new ClassNode("DemoClass");
  PropertyNode dohGet = new PropertyNode("doh", false);
  dohGet.setStatic(true);
  PropertyNode dohSet = new PropertyNode("doh", "DemoClass", false, true);
  ParameterNode dohSetParam = new ParameterNode();
  dohSetParam.addParam("d", null);
  dohSet.addChild(dohSetParam);
  dohSet.setStatic(true);
  demoClass.addChild(dohGet);
  demoClass.addChild(dohSet);
  dohGet.addChild(new FunctionBodyNode("").withChild(new FillerNode("{return this._doh;}")));
  dohSet.addChild(new FunctionBodyNode("").withChild(new FillerNode("{this._doh = d;}")));
  Assert.assertEquals("function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError(\"Cannot call a class as a function\") } }\n" +
          "var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if (\"value\" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();\n" +
          "var DemoClass = function() { \n" +
          "\tfunction DemoClass(){\n" +
          "\t _classCallCheck(this,DemoClass);}\n" +
          "\t_createClass(DemoClass, null,[\n" +
          "\t\t{key: \"doh\",set: function set(d){this._doh = d;},get: function get(){return this._doh;}}]);\n" +
          "\treturn DemoClass;\n" +
          "}();", demoClass.genCode());
}

  @Test
  public void testClassConstruction() {
    ClassNode demoClass = new ClassNode("DemoClass");
    ConstructorNode demoConstructor = new ConstructorNode("DemoClass");
    demoClass.addChild(demoConstructor);
    demoConstructor.addChild(new FunctionBodyNode("").withChild(new FillerNode("{ this.foo = 42; }")));
    Assert.assertEquals("function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError(\"Cannot call a class as a function\") } }\n" +
            "var DemoClass = function() { \n" +
            "\tfunction DemoClass(){\n" +
            "\t _classCallCheck(this,DemoClass); this.foo = 42; }\n" +
            "\treturn DemoClass;\n" +
            "}();", demoClass.genCode());
  }


  @Test
  public void testFunctionConstruction() {
    ClassNode demoClass = new ClassNode("DemoClass");
    FunctionNode bar = new FunctionNode("bar", "DemoClass", false);
    PropertyNode doh = new PropertyNode("doh", false);
    demoClass.addChild(bar);
    demoClass.addChild(doh);
    FunctionBodyNode barBody = new FunctionBodyNode("bar");
    barBody.addChild(new FunctionBodyNode("").withChild(new FillerNode("{return this.foo;}")));
    bar.addChild(barBody);
    doh.addChild(barBody);
    Assert.assertEquals("function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError(\"Cannot call a class as a function\") } }\n" +
            "var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if (\"value\" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();\n" +
            "var DemoClass = function() { \n" +
            "\tfunction DemoClass(){\n" +
            "\t _classCallCheck(this,DemoClass);}\n" +
            "\tDemoClass.prototype.bar = function(){return this.foo;}\n" +
            "\t_createClass(DemoClass, [\n" +
            "\t\t{key: \"doh\",get: function get(){return this.foo;}}],null);\n" +
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
    ClassNode demoClass = new ClassNode("DemoClass");
    ConstructorNode demoConstructor = new ConstructorNode("DemoClass");
    FunctionNode bar = new FunctionNode("bar", "DemoClass", false);
    PropertyNode dohSet = new PropertyNode("doh", "DemoClass", false, true);
    ParameterNode dohSetParam = new ParameterNode();
    dohSetParam.addParam("d", null);
    dohSet.addChild(dohSetParam);
    PropertyNode dohGet = new PropertyNode("doh", false);
    PropertyNode pohSet = new PropertyNode("poh", "DemoClass", false, true);
    ParameterNode pohSetParam = new ParameterNode();
    pohSetParam.addParam("p", null);
    pohSet.addChild(pohSetParam);
    PropertyNode pohGet = new PropertyNode("poh", false);
    FunctionNode staticFoo = new FunctionNode("staticFoo", "DemoClass", true);
    demoClass.addChild(demoConstructor);
    demoClass.addChild(bar);
    demoClass.addChild(dohSet);
    demoClass.addChild(dohGet);
    demoClass.addChild(pohSet);
    demoClass.addChild(pohGet);
    demoClass.addChild(staticFoo);
    demoConstructor.addChild(new FunctionBodyNode("").withChild(new FillerNode("{this.foo = 42;}")));
    bar.addChild(new FunctionBodyNode("").withChild(new FillerNode("{return this.foo;}")));
    dohGet.addChild(new FunctionBodyNode("").withChild(new FillerNode("{return this.foo;}")));
    dohSet.addChild(new FunctionBodyNode("").withChild(new FillerNode("{this.halfDoh = d/2;}")));
    pohGet.addChild(new FunctionBodyNode("").withChild(new FillerNode("{return this.doh;}")));
    pohSet.addChild(new FunctionBodyNode("").withChild(new FillerNode("{this.doublePoh = p*2;}")));
    staticFoo.addChild(new FunctionBodyNode("").withChild(new FillerNode("{return 42;}")));
    return demoClass;
  }

  @Test
  public void testClassNodeMembers() throws ScriptException, NoSuchMethodException {
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
