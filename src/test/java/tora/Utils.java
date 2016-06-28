package tora;

import gw.lang.Gosu;
import gw.lang.reflect.ITypeLoader;
import gw.lang.reflect.TypeSystem;
import tora.plugin.JavascriptPlugin;

public class Utils
{
  private static boolean _initializedGosu;

  public static void maybeInit()
  {
    if( !_initializedGosu )
    {
      Gosu.init();
      ITypeLoader javascriptPlugin = new JavascriptPlugin( TypeSystem.getCurrentModule()); // global vs. current?
      TypeSystem.pushTypeLoader(TypeSystem.getGlobalModule(), javascriptPlugin);
      javascriptPlugin.init();
      _initializedGosu = true;
    }
  }
}
