package cc.fooledit.editor.text.mode.xml;
import cc.fooledit.core.*;
import cc.fooledit.editor.text.*;
import cc.fooledit.editor.text.mode.xml.Activator;
import cc.fooledit.spi.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="application/xml";
	public static final String CONTENT_TYPE_DTD="application/xml-dtd";
	@Override
	public void start(BundleContext bc) throws Exception{
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put(CONTENT_TYPE,TextObjectType.class.getName());
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put(CONTENT_TYPE_DTD,TextObjectType.class.getName());
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("text/xml",CONTENT_TYPE);
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("text/xml-external-parsed-entity","application/xml-external-parsed-entity");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("text/x-dtd",CONTENT_TYPE_DTD);
		MultiRegistryNode.addChildElement("xml",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("xbl",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("xsd",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("rng",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("dtd",CONTENT_TYPE_DTD,CoreModule.SUFFIX_REGISTRY);
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.xml.XMLLexer.class,Activator.class.getResourceAsStream("tokens.json"),CONTENT_TYPE);
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.xml.XMLLexer.class,Activator.class.getResourceAsStream("tokens.json"),CONTENT_TYPE_DTD);
		StructuredTextEditor.INSTANCE.registerParser(cc.fooledit.editor.text.mode.xml.XMLParser.class,"document",CONTENT_TYPE);
		StructuredTextEditor.INSTANCE.registerParser(cc.fooledit.editor.text.mode.xml.XMLParser.class,"extSubset",CONTENT_TYPE_DTD);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
