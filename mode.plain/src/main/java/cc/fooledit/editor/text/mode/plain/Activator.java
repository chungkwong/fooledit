package cc.fooledit.editor.text.mode.plain;
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
	public static final String CONTENT_TYPE="text/plain";
	@Override
	public void start(BundleContext bc) throws Exception{
		MultiRegistryNode.addChildElement("txt",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
