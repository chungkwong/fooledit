package cc.fooledit.editor.text.mode.javascript;
import cc.fooledit.core.*;
import cc.fooledit.editor.text.*;
import cc.fooledit.editor.text.mode.javascript.Activator;
import cc.fooledit.spi.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="application/javascript";
	@Override
	public void start(BundleContext bc) throws Exception{
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put(CONTENT_TYPE,TextObjectType.class.getName());
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-javascript",CONTENT_TYPE);
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("text/javascript",CONTENT_TYPE);
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("text/ecmascript",CONTENT_TYPE);
		MultiRegistryNode.addChildElement("js",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("jsm",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.javascript.ECMAScriptLexer.class,Activator.class.getResourceAsStream("tokens.json"),CONTENT_TYPE);
		StructuredTextEditor.INSTANCE.registerParser(cc.fooledit.editor.text.mode.javascript.ECMAScriptParser.class,"program",CONTENT_TYPE);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
