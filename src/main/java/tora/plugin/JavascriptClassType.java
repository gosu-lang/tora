package tora.plugin;

import gw.fs.IFile;
import gw.lang.reflect.ITypeInfo;
import tora.parser.Parser;

public class JavascriptClassType extends JavascriptTypeBase
{
  private final JavascriptClassTypeInfo _typeinfo;
  private final Parser _parser;

  public JavascriptClassType( JavascriptPlugin typeloader, String name, IFile jsFile, Parser parser )
  {
    super( typeloader, name, jsFile );
    _typeinfo = new JavascriptClassTypeInfo( this, parser );
    _parser = parser;
  }

  @Override
  public ITypeInfo getTypeInfo()
  {
    return _typeinfo;
  }
}
