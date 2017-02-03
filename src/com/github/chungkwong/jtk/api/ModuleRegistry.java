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
package com.github.chungkwong.jtk.api;
import com.github.chungkwong.json.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ModuleRegistry{
	private static final HashMap<String,Module> modules=new HashMap<>();
	public static void register(String cls) throws ClassNotFoundException, MalformedURLException,ReflectiveOperationException{
		register((Module)getModuleLoader().loadClass(cls).newInstance());
	}
	public static void register(Module module){
		modules.put(module.getModuleDescriptor().getName(),module);
		module.onLoad();
	}
	public static void unRegister(Module module){
		module.onUnLoad();
		modules.remove(module.getModuleDescriptor().getName());
	}
	private static ClassLoader getModuleLoader(){
		String[] paths=Preferences.userNodeForPackage(ModuleRegistry.class).node("module").get("classpath","").split(" ");
		URL[] urls=Arrays.stream(paths).map((path)->{
			try{
				return new File(path).toURI().toURL();
			}catch(MalformedURLException ex){
				Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
				return null;
			}
		}).toArray(URL[]::new);
		return new URLClassLoader(urls,ModuleRegistry.class.getClassLoader());
	}
	public List<ModuleDescriptor> listDownloadable(){
		String url=Preferences.userNodeForPackage(ModuleRegistry.class).node("module").get("repository","");
		try(BufferedReader in=new BufferedReader(new InputStreamReader(new URL(url).openStream(),StandardCharsets.UTF_8))){
			return ((JSONArray)JSONParser.parse(in)).getElements().stream().
					map((e)->ModuleDescriptor.fromJSON((JSONObject)e)).collect(Collectors.toList());
		}catch(IOException|SyntaxException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			return Collections.emptyList();
		}
	}
	/*public void download(String url){
		try(ZipInputStream in=new ZipInputStream(new BufferedInputStream(new URL(url).openStream()),UTF8)){
			ZipEntry entry;
			byte[] buf=new byte[4096];
			while((entry=in.getNextEntry())!=null){
				File file=new File(PATH,entry.getName());
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
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
	}
	public static void main(String[] args)throws Exception{

	}*/
}
