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
	public static final String REPOSITORY="repository";
	public static final String PRELOAD="preload";
	public static void loadDefault(){
		String preload="core:editor.text:editor.media:editor.image:editor.binary:editor.filesystem:editor.browser:editor.terminal:editor.zip";//FIXME
		Arrays.stream(preload.split(":")).forEach((name)->ensureLoaded(name));
	}
	public static void ensureLoaded(String module){
		if(!MODULE_REGISTRY.hasChild(module)){
			MODULE_REGISTRY.addChild(module,(Module)null);
			ModuleDescriptor moduleDescriptor=getModuleDescriptor(module);
			moduleDescriptor.getDependency().forEach((s)->ensureLoaded(s));
			Module mod=new ScriptModule(module);
			try{
				mod.onLoad();
				MODULE_REGISTRY.addChild(module,mod);
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		}
	}
	public static void unLoad(String cls) throws Exception{
		Module module=MODULE_REGISTRY.removeChild(cls);
		module.onUnLoad();
	}
	public static ModuleDescriptor getModuleDescriptor(String name){
		return ModuleDescriptor.fromJSON(Main.INSTANCE.loadJSON(new File(Main.INSTANCE.getModulePath(name),"descriptor.json")));
	}
	public static Collection<String> getInstalledModules(){
		return Arrays.stream(Main.INSTANCE.getDataPath().listFiles((File file)->file.isDirectory())).
				map((f)->f.getName()).collect(Collectors.toSet());
	}
	public static List<ModuleDescriptor> listDownloadable(){
		String url=(String)CoreModule.REGISTRY.getChild(REPOSITORY);
		try(BufferedReader in=new BufferedReader(new InputStreamReader(new URL(url).openStream(),StandardCharsets.UTF_8))){
			return ((List<Map<Object,Object>>)JSONDecoder.decode(in)).stream().
					map((e)->ModuleDescriptor.fromJSON(e)).collect(Collectors.toList());
		}catch(IOException|SyntaxException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
			return Collections.emptyList();
		}
	}
	public File download(ModuleDescriptor module) throws IOException{
		try(ZipInputStream in=new ZipInputStream(new BufferedInputStream(new URL(module.getURL()).openStream()),StandardCharsets.UTF_8)){
			File base=Main.INSTANCE.getModulePath(module.getName());
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
}
