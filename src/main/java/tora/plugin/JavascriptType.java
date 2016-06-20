package tora.plugin;

import gw.fs.IFile;
import gw.fs.IFileUtil;
import gw.lang.reflect.IType;
import gw.lang.reflect.ITypeInfo;
import gw.lang.reflect.ITypeLoader;
import gw.lang.reflect.TypeBase;
import gw.util.GosuExceptionUtil;
import gw.util.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by carson on 3/28/16.
 */
public class JavascriptType extends TypeBase implements IType
{
  private final String _name;
  private final JavascriptPlugin _typeloader;
  private final String _relativeName;
  private final String _package;
  private final String _src;
  private ITypeInfo _typeinfo;

  public JavascriptType( JavascriptPlugin typeloader, String name, IFile jsFile )
  {
    _name = name;
    _typeloader = typeloader;
    if( _name.indexOf( '.' ) > 0 )
    {
      _relativeName = _name.substring( _name.lastIndexOf( '.' ) );
      _package = _name.substring( 0, _name.lastIndexOf( '.' ) - 1);
    }
    else
    {
      _relativeName = _name;
      _package = "";
    }
    try
    {
      _src = StreamUtil.getContent( new InputStreamReader( jsFile.openInputStream() ) );
    }
    catch( IOException e )
    {
      throw GosuExceptionUtil.forceThrow( e );
    }
    _typeinfo = new JavascriptTypeInfo(this);
  }

  public String getSource() {
    return _src;
  }

  @Override
  public String getName()
  {
    return _name;
  }

  @Override
  public String getRelativeName()
  {
    return _relativeName;
  }

  @Override
  public String getNamespace()
  {
    return _package;
  }

  @Override
  public ITypeLoader getTypeLoader()
  {
    return _typeloader;
  }

  @Override
  public IType getSupertype()
  {
    return null;
  }

  @Override
  public IType[] getInterfaces()
  {
    return new IType[0];
  }

  @Override
  public ITypeInfo getTypeInfo()
  {
    return _typeinfo;
  }

  @Override
  public IFile[] getSourceFiles()
  {
    //TODO cgross - implmement?
    return new IFile[0];
  }
}
