package cc.fooledit.editor.text.mode.c;
import cc.fooledit.core.*;
import cc.fooledit.editor.text.*;
import cc.fooledit.editor.text.mode.c.Activator;
import cc.fooledit.spi.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="text/x-csrc";
	@Override
	public void start(BundleContext bc) throws Exception{
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put(CONTENT_TYPE,TextObjectType.class.getName());
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("text/x-c",CONTENT_TYPE);
		MultiRegistryNode.addChildElement("c",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		Registry.provides(CONTENT_TYPE,NAME,"highlighter","cc.fooledit.editor.text");
		Registry.provides(CONTENT_TYPE,NAME,"parser","cc.fooledit.editor.text");
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.c.CLexer.class,Activator.class.getResourceAsStream("tokens.json"),CONTENT_TYPE);
		StructuredTextEditor.INSTANCE.registerParser(cc.fooledit.editor.text.mode.c.CParser.class,"compilationUnit",CONTENT_TYPE);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
