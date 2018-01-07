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
import cc.fooledit.spi.*;
import com.github.chungkwong.json.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Registry extends SimpleRegistryNode<String,RegistryNode<?,?,String>,String>{
	public static final Registry ROOT=new Registry();
	private Registry(){
		try{
			RegistryNode<String,RegistryNode<Object,Object,Object>,Object> toLoad=(RegistryNode<String,RegistryNode<Object,Object,Object>,Object>)StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(getPersistentFile()));
			for(String path:toLoad.getChildNames()){
				RegistryNode<Object,Object,String> registry=resolve(path);
				for(Object key:toLoad.getChild(path).getChildNames())
					registry.addChild(key,toLoad.getChild(path).getChild(key));
			}
			EventManager.addEventListener(EventManager.SHUTDOWN,(obj)->syncPersistent());
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	public <K,V> RegistryNode<K,V,String> resolve(String path){
		RegistryNode node=Registry.ROOT;
		for(String name:path.split("/"))
			node=(RegistryNode)node.getOrCreateChild(name);
		return node;
	}
	public void syncPersistent(){
		try{
			File tmp=new File(Main.INSTANCE.getUserPath(),".registry.json");
			OutputStreamWriter out=new OutputStreamWriter(new FileOutputStream(tmp),StandardCharsets.UTF_8);
			out.append('{');
			Iterator<String> toSave=CoreModule.PERSISTENT_REGISTRY.getChildNames().iterator();
			while(toSave.hasNext()){
				String path=toSave.next();
				out.write(JSONEncoder.encode(path));
				out.write(':');
				out.write(StandardSerializiers.JSON_SERIALIZIER.encode(resolve(path)));
				if(toSave.hasNext())
					out.write(',');
			}
			out.append('}');
			Files.move(tmp.toPath(),getPersistentFile().toPath(),StandardCopyOption.REPLACE_EXISTING);
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private File getPersistentFile(){
		return new File(Main.INSTANCE.getUserPath(),"registry.json");
	}
	public NavigableRegistryNode<String,String,String> registerKeymap(String module){
		TreeMap<String,String> mapping=new TreeMap<>();
		File src=Main.INSTANCE.getFile("keymaps/default.json",module);
		if(src!=null)
			mapping.putAll((Map<String,String>)(Object)Main.INSTANCE.loadJSON(src));
		NavigableRegistryNode<String,String,String> registry=new NavigableRegistryNode<>(mapping);
		((RegistryNode<String,RegistryNode<String,String,String>,String>)ROOT.getOrCreateChild(module)).addChild(CoreModule.KEYMAP_REGISTRY_NAME,registry);
		return registry;
	}
	public RegistryNode<String,String,String> registerMessage(String module){
		RegistryNode<String,String,String> registry=new SimpleRegistryNode<>();
		ResourceBundle bundle;
		try{
			bundle=ResourceBundle.getBundle("messages",Locale.getDefault(),
					new URLClassLoader(new URL[]{new File(new File(Main.INSTANCE.getDataPath(),module),"locales").toURI().toURL()}));
			bundle.keySet().forEach((key)->registry.addChild(key,bundle.getString(key)));
		}catch(MalformedURLException|MissingResourceException ex){
			Logger.getGlobal().log(Level.INFO,null,ex);
		}
		((RegistryNode<String,RegistryNode<String,String,String>,String>)ROOT.getOrCreateChild(module)).addChild(CoreModule.MESSAGE_REGISTRY_NAME,registry);
		return registry;
	}
	public RegistryNode<String,Command,String> registerCommand(String module){
		RegistryNode<String,Command,String> registry=new SimpleRegistryNode<>();
		((RegistryNode<String,RegistryNode<String,Command,String>,String>)ROOT.getOrCreateChild(module)).addChild(CoreModule.COMMAND_REGISTRY_NAME,registry);
		return registry;
	}
	public static void registerApplication(String path,String baseType,DataObjectType factory,Class<? extends DataObject> cls,Supplier<DataEditor> editorSupplier){
		CoreModule.APPLICATION_REGISTRY.addChild(path,baseType);
		DataObjectTypeRegistry.addDataObjectType(factory);
		DataObjectTypeRegistry.addDataEditor(editorSupplier,cls);
		DataObjectTypeRegistry.registerMime(baseType,factory.getClass().getName());
	}

}
