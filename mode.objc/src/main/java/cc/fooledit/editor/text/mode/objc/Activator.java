package cc.fooledit.editor.text.mode.objc;
import cc.fooledit.core.*;
import cc.fooledit.editor.text.*;
import cc.fooledit.editor.text.mode.objc.Activator;
import cc.fooledit.spi.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="text/x-objcsrc";
	@Override
	public void start(BundleContext bc) throws Exception{
		MultiRegistryNode.addChildElement("m",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.objc.ObjectiveCLexer.class,Activator.class.getResourceAsStream("tokens.json"),CONTENT_TYPE);
		StructuredTextEditor.INSTANCE.registerParser(cc.fooledit.editor.text.mode.objc.ObjectiveCParser.class,"translationUnit",CONTENT_TYPE);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
