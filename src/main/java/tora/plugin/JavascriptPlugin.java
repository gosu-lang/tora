package tora.plugin;

import gw.config.CommonServices;
import gw.fs.IDirectory;
import gw.fs.IFile;
import gw.lang.reflect.IType;
import gw.lang.reflect.RefreshKind;
import gw.lang.reflect.RefreshRequest;
import gw.lang.reflect.TypeLoaderBase;
import gw.lang.reflect.TypeSystem;
import gw.lang.reflect.module.IModule;
import gw.util.Pair;
import gw.util.StreamUtil;
import gw.util.concurrent.LockingLazyVar;
import tora.parser.Parser;
import tora.parser.Tokenizer;
import tora.parser.tree.ProgramNode;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavascriptPlugin extends TypeLoaderBase
{

  private static final String JS_EXTENSION = ".js";
  private Set<String> _namespaces;

  private final LockingLazyVar<Map<String, IFile>> _jsSources = new LockingLazyVar<Map<String, IFile>>() {
    @Override
    protected Map<String, IFile> init() {
      Map<String, IFile> result = new HashMap<>();
      for(Pair<String, IFile> p : findAllFilesByExtension( JS_EXTENSION)) {
        IFile file = p.getSecond();
        String fileName = p.getFirst();
        String fqn = fileName.substring(0, fileName.length() - JS_EXTENSION.length()).replace('/', '.');
        result.put(fqn, file);
      }
      return result;
    }
  };

  public List<Pair<String, IFile>> findAllFilesByExtension(String extension) {
    List<Pair<String, IFile>> results = new ArrayList<>();

    for (IDirectory sourceEntry : _module.getSourcePath()) {
      if (sourceEntry.exists()) {
        String prefix = sourceEntry.getName().equals(IModule.CONFIG_RESOURCE_PREFIX) ? IModule.CONFIG_RESOURCE_PREFIX : "";
        addAllLocalResourceFilesByExtensionInternal(prefix, sourceEntry, extension, results);
      }
    }
    return results;
  }

  private void addAllLocalResourceFilesByExtensionInternal(String relativePath, IDirectory dir, String extension, List<Pair<String, IFile>> results) {
    List<IDirectory> excludedPath = Arrays.asList( _module.getFileRepository().getExcludedPath());
    if ( excludedPath.contains( dir )) {
      return;
    }
    if (!CommonServices.getPlatformHelper().isPathIgnored( relativePath)) {
      for (IFile file : dir.listFiles()) {
        if (file.getName().endsWith(extension)) {
          String path = appendResourceNameToPath(relativePath, file.getName());
          results.add(new Pair<String, IFile>(path, file));
        }
      }
      for (IDirectory subdir : dir.listDirs()) {
        String path = appendResourceNameToPath(relativePath, subdir.getName());
        addAllLocalResourceFilesByExtensionInternal(path, subdir, extension, results);
      }
    }
  }

  private static String appendResourceNameToPath( String relativePath, String resourceName )
  {
    String path;
    if( relativePath.length() > 0 )
    {
      path = relativePath + '/' + resourceName;
    }
    else
    {
      path = resourceName;
    }
    return path;
  }


  public JavascriptPlugin( IModule currentModule )
  {
    super(currentModule);
  }

  @Override
  public IType getType( String name )
  {
    IFile iFile = _jsSources.get().get( name );

    if( iFile != null )
    {
      try
      {
        Parser parser = new Parser( new Tokenizer( StreamUtil.getContent( new InputStreamReader( iFile.openInputStream() ) ) ) );
        ProgramNode programNode = parser.parse();
        if( parser.isES6Class() )
        {
          return new JavascriptClassType( this, name, iFile, programNode );
        } else  {
          return new JavascriptProgramType(this, name, iFile, programNode);
        }
      } catch (IOException e) {

      }
    }

    return null;
  }


  @Override
  public Set<? extends CharSequence> getAllNamespaces()
  {
    if( _namespaces == null ) {
      try {
        _namespaces = TypeSystem.getNamespacesFromTypeNames( getAllTypeNames(), new HashSet<String>() );
      }
      catch( NullPointerException e ) {
        //!! hack to get past dependency issue with tests
        return Collections.emptySet();
      }
    }
    return _namespaces;
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
  public void refreshedNamespace( String namespace, IDirectory iDirectory, RefreshKind kind )
  {
    clear();
    if( _namespaces != null ) {
      if( kind == RefreshKind.CREATION ) {
        _namespaces.add( namespace );
      }
      else if( kind == RefreshKind.DELETION ) {
        _namespaces.remove( namespace );
      }
    }
  }

  @Override
  protected void refreshedImpl() {
    clear();
  }

  @Override
  public RefreshKind refreshedFile( IFile file, String[] types, RefreshKind kind ) {
    clear();
    return kind;
  }

  @Override
  protected void refreshedTypesImpl( RefreshRequest request ) {
    clear();
  }

  private void clear() {
    _jsSources.clear();
  }

  @Override
  public boolean hasNamespace( String namespace )
  {
    return getAllNamespaces().contains( namespace );
  }

  @Override
  public Set<String> computeTypeNames()
  {
    return _jsSources.get().keySet();
  }
}
