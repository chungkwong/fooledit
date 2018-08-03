package cc.fooledit.editor.text.mode.java;
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
	public static final String CONTENT_TYPE="text/x-java";
	@Override
	public void start(BundleContext bc) throws Exception{
		MultiRegistryNode.addChildElement("java",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		Registry.provides(CONTENT_TYPE,NAME,"highlighter","cc.fooledit.editor.text");
		Registry.provides(CONTENT_TYPE,NAME,"parser","cc.fooledit.editor.text");
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.java.Java9Lexer.class,Activator.class.getResourceAsStream("tokens.json"),CONTENT_TYPE);
		StructuredTextEditor.INSTANCE.registerParser(cc.fooledit.editor.text.mode.java.Java9Parser.class,"compilationUnit",CONTENT_TYPE);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
