package tora.plugin;

import gw.fs.IFile;
import gw.lang.reflect.ITypeInfo;

public class JavascriptProgramType extends JavascriptTypeBase
{
  private final JavascriptClassTypeInfo _typeinfo;

  public JavascriptProgramType( JavascriptPlugin typeloader, String name, IFile jsFile )
  {
    super( typeloader, name, jsFile );
    _typeinfo = new JavascriptClassTypeInfo( this, parser );
  }

  @Override
  public ITypeInfo getTypeInfo()
  {
    return _typeinfo;
  }
}
