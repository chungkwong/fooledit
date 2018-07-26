package cc.fooledit.editor.text.mode.troff;
import cc.fooledit.core.*;
import cc.fooledit.editor.text.*;
import cc.fooledit.spi.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="text/troff";
	@Override
	public void start(BundleContext bc) throws Exception{
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("text/x-troff",CONTENT_TYPE);
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-troff",CONTENT_TYPE);
		MultiRegistryNode.addChildElement("t",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("tr",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("roff",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		Registry.provides(CONTENT_TYPE,NAME,"highlighter","cc.fooledit.editor.text");
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.troff.TroffLexer.class,Activator.class.getResourceAsStream("token.json"),CONTENT_TYPE);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
