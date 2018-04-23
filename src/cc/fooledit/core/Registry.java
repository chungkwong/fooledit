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
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Registry extends SimpleRegistryNode<String,RegistryNode<String,?>>{
	public static final Registry ROOT=new Registry();
	private Registry(){

	}
	public <K,V> RegistryNode<K,V> resolve(String path){
		RegistryNode node=this;
		for(String name:path.split("/"))
			node=(RegistryNode)node.getOrCreateChild(name);
		return node;
	}
	public String[] splitPath(String path){
		int index=path.lastIndexOf('/');
		String parent=index==-1?"":path.substring(0,index);
		String leaf=index==-1?path:path.substring(index+1);
		return new String[]{parent,leaf};
	}
	public void loadPreference(){
		try{
			RegistryNode<String,RegistryNode<Object,Object>> toLoad=(RegistryNode<String,RegistryNode<Object,Object>>)StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(getPersistentFile()));
			for(String path:toLoad.keySet()){
				String[] comp=splitPath(path);
				resolve(comp[0]).put(comp[1],toLoad.get(path));
			}
			fixProvider();
		}catch(Exception ex){
			Logger.getGlobal().log(Level.INFO,"Failed to load registry cache",ex);
			for(String mod:Main.INSTANCE.getDataPath().list()){
				ModuleRegistry.ensureInstalled(mod);
			}
		}
		EventManager.addEventListener(EventManager.SHUTDOWN,(obj)->syncPersistent());
	}
	public void fixProvider(){
		RegistryNode providers=((RegistryNode)ROOT.get(CoreModule.NAME).getOrCreateChild(CoreModule.PROVIDER_REGISTRY_NAME));
		fixProvider(providers,ROOT);
	}
	private void fixProvider(RegistryNode provider,RegistryNode actual){
		provider.forEach((key,value)->{
			if(value instanceof RegistryNode){
				fixProvider((RegistryNode)value,(RegistryNode)actual.getOrCreateChild(key));
			}else if(value instanceof String){
				actual.putIfAbsent(key,LoaderValue.create((String)value));
			}
		});
	}
	public void syncPersistent(){
		File tmp=new File(Main.INSTANCE.getUserPath(),".registry.json");
		try(OutputStreamWriter out=new OutputStreamWriter(new FileOutputStream(tmp),StandardCharsets.UTF_8)){
			out.append('{');
			Iterator<String> toSave=CoreModule.PERSISTENT_REGISTRY.values().iterator();
			while(toSave.hasNext()){
				String path=toSave.next();
				out.write(JSONEncoder.encode(path));
				out.write(':');
				out.write(StandardSerializiers.JSON_SERIALIZIER.encode(resolve(path)));
				if(toSave.hasNext())
					out.write(',');
			}
			out.append('}');
			out.flush();
			Files.move(tmp.toPath(),getPersistentFile().toPath(),StandardCopyOption.REPLACE_EXISTING);
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private File getPersistentFile(){
		return new File(Main.INSTANCE.getUserPath(),"registry.json");
	}
	public MenuRegistry registerMenu(String module){
		try{
			File src=Main.INSTANCE.getFile("menus/default.json",module);
			RegistryNode<String,List<ListRegistryNode<Object>>> json=(RegistryNode<String,List<ListRegistryNode<Object>>>)StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(src));
			RegistryNode<String,ListRegistryNode<Object>> mod=((RegistryNode<String,ListRegistryNode<Object>>)Registry.ROOT.getOrCreateChild(module));
			mod.putIfAbsent(CoreModule.MENU_REGISTRY_NAME,new ListRegistryNode<>());
			mergeTo((ListRegistryNode<Object>)json.get("children"),mod.get(CoreModule.MENU_REGISTRY_NAME));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.INFO,null,ex);
		}
		return new MenuRegistry(module);
	}
	private <K,V> void mergeTo(RegistryNode<K,V> a,RegistryNode<K,V> b){
		for(K key:a.keySet()){
			if(b.containsKey(key)){
				V childa=a.get(key);
				V childb=b.get(key);
				if(childa instanceof RegistryNode&&childb instanceof RegistryNode)
					mergeTo((RegistryNode)childa,(RegistryNode)childb);
				else
					Logger.getGlobal().log(Level.INFO,"Failed to merge {0}",new Object[]{key});
			}else{
				b.put(key,a.get(key));
			}
		}
	}
	public NavigableRegistryNode<String,String> registerKeymap(String module){
		TreeMap<String,String> mapping=new TreeMap<>();
		File src=Main.INSTANCE.getFile("keymaps/default.json",module);
		if(src!=null)
			mapping.putAll((Map<String,String>)(Object)Main.INSTANCE.loadJSON(src));
		NavigableRegistryNode<String,String> registry=new NavigableRegistryNode<>(mapping);
		((RegistryNode<String,RegistryNode<String,String>>)ROOT.getOrCreateChild(module)).put(CoreModule.KEYMAP_REGISTRY_NAME,registry);
		return registry;
	}
	public RegistryNode<String,String> registerMessage(String module){
		RegistryNode<String,String> registry=new SimpleRegistryNode<>();
		ResourceBundle bundle;
		try{
			bundle=ResourceBundle.getBundle("messages",Locale.getDefault(),
					new URLClassLoader(new URL[]{new File(new File(Main.INSTANCE.getDataPath(),module),"locales").toURI().toURL()}));
			bundle.keySet().forEach((key)->registry.put(key,bundle.getString(key)));
		}catch(MalformedURLException|MissingResourceException ex){
			Logger.getGlobal().log(Level.INFO,null,ex);
		}
		((RegistryNode<String,RegistryNode<String,String>>)ROOT.getOrCreateChild(module)).put(CoreModule.MESSAGE_REGISTRY_NAME,registry);
		return registry;
	}
	public RegistryNode<String,Command> registerCommand(String module){
		RegistryNode<String,Command> registry=new SimpleRegistryNode<>();
		((RegistryNode<String,RegistryNode<String,Command>>)ROOT.getOrCreateChild(module)).put(CoreModule.COMMAND_REGISTRY_NAME,registry);
		return registry;
	}
	public static void registerApplication(String path,String baseType,DataObjectType factory,Class<? extends DataObject> cls,DataEditor editor){
		CoreModule.APPLICATION_REGISTRY.put(path,baseType);
		DataObjectTypeRegistry.addDataObjectType(factory);
		DataObjectTypeRegistry.addDataEditor(editor,cls);
		DataObjectTypeRegistry.registerMime(baseType,factory.getClass().getName());
	}
	public static void providesProtocol(String scheme,String module){
		providesCore(scheme,module,CoreModule.PROTOCOL_REGISTRY_NAME);
	}
	public static void providesDataObjectType(String type,String module){
		providesCore(type,module,CoreModule.DATA_OBJECT_TYPE_REGISTRY_NAME);
	}
	public static void providesDataObjectEditor(String type,String module){
		providesCore(type,module,CoreModule.DATA_OBJECT_EDITOR_REGISTRY_NAME);
	}
	public static void providesTypeToEditor(String type,String module){
		providesCore(type,module,CoreModule.TYPE_TO_EDITOR_REGISTRY_NAME);
	}
	public static void providesTemplateType(String type,String module){
		providesCore(type,module,CoreModule.TEMPLATE_TYPE_REGISTRY_NAME);
	}
	public static void providesApplication(String type,String module){
		providesCore(type,module,CoreModule.APPLICATION_REGISTRY_NAME);
	}
	public static void providesContentTypeLoader(String type,String module){
		providesCore(type,module,CoreModule.CONTENT_TYPE_LOADER_REGISTRY_NAME);
	}
	public static void providesDynamicMenu(String type,String module){
		providesCore(type,module,CoreModule.DYNAMIC_MENU_REGISTRY_NAME);
	}
	public static void providesCommand(String type,String module){
		providesCore(type,module,CoreModule.COMMAND_REGISTRY_NAME);
	}
	public static void providesCore(String type,String module,String registry){
		provides(type,module,registry,CoreModule.NAME);
	}
	public static void provides(String type,String module,String registry,String target){
		RegistryNode<Object,Object> core=CoreModule.PROVIDER_REGISTRY.getOrCreateChild(target);
		((RegistryNode)core.getOrCreateChild(registry)).put(type,module);
		((RegistryNode)ROOT.get(target).getOrCreateChild(registry)).put(type,LoaderValue.create(module));
	}
}
