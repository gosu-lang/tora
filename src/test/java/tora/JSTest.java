package tora;


import org.junit.Test;

import static org.junit.Assert.*;
import static tora.JS.*;

public class JSTest
{

  @Test
  public void basicEval()
  {
    assertEquals( 1, JS.eval( "1" ) );
    assertEquals( 2, JS.eval( "1 + 1" ) );
  }

  @Test
  public void zeroArgScriptBuilding()
  {
    JSScript0<Integer> script = JS.buildScript( "2 + 1", Integer.class );
    assertEquals( 3, script.eval().intValue() );
  }

  @Test
  public void oneArgScriptBuilding()
  {
    JSScript1<Double, Integer> script = JS.buildScript( "2 + foo", Double.class,
                                                        param( Integer.class, "foo" ) );
    assertEquals( new Double(3), script.eval(1) );
  }

  @Test
  public void twoArgScriptBuilding()
  {
    JSScript2<Double, Integer, Integer> script = JS.buildScript( "bar / foo", Double.class,
                                                                 param( Integer.class, "foo" ),
                                                                 param( Integer.class, "bar" ) );
    assertEquals( new Double(3.0/1.0), script.eval(1, 3) );
    assertEquals( new Double(1.0/3.0), script.eval(3, 1) );
  }

}
