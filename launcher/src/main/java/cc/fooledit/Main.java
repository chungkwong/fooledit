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
import cc.fooledit.Main;
import java.io.*;
import java.net.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Logger;
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
				+"org.w3c.dom,org.xml.sax,com.sun.javafx.scene.control.skin,javax.activation,javax.script,com.sun.javafx.css.converters,"
				+"com.sun.javafx.binding,com.sun.javafx.collections,com.sun.org.apache.xml.internal.utils,com.sun.org.apache.xpath.internal,"
				+"com.sun.org.apache.xpath.internal.objects,sun.net.www");
		config.put("felix.auto.deploy.action","uninstall,install,update,start");
		URL url=Main.class.getResource("");
		if(url.getProtocol().equals("file")){
			System.out.println(url);
			File base;
			try{
				base=new File(new File(url.toURI()),"../../../../../distribution/target/distribution-1.0-SNAPSHOT-dist");
				config.put("org.osgi.framework.storage",new File(base,"felix-cache").getCanonicalPath());
				config.put("felix.auto.deploy.dir",new File(base,"bundle").getCanonicalPath());
			}catch(URISyntaxException|IOException ex){
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE,null,ex);
			}
		}else{
			try{
				File home=new File(URLDecoder.decode(url.toString().substring(9,url.toString().indexOf('!')),"UTF-8")).getParentFile().getParentFile();
				config.put("org.osgi.framework.storage",new File(home,"felix-cache").getAbsolutePath());
				config.put("felix.auto.deploy.dir",new File(home,"bundle").getAbsolutePath());
			}catch(UnsupportedEncodingException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		}
		//config.put("felix.auto.deploy.dir","bundle");
		config.put("org.osgi.framework.storage.clean","onFirstInit");
		config.put("felix.log.level","3");
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
		try{
			Path path=new File(Objects.toString(config.get("felix.auto.deploy.dir"))).toPath();
			FileSystem fileSystem=path.getFileSystem();
			WatchService watchService=fileSystem.newWatchService();
			path.register(watchService,StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE,
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.OVERFLOW);
			Thread monitor=new Thread(()->{
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
			monitor.setDaemon(true);
			monitor.start();
		}catch(Exception ex){
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE,null,ex);
			ex.printStackTrace();
		}
	}
}
