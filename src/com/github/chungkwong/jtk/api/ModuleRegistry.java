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
import com.github.chungkwong.jtk.*;
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
	private static final HashMap<String,Module> modules=new HashMap<>();
	private static final Preferences paths=Preferences.userNodeForPackage(ModuleRegistry.class).node("module/classpath");
	private static final Preferences dirs=Preferences.userNodeForPackage(ModuleRegistry.class).node("module/directory");
	public static void ensureLoaded(String cls)throws MalformedURLException,ReflectiveOperationException{
		if(!modules.containsKey(cls)){
			Module module=getInstance(cls);
			module.onLoad();
			modules.put(cls,module);
		}
	}
	public static void unLoad(String cls){
		Module module=modules.remove(cls);
		module.onUnLoad();
	}
	public static Map<String,Module> getModules(){
		return Collections.unmodifiableMap(modules);
	}
	public static List<ModuleDescriptor> listInstalled() throws BackingStoreException, ReflectiveOperationException, MalformedURLException{
		ArrayList<ModuleDescriptor> installed=new ArrayList<>();
		for(String m:paths.keys()){
			installed.add(getInstance(m).getModuleDescriptor());
		}
		return installed;
	}
	public static List<ModuleDescriptor> listDownloadable(){
		String url=((JSONString)Main.loadJSON("module.json").getMembers().get(new JSONString("repository"))).getValue();
		try(BufferedReader in=new BufferedReader(new InputStreamReader(new URL(url).openStream(),StandardCharsets.UTF_8))){
			return ((JSONArray)JSONParser.parse(in)).getElements().stream().
					map((e)->ModuleDescriptor.fromJSON((JSONObject)e)).collect(Collectors.toList());
		}catch(IOException|SyntaxException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			return Collections.emptyList();
		}
	}
	public static URL resolveURL(String url) throws MalformedURLException{
		return new URL(getDataDirectory().toURI().toURL(),url);
	}
	public static File getDataDirectory(){
		return new File(Main.getPath(),"modules");
	}
	public File download(ModuleDescriptor module) throws IOException{
		try(ZipInputStream in=new ZipInputStream(new BufferedInputStream(new URL(module.getURL()).openStream()),StandardCharsets.UTF_8)){
			File base=File.createTempFile("module",module.getName(),getDataDirectory());
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
		JSONObject object=(JSONObject)JSONParser.parse(manifest);
		String cls=((JSONString)object.getMembers().get(new JSONString("class"))).getValue();
		String path=new URL(dir.toURI().toURL(),((JSONString)object.getMembers().get(new JSONString("classpath"))).getValue()).toString();
		paths.put(cls,path);
		paths.flush();
		dirs.put(cls,dir.getAbsolutePath());
		dirs.flush();
	}
	private static Module getInstance(String cls) throws ReflectiveOperationException, MalformedURLException{
		String path=paths.get(cls,null);
		ClassLoader loader=path!=null?new URLClassLoader(new URL[]{resolveURL(path)}):ModuleRegistry.class.getClassLoader();
		return (Module)loader.loadClass(cls).newInstance();
	}
}
