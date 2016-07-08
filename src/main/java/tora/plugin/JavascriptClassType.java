package tora.plugin;

import gw.fs.IFile;
import gw.lang.reflect.ITypeInfo;
import tora.parser.Parser;
import tora.parser.tree.ProgramNode;

public class JavascriptClassType extends JavascriptTypeBase
{
  private final JavascriptClassTypeInfo _typeinfo;

  public JavascriptClassType(JavascriptPlugin typeloader, String name, IFile jsFile, ProgramNode programNode)
  {
    super( typeloader, name, jsFile );
    _typeinfo = new JavascriptClassTypeInfo( this, programNode );
  }

  @Override
  public ITypeInfo getTypeInfo()
  {
    return _typeinfo;
  }
}
