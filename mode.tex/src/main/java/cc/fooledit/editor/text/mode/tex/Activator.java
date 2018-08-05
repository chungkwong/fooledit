package cc.fooledit.editor.text.mode.tex;
import cc.fooledit.core.*;
import cc.fooledit.editor.text.*;
import cc.fooledit.editor.text.mode.tex.Activator;
import cc.fooledit.spi.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="text/x-tex";
	@Override
	public void start(BundleContext bc) throws Exception{
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-tex",CONTENT_TYPE);
		MultiRegistryNode.addChildElement("tex",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("cls",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("dtx",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("ins",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("latex",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("ltx",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("sty",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.tex.TexLexer.class,Activator.class.getResourceAsStream("tokens.json"),CONTENT_TYPE);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
