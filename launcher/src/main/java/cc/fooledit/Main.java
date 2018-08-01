/*
 * Copyright (C) 2018 kwong
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cc.fooledit;
import java.util.*;
import org.apache.felix.framework.FrameworkFactory;
import org.apache.felix.main.*;
import org.osgi.framework.launch.*;
/**
 *
 * @author kwong
 */
public class Main{
	public static void main(String[] args) throws Exception{
		Map config=getDefaultConfig();
		Framework framework=new FrameworkFactory().newFramework(config);
		stopFrameOnExit(framework);
		System.out.println(framework.getLocation());
		framework.init();
		syncBundles(config,framework);
		framework.start();
		framework.waitForStop(0);
		System.exit(0);
	}
	private static Map<String,String> getDefaultConfig(){
		Map<String,String> config=new HashMap<>();
		config.put("org.osgi.framework.system.packages.extra","javafx.animation,javafx.application,"
				+"javafx.beans,javafx.beans.binding,javafx.beans.property,javafx.beans.property.adapter,javafx.beans.value,"
				+"javafx.collections,javafx.collections.transformation,javafx.concurrent,javafx.css,javafx.embed.swing,javafx.embed.swt,"
				+"javafx.event,javafx.fxml,javafx.geometry,javafx.print,javafx.scene,javafx.scene.canvas,javafx.scene.chart,javafx.scene.control,"
				+"javafx.scene.control.cell,javafx.scene.effect,javafx.scene.image,javafx.scene.input,javafx.scene.layout,javafx.scene.media,"
				+"javafx.scene.paint,javafx.scene.shape,javafx.scene.text,javafx.scene.transform,javafx.scene.web,javafx.stage,"
				+"javafx.util,javafx.util.converter,netscape.javascript,javax.crypto,javax.crypto.spec,javax.imageio,javax.i"
				+"mageio.metadata,javax.imageio.plugins.jpeg,javax.imageio.stream,javax.xml.namespace,javax.xml.parsers,javax.xml.xpath,"
				+"org.w3c.dom,org.xml.sax,com.sun.javafx.scene.control.skin,javax.activation,javax.script,"
				+"com.sun.javafx.binding,com.sun.org.apache.xml.internal.utils,com.sun.org.apache.xpath.internal,"
				+"com.sun.org.apache.xpath.internal.objects,sun.net.www");
		config.put("felix.auto.deploy.action","uninstall,install,update,start");
		config.put("org.osgi.framework.storage","/home/kwong/NetBeansProjects/fooledit/distribution/target/distribution-1.0-SNAPSHOT-dist/felix-cache");
		config.put("felix.auto.deploy.dir","/home/kwong/NetBeansProjects/fooledit/distribution/target/distribution-1.0-SNAPSHOT-dist/bundle");
		//config.put("felix.auto.deploy.dir","bundle");
		config.put("org.osgi.framework.storage.clean","onFirstInit");
		config.put("felix.log.level","4");
		return config;
	}
	private static void stopFrameOnExit(Framework framework){
		Runtime.getRuntime().addShutdownHook(new Thread(()->{
			try{
				framework.stop();
				framework.waitForStop(0);
			}catch(Exception ex){
				System.err.println("Error stopping framework: "+ex);
			}
		},"Felix Shutdown Hook"));
	}
	private static void syncBundles(Map config,Framework framework){
		AutoProcessor.process(config,framework.getBundleContext());
		/*try{
			Path path=new File("bundle").toPath();
			FileSystem fileSystem=path.getFileSystem();
			WatchService watchService=fileSystem.newWatchService();
			path.register(watchService,StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.OVERFLOW);
			new Thread(()->{
				while(true){
					try{
						WatchKey key=watchService.take();
						AutoProcessor.process(config,framework.getBundleContext());
						key.reset();
					}catch(InterruptedException ex){
						Logger.getLogger(Main.class.getName()).log(Level.SEVERE,null,ex);
					}
				}
			},"Check bundle directory");
		}catch(Exception ex){
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE,null,ex);
			ex.printStackTrace();
		}*/
	}
}
