package tora.plugin;

import gw.fs.IDirectory;
import gw.lang.reflect.IType;
import gw.lang.reflect.RefreshKind;
import gw.lang.reflect.TypeLoaderBase;
import gw.lang.reflect.module.IModule;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JavascriptPlugin extends TypeLoaderBase
{

  public JavascriptPlugin( IModule currentModule )
  {
    super(currentModule);
  }

  @Override
  public IType getType( String s )
  {
    InputStream jsFile = getClass().getResourceAsStream( toJavascriptFile( s ) );
    if( jsFile != null )
    {
      return new JavascriptType(this, s, jsFile);
    }
    else
    {
      return null;
    }
  }

  private String toJavascriptFile( String s )
  {
    return "/" + s.replace( ".", "/" ) + ".js";
  }

  @Override
  public Set<? extends CharSequence> getAllNamespaces()
  {
    return Collections.EMPTY_SET;
  }

  @Override
  public List<String> getHandledPrefixes()
  {
    return Collections.emptyList();
  }

  @Override
  public boolean handlesNonPrefixLoads()
  {
    return true;
  }

  @Override
  public void refreshedNamespace( String s, IDirectory iDirectory, RefreshKind refreshKind )
  {
      //TODO cgross - implement
  }

  @Override
  public boolean hasNamespace( String s )
  {
    return getAllNamespaces().contains(s);
  }

  @Override
  public Set<String> computeTypeNames()
  {
    return new HashSet<>();
  }
}
