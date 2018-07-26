package cc.fooledit.editor.text.mode.sql;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="application/sql";
	@Override
	public void start(BundleContext bc) throws Exception{
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
