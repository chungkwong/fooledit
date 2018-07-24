package cc.fooledit.editor.text.mode.bibtex;
import cc.fooledit.core.*;
import cc.fooledit.editor.text.*;
import cc.fooledit.editor.text.mode.bibtex.Activator;
import cc.fooledit.spi.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="text/x-bibtex";
	@Override
	public void start(BundleContext bc) throws Exception{
		MultiRegistryNode.addChildElement("bib",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		Registry.provides(CONTENT_TYPE,NAME,"highlighter","cc.fooledit.editor.text");
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.bibtex.BibtexLexer.class,cc.fooledit.core.Activator.class.getResourceAsStream("token.json"),CONTENT_TYPE);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
