package cc.fooledit.editor.text.mode.makefile;
import cc.fooledit.core.*;
import cc.fooledit.editor.text.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="text/x-makefile";
	@Override
	public void start(BundleContext bc) throws Exception{
		CoreModule.GLOB_REGISTRY.put("(.*[/\\\\])?(GNUmakefile|makefile|Makefile)",CONTENT_TYPE);
		StructuredTextEditor.INSTANCE.registerHighlighter(cc.fooledit.editor.text.mode.makefile.MakefileLexer.class,Activator.class.getResourceAsStream("tokens.json"),CONTENT_TYPE);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
