package cc.fooledit.editor.text.mode.bash;
import cc.fooledit.core.*;
import cc.fooledit.editor.text.*;
import cc.fooledit.editor.text.mode.bash.Activator;
import cc.fooledit.spi.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="application/x-shellscript";
	@Override
	public void start(BundleContext bc) throws Exception{
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put(CONTENT_TYPE,TextObjectType.class.getName());
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("text/x-sh",CONTENT_TYPE);
		MultiRegistryNode.addChildElement("sh",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		Registry.provides(CONTENT_TYPE,NAME,"highlighter","cc.fooledit.editor.text");
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.bash.BashLexer.class,cc.fooledit.core.Activator.class.getResourceAsStream("token.json"),CONTENT_TYPE);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
