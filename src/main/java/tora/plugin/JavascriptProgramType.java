package tora.plugin;

import gw.fs.IFile;
import gw.lang.reflect.ITypeInfo;

public class JavascriptProgramType extends JavascriptTypeBase
{
  private final JavascriptProgramTypeInfo _typeinfo;

  public JavascriptProgramType( JavascriptPlugin typeloader, String name, IFile jsFile )
  {
    super( typeloader, name, jsFile );
    _typeinfo = new JavascriptProgramTypeInfo( this );
  }

  @Override
  public ITypeInfo getTypeInfo()
  {
    return _typeinfo;
  }
}
