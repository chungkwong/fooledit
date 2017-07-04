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
package com.github.chungkwong.fooledit.api;
import com.github.chungkwong.fooledit.*;
import com.github.chungkwong.fooledit.setting.*;
import com.github.chungkwong.json.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;
import java.util.stream.*;
import java.util.zip.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ModuleRegistry{
	private static final HashMap<String,Module> loadedModules=new HashMap<>();
	private static final Map<String,Object> MODULES=(Map<String,Object>)PersistenceStatusManager.CORE.getOrDefault("modules",null);
	private static final Map<String,Map<Object,Object>> INSTALLED=(Map<String,Map<Object,Object>>)MODULES.get("installed");
	public static void ensureLoaded(String cls){
		if(!loadedModules.containsKey(cls)){
			try{
				loadedModules.put(cls,null);
				getModuleDescriptor(cls).getDependency().forEach((s)->ensureLoaded(s));
				Module module=getInstance(cls);
				module.onLoad();
				loadedModules.put(cls,module);
			}catch(ReflectiveOperationException|MalformedURLException ex){
				Logger.getLogger(ModuleRegistry.class.getName()).log(Level.SEVERE,null,ex);
			}
		}
	}
	public static void unLoad(String cls){
		Module module=loadedModules.remove(cls);
		module.onUnLoad();
	}
	public static Map<String,Module> getModules(){
		return Collections.unmodifiableMap(loadedModules);
	}
	public static ModuleDescriptor getModuleDescriptor(String name){
		return ModuleDescriptor.fromJSON(INSTALLED.get(name));
	}
	public static Map<String,Map<Object,Object>> getInstalledModules(){
		return INSTALLED;
	}
	public static List<ModuleDescriptor> listDownloadable(){
		String url=(String)Main.loadJSON("module.json").get("repository");
		try(BufferedReader in=new BufferedReader(new InputStreamReader(new URL(url).openStream(),StandardCharsets.UTF_8))){
			return ((List<Map<Object,Object>>)JSONDecoder.decode(in)).stream().
					map((e)->ModuleDescriptor.fromJSON(e)).collect(Collectors.toList());
		}catch(IOException|SyntaxException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			return Collections.emptyList();
		}
	}
	public static URL resolveURL(String url,String module) throws MalformedURLException{
		return new URL(Main.getModulePath(module).toURI().toURL(),url);
	}
	public File download(ModuleDescriptor module) throws IOException{
		try(ZipInputStream in=new ZipInputStream(new BufferedInputStream(new URL(module.getURL()).openStream()),StandardCharsets.UTF_8)){
			File base=File.createTempFile("module",module.getName(),Main.getDataPath());
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
	public void install(File dir) throws BackingStoreException, IOException, SyntaxException{
		String manifest=new String(Files.readAllBytes(new File(dir,"manifest.json").toPath()),StandardCharsets.UTF_8);
		Map<Object,Object> object=(Map<Object,Object>)JSONDecoder.decode(manifest);
		String cls=(String)object.get("class");
		String path=new URL(dir.toURI().toURL(),(String)object.get("classpath")).toString();
	}
	private static Module getInstance(String cls) throws ReflectiveOperationException, MalformedURLException{
		//String path=paths.get(cls,null);
		ClassLoader loader=cls!=null?new URLClassLoader(new URL[]{resolveURL(cls,cls)}):ModuleRegistry.class.getClassLoader();
		return (Module)loader.loadClass(cls).newInstance();
	}
}
