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
import com.github.chungkwong.json.*;
import java.io.*;
import java.nio.charset.*;
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
		for(String name:path.split("/")){
			node=(RegistryNode)node.getOrCreateChild(name);
		}
		return node;
	}
	public String[] splitPath(String path){
		int index=path.lastIndexOf('/');
		String parent=index==-1?"":path.substring(0,index);
		String leaf=index==-1?path:path.substring(index+1);
		return new String[]{parent,leaf};
	}
	public MenuRegistry registerMenu(Class module){
		try{
			InputStream src=module.getResourceAsStream("/menu.json");
			RegistryNode<String,List<ListRegistryNode<Object>>> json=(RegistryNode<String,List<ListRegistryNode<Object>>>)StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(src));
			RegistryNode<String,ListRegistryNode<Object>> mod=((RegistryNode<String,ListRegistryNode<Object>>)Registry.ROOT.getOrCreateChild(module.getPackage().getName()));
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
				if(childa instanceof RegistryNode&&childb instanceof RegistryNode){
					mergeTo((RegistryNode)childa,(RegistryNode)childb);
				}else{
					Logger.getGlobal().log(Level.INFO,"Failed to merge {0}",new Object[]{key});
				}
			}else{
				b.put(key,a.get(key));
			}
		}
	}
	public NavigableRegistryNode<String,String> registerKeymap(Class module){
		TreeMap<String,String> mapping=new TreeMap<>();
		try{
			mapping.putAll((Map<String,String>)(Object)JSONDecoder.decode(new InputStreamReader(module.getResourceAsStream("/keymap.json"),StandardCharsets.UTF_8)));
		}catch(IOException|SyntaxException ex){
			Logger.getLogger(Registry.class.getName()).log(Level.SEVERE,null,ex);
		}
		NavigableRegistryNode<String,String> registry=new NavigableRegistryNode<>(mapping);
		((RegistryNode<String,RegistryNode<String,String>>)ROOT.getOrCreateChild(module.getPackage().getName())).put(CoreModule.KEYMAP_REGISTRY_NAME,registry);
		return registry;
	}
	public RegistryNode<String,String> registerMessage(Class module){
		RegistryNode<String,String> registry=new SimpleRegistryNode<>();
		ResourceBundle bundle=ResourceBundle.getBundle("/messages",Locale.getDefault(),module.getClassLoader());
		bundle.keySet().forEach((key)->registry.put(key,bundle.getString(key)));
		((RegistryNode<String,RegistryNode<String,String>>)ROOT.getOrCreateChild(module.getPackage().getName())).put(CoreModule.MESSAGE_REGISTRY_NAME,registry);
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
}
