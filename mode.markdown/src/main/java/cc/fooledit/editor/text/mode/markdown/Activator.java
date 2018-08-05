package cc.fooledit.editor.text.mode.markdown;
import cc.fooledit.core.*;
import cc.fooledit.editor.text.*;
import cc.fooledit.editor.text.mode.markdown.Activator;
import cc.fooledit.spi.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="text/markdown";
	@Override
	public void start(BundleContext bc) throws Exception{
		MultiRegistryNode.addChildElement("md",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("mkd",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.markdown.MarkdownLexer.class,Activator.class.getResourceAsStream("tokens.json"),CONTENT_TYPE);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
