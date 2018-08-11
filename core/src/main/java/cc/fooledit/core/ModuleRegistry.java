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
import cc.fooledit.spi.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.logging.*;
import java.util.zip.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ModuleRegistry{
	public static final String REPOSITORY="repository";
	public static final String PRELOAD="preload";
	public static void ensureInstalled(String module){
		if(!CoreModule.INSTALLED_MODULE_REGISTRY.containsKey(module)){
			try{
				install(listDownloadable().values().stream().filter((d)->module.equals(d.get(NAME))).findAny().get());
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		}
	}
	private static void install(RegistryNode<String,Object> moduleDescriptor) throws Exception{
		String module=(String)moduleDescriptor.get(NAME);
		Logger.getGlobal().log(Level.INFO,"Trying to install {0}",new Object[]{module});
		if(CoreModule.INSTALLING_MODULE_REGISTRY.containsKey(module)){
			return;
		}
		CoreModule.INSTALLING_MODULE_REGISTRY.put(module,null);
		((ListRegistryNode<String>)moduleDescriptor.get(DEPENDENCY)).values().forEach((s)->ensureInstalled(s));
		//onInstall(module);
		CoreModule.INSTALLING_MODULE_REGISTRY.remove(module);
		CoreModule.INSTALLED_MODULE_REGISTRY.put(module,null);
	}
	public static RegistryNode<String,Object> getModuleDescriptor(Class model) throws Exception{
		RegistryNode<String,Object> moduleDescriptor=(RegistryNode<String,Object>)StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(model.getResourceAsStream("/descriptor.json")));
		return moduleDescriptor;
	}
	public static ListRegistryNode<RegistryNode<String,Object>> listDownloadable(){
		String url=(String)CoreModule.REGISTRY.get(REPOSITORY);
		try(BufferedReader in=new BufferedReader(new InputStreamReader(new URL(url).openStream(),StandardCharsets.UTF_8))){
			return (ListRegistryNode<RegistryNode<String,Object>>)StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(in));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			return new ListRegistryNode<>();
		}
	}
	public File download(RegistryNode<String,Object> module) throws IOException{
		try(ZipInputStream in=new ZipInputStream(new BufferedInputStream(new URL((String)module.get(URL)).openStream()),StandardCharsets.UTF_8)){
			File base;
			URL url=Main.class.getResource("");
			if(url.getProtocol().equals("file")){
				base=new File("/home/kwong/NetBeansProjects/fooledit/distribution/target/distribution-1.0-SNAPSHOT-dist/bundle");
			}else{
				base=new File(URLDecoder.decode(url.toString().substring(9,url.toString().indexOf('!')),"UTF-8")).getParentFile();
			}
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
					while((c=in.read(buf))!=-1){
						out.write(buf,0,c);
					}
					out.close();
				}
			}
			return base;
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
