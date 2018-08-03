package cc.fooledit.editor.text.mode.php;
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
	public static final String CONTENT_TYPE="application/x-php";
	@Override
	public void start(BundleContext bc) throws Exception{
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put(CONTENT_TYPE,TextObjectType.class.getName());
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-php-source",CONTENT_TYPE);
		MultiRegistryNode.addChildElement("php",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("php3",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("php4",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("php5",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("phps",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		Registry.provides(CONTENT_TYPE,NAME,"highlighter","cc.fooledit.editor.text");
		Registry.provides(CONTENT_TYPE,NAME,"parser","cc.fooledit.editor.text");
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.php.PHPLexer.class,Activator.class.getResourceAsStream("tokens.json"),CONTENT_TYPE);
		StructuredTextEditor.INSTANCE.registerParser(cc.fooledit.editor.text.mode.php.PHPParser.class,"htmlDocument",CONTENT_TYPE);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
