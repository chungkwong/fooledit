package cc.fooledit.editor.text.mode.plain;
import cc.fooledit.core.*;
import cc.fooledit.editor.text.*;
import cc.fooledit.editor.text.mode.plain.Activator;
import cc.fooledit.spi.*;
import java.io.*;
import java.nio.charset.*;
import java.util.logging.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="text/plain";
	@Override
	public void start(BundleContext bc) throws Exception{
		CoreModule.INSTALLED_MODULE_REGISTRY.put(NAME,this.getClass());
		MultiRegistryNode.addChildElement("txt",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.plain.TextLexer.class,Activator.class.getResourceAsStream("tokens.json"),CONTENT_TYPE);
		StructuredTextEditor.INSTANCE.registerParser(cc.fooledit.editor.text.mode.plain.TextParser.class,"text",CONTENT_TYPE);
		try{
			((ListRegistryNode)CoreModule.TEMPLATE_REGISTRY.getOrCreateChild("children")).put(
					StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(new InputStreamReader(
							getClass().getResourceAsStream("templates.json"),StandardCharsets.UTF_8))));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.INFO,null,ex);
		}
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
