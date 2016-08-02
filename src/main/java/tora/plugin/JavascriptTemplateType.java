package tora.plugin;

import gw.fs.IFile;
import gw.lang.reflect.ITypeInfo;
import tora.parser.tree.ProgramNode;
import tora.parser.tree.template.TemplateNode;

public class JavascriptTemplateType extends JavascriptTypeBase
{
  private final JavascriptTemplateTypeInfo _typeinfo;

  public JavascriptTemplateType(JavascriptPlugin typeloader, String name, IFile jsFile, TemplateNode templateNode)
  {
    super( typeloader, name, jsFile );
    _typeinfo = new JavascriptTemplateTypeInfo(this, templateNode);
  }

  @Override
  public ITypeInfo getTypeInfo()
  {
    return _typeinfo;
  }
}
