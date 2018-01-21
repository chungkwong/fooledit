/*
 * Copyright (C) 2017 Chan Chung Kwong <1m02math@126.com>
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
package cc.fooledit.core;
import cc.fooledit.*;
import static cc.fooledit.core.CoreModule.MODULE_REGISTRY;
import cc.fooledit.spi.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import java.util.zip.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ModuleRegistry{
	public static final String REPOSITORY="repository";
	public static final String PRELOAD="preload";
	public static void loadDefault(){
		ensureLoaded(CoreModule.NAME);
		//String preload="core:editor.text:editor.media:editor.image:editor.binary:editor.filesystem:editor.browser:editor.terminal:editor.zip";//FIXME
		//Arrays.stream(preload.split(":")).forEach((name)->ensureLoaded(name));
	}
	public static void ensureLoaded(String module){
		if(!MODULE_REGISTRY.hasChild(module)){
			Logger.getGlobal().log(Level.INFO,"Trying to load {0}",new Object[]{module});
			try{
				ensureLoaded(getModuleDescriptor(module));
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		}
	}
	private static void ensureLoaded(RegistryNode<String,Object,String> moduleDescriptor) throws Exception{
		String module=(String)moduleDescriptor.getChild(NAME);
		MODULE_REGISTRY.addChild(module,moduleDescriptor);
		ensureInstalled(module);
		((ListRegistryNode<String,String>)moduleDescriptor.getChild(DEPENDENCY)).toMap().values().forEach((s)->ensureLoaded(s));
		onLoad(module);
	}
	public static void ensureInstalled(String module){
		try{
			if(!CoreModule.INSTALLED_MODULE_REGISTRY.hasChild(module))
				ensureInstalled(getModuleDescriptor(module));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private static void ensureInstalled(RegistryNode<String,Object,String> moduleDescriptor) throws Exception{
		String module=(String)moduleDescriptor.getChild(NAME);
		((ListRegistryNode<String,String>)moduleDescriptor.getChild(DEPENDENCY)).toMap().values().forEach((s)->ensureInstalled(s));
		if(!CoreModule.INSTALLED_MODULE_REGISTRY.hasChild(module)){
			Logger.getGlobal().log(Level.INFO,"Trying to install {0}",new Object[]{module});
			onInstall(module);
			CoreModule.INSTALLED_MODULE_REGISTRY.addChild(module,null);
		}
	}
	public static RegistryNode<String,Object,String> getModuleDescriptor(String name) throws Exception{
		RegistryNode<String,Object,String> moduleDescriptor=(RegistryNode<String,Object,String>)StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(new File(Main.INSTANCE.getModulePath(name),"descriptor.json")));
		if(!moduleDescriptor.getChild(NAME).equals(name))
			throw new RuntimeException("Bad module format: "+name);
		return moduleDescriptor;
	}
	public static Collection<String> getInstalledModules(){
		return Arrays.stream(Main.INSTANCE.getDataPath().listFiles((File file)->file.isDirectory())).
				map((f)->f.getName()).collect(Collectors.toSet());
	}
	public static ListRegistryNode<RegistryNode<String,Object,String>,String> listDownloadable(){
		String url=(String)CoreModule.REGISTRY.getChild(REPOSITORY);
		try(BufferedReader in=new BufferedReader(new InputStreamReader(new URL(url).openStream(),StandardCharsets.UTF_8))){
			return (ListRegistryNode<RegistryNode<String,Object,String>,String>)StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(in));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			return new ListRegistryNode<>();
		}
	}
	public File download(RegistryNode<String,Object,String> module) throws IOException{
		try(ZipInputStream in=new ZipInputStream(new BufferedInputStream(new URL((String)module.getChild(URL)).openStream()),StandardCharsets.UTF_8)){
			File base=Main.INSTANCE.getModulePath((String)module.getChild(NAME));
			base.mkdirs();
			ZipEntry entry;
			byte[] buf=new byte[4096];
			while((entry=in.getNextEntry())!=null){
				File file=new File(base,entry.getName());
				if(entry.isDirectory()){
					file.mkdirs();
				}else{
					FileOutputStream out=new FileOutputStream(file);
					int c;
					while((c=in.read(buf))!=-1)
						out.write(buf,0,c);
					out.close();
				}
			}
			return base;
		}
	}
	private void onLoadModule(){

	}
	public static void onLoad(String module)throws Exception{
		evalScript("on-load.scm",module);
	}
	public static void onUnLoad(String module)throws Exception{
		evalScript("on-unload.scm",module);
	}
	public static void onInstall(String module)throws Exception{
		evalScript("on-install.scm",module);
	}
	public static void onUninstall(String module)throws Exception{
		evalScript("on-uninstall.scm",module);
	}
	private static void evalScript(String filename,String module)throws Exception{
		File file=Main.INSTANCE.getFile(filename,module);
		if(file.exists()){
			Main.INSTANCE.getScriptAPI().eval(Helper.readText(file));
		}
	}
	public static final String MODULE="module";
	public static final String NAME="name";
	public static final String DESCRIPTION="description";
	public static final String LICENSE="license";
	public static final String AUTHOR="author";
	public static final String URL="url";
	public static final String MAJOR_VERSION="version_major";
	public static final String MINOR_VERSION="version_minor";
	public static final String REVISE_VERSION="version_revise";
	public static final String DEPENDENCY="dependency";
}
