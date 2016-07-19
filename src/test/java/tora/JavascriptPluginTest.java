package tora;

import gw.config.CommonServices;
import gw.lang.parser.*;
import gw.lang.reflect.IType;
import gw.lang.reflect.TypeSystem;
import gw.util.GosuExceptionUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class JavascriptPluginTest
{
  @BeforeClass
  public static void beforeClass() {
    Utils.maybeInit();
  }


  @Test
  public void method() {
    assertEquals(42,eval("var dem = new DemoClass(); return dem.bar()"));
  }
  @Test
  public void constructorArgsTest() {
    assertEquals(5,eval("var dem = new DemoClass(5); return dem.cons"));
  }

  @Test
  public void propertyInMethodTest() {
    assertEquals(105.0,eval("var dem = new DemoClass(5);  return dem.fcal()"));
    assertEquals(20.0,eval("var dem = new DemoClass(5); dem.poh = 10; dem.doh = 10; return dem.fcal()"));
  }
  @Test
  public void methodWithArgs() {
    assertEquals(42.0,eval("var dem = new DemoClass(); return dem.sum(20,22)"));
  }
  @Test
  public void staticMethod() {
    assertEquals(42, eval("DemoClass.staticFoo()"));
  }

  @Test
  public void propertySetAndGet() {
    assertEquals(80,eval("var dem = new DemoClass(); dem.poh = 80; return dem.poh"));
  }

  @Test
  public void staticPropertySetAndGet() {
    assertEquals(40,eval("var dem = new DemoClass(); DemoClass.staticPoh = 40; return DemoClass.staticPoh"));
  }

  @Test
  public void topLevelVarIsPreservedAcrossInvocations() {
    assertEquals(0.0, eval("CounterVar.nextNum()"));
    assertEquals(1.0, eval("CounterVar.nextNum()"));
  }

  private Object eval( String program )
  {
    IGosuProgramParser programParser = CommonServices.getGosuParserFactory().createProgramParser();
    try
    {
      IParseResult iParseResult = programParser.parseExpressionOrProgram( program, new StandardSymbolTable( true ), new ParserOptions() );
      return iParseResult.getProgram().evaluate( new ExternalSymbolMapForMap( new HashMap<>() ) );
    }
    catch( Exception e )
    {
      throw GosuExceptionUtil.forceThrow( e );
    }
  }

}
