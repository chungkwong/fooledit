package cc.fooledit.editor.text.mode.haskell;
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
	public static final String CONTENT_TYPE="text/x-haskell";
	@Override
	public void start(BundleContext bc) throws Exception{
		MultiRegistryNode.addChildElement("hs",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		Registry.provides(CONTENT_TYPE,NAME,"highlighter","cc.fooledit.editor.text");
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.haskell.HaskellLexer.class,Activator.class.getResourceAsStream("tokens.json"),CONTENT_TYPE);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
