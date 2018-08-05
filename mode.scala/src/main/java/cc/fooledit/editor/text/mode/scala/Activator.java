package cc.fooledit.editor.text.mode.scala;
import cc.fooledit.core.*;
import cc.fooledit.editor.text.*;
import cc.fooledit.editor.text.mode.scala.Activator;
import cc.fooledit.spi.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="text/x-scala";
	@Override
	public void start(BundleContext bc) throws Exception{
		MultiRegistryNode.addChildElement("scala",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.scala.ScalaLexer.class,Activator.class.getResourceAsStream("tokens.json"),CONTENT_TYPE);
		StructuredTextEditor.INSTANCE.registerParser(cc.fooledit.editor.text.mode.scala.ScalaParser.class,"compilationUnit",CONTENT_TYPE);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
