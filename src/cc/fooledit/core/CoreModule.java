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
import cc.fooledit.control.*;
import cc.fooledit.spi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import javafx.collections.*;
import javafx.concurrent.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CoreModule{
	private static final Serializier serializier= StandardSerializiers.JSON_SERIALIZIER;
	public static final String NAME="core";
	public static final String APPLICATION_REGISTRY_NAME="application";
	public static final String CLIP_REGISTRY_NAME="clip";
	public static final String CONTENT_TYPE_ALIAS_REGISTRY_NAME="content_type_alias";
	public static final String CONTENT_TYPE_SUPERCLASS_REGISTRY_NAME="content_type_superclass";
	public static final String CONTENT_TYPE_DETECTOR_REGISTRY_NAME="content_type_detector";
	public static final String CONTENT_TYPE_LOADER_REGISTRY_NAME="content_type_loader";
	public static final String SUFFIX_REGISTRY_NAME="suffix";
	public static final String GLOB_REGISTRY_NAME="glob";
	public static final String COMMAND_REGISTRY_NAME="command";
	public static final String DATA_OBJECT_REGISTRY_NAME="data_object";
	public static final String DATA_OBJECT_TYPE_REGISTRY_NAME="data_object_type";
	public static final String DATA_OBJECT_EDITOR_REGISTRY_NAME="data_object_editor";
	public static final String DYNAMIC_MENU_REGISTRY_NAME="dynamic_menu";
	public static final String TYPE_TO_EDITOR_REGISTRY_NAME="type_to_editor";
	public static final String EDITOR_TO_TOOLBOX_REGISTRY_NAME="editor_to_toolbox";
	public static final String EVENT_REGISTRY_NAME="event";
	public static final String HISTORY_REGISTRY_NAME="history";
	public static final String LOADED_MODULE_REGISTRY_NAME="loaded_module";
	public static final String LOADING_MODULE_REGISTRY_NAME="loading_module";
	public static final String INSTALLED_MODULE_REGISTRY_NAME="installed_module";
	public static final String INSTALLING_MODULE_REGISTRY_NAME="installing_module";
	public static final String KEYMAP_REGISTRY_NAME="keymap";
	public static final String MESSAGE_REGISTRY_NAME="message";
	public static final String MENU_REGISTRY_NAME="menu";
	public static final String PROTOCOL_REGISTRY_NAME="protocol";
	public static final String PROVIDER_REGISTRY_NAME="provider";
	public static final String PERSISTENT_REGISTRY_NAME="persistent";
	public static final String SERIALIZIER_REGISTRY_NAME="serializier";
	public static final String TASK_REGISTRY_NAME="task";
	public static final String TEMPLATE_REGISTRY_NAME="template";
	public static final String TEMPLATE_TYPE_REGISTRY_NAME="template_type";
	public static final String TOOLBOX_REGISTRY_NAME="toolbox";
	public static final String WINDOW_REGISTRY_NAME="window";
	public static final String MISC_REGISTRY_NAME="misc";
	public static RegistryNode<String,Object> REGISTRY;
	public static RegistryNode<String,String> APPLICATION_REGISTRY;
	public static RegistryNode<String,Object> CLIP_REGISTRY;
	public static RegistryNode<String,ContentTypeDetector> CONTENT_TYPE_DETECTOR_REGISTRY;
	public static RegistryNode<String,String> CONTENT_TYPE_ALIAS_REGISTRY;
	public static RegistryNode<String,String> CONTENT_TYPE_SUPERCLASS_REGISTRY;
	public static RegistryNode<String,String> CONTENT_TYPE_LOADER_REGISTRY;
	public static RegistryNode<String,ListRegistryNode<String>> SUFFIX_REGISTRY;
	public static RegistryNode<String,String> GLOB_REGISTRY;
	public static NavigableRegistryNode<String,RegistryNode> DATA_OBJECT_REGISTRY;
	public static RegistryNode<String,DataObjectType> DATA_OBJECT_TYPE_REGISTRY;
	public static RegistryNode<String,DataEditor> DATA_OBJECT_EDITOR_REGISTRY;
	public static RegistryNode<String,Consumer<ObservableList<MenuItem>>> DYNAMIC_MENU_REGISTRY;
	public static RegistryNode<String,ListRegistryNode<String>> TYPE_TO_EDITOR_REGISTRY;
	public static RegistryNode<String,ListRegistryNode<String>> EDITOR_TO_TOOLBOX_REGISTRY;
	public static RegistryNode<String,ListRegistryNode<Consumer>> EVENT_REGISTRY;
	public static ListRegistryNode<RegistryNode<String,Object>> HISTORY_REGISTRY;
	public static RegistryNode<String,RegistryNode<String,Object>> LOADED_MODULE_REGISTRY;
	public static RegistryNode<String,RegistryNode<String,Object>> LOADING_MODULE_REGISTRY;
	public static RegistryNode<String,Object> INSTALLED_MODULE_REGISTRY;
	public static RegistryNode<String,Object> INSTALLING_MODULE_REGISTRY;
	public static RegistryNode<String,URLStreamHandler> PROTOCOL_REGISTRY;
	public static RegistryNode<String,RegistryNode<Object,Object>> PROVIDER_REGISTRY;
	public static ListRegistryNode<String> PERSISTENT_REGISTRY;
	public static RegistryNode<String,Serializier> SERIALIZIER_REGISTRY;
	public static RegistryNode<String,Task> TASK_REGISTRY;
	public static RegistryNode<String,Object> TEMPLATE_REGISTRY;
	public static RegistryNode<String,Function<Map<Object,Object>,Template>> TEMPLATE_TYPE_REGISTRY;
	public static RegistryNode<String,ToolBox> TOOLBOX_REGISTRY;
	public static RegistryNode<String,Object> MISC_REGISTRY;
	public static void onLoad(){
		onInit();
		//HISTORY_REGISTRY.limit(20);
		REGISTRY.put(HISTORY_REGISTRY_NAME,HISTORY_REGISTRY);
		REGISTRY.put(COMMAND_REGISTRY_NAME,Main.INSTANCE.getGlobalCommandRegistry());
		Registry.registerApplication("template","fooledit/template",TemplateEditor.INSTANCE,TemplateEditor.class,TemplateEditor.INSTANCE);
		Registry.registerApplication("registry","fooledit/registry",RegistryEditor.INSTANCE,RegistryEditor.class,RegistryEditor.INSTANCE);
		EventManager.addEventListener(EventManager.SHUTDOWN,(obj)->{
			try{
				Helper.writeText(serializier.encode(HISTORY_REGISTRY),new File(Main.INSTANCE.getUserPath(),"file_history.json"));
			}catch(Exception ex){
				Logger.getLogger(CoreModule.class.getName()).log(Level.SEVERE,null,ex);
			}
		});
		EventManager.addEventListener(EventManager.SHUTDOWN,(obj)->{
			try{
				Helper.writeText(serializier.encode(HISTORY_REGISTRY),new File(Main.INSTANCE.getUserPath(),"file_history.json"));
			}catch(Exception ex){
				Logger.getLogger(CoreModule.class.getName()).log(Level.SEVERE,null,ex);
			}
			DATA_OBJECT_REGISTRY.values().forEach((data)->DataObjectRegistry.clean(data));
		});
	}
	public static void onUnLoad(){

	}
	public static void onInstall(){
		onInit();
		PERSISTENT_REGISTRY.put("core/"+PROVIDER_REGISTRY_NAME);
		PERSISTENT_REGISTRY.put("core/"+PERSISTENT_REGISTRY_NAME);
		PERSISTENT_REGISTRY.put("core/"+INSTALLED_MODULE_REGISTRY_NAME);
		PERSISTENT_REGISTRY.put("core/"+TEMPLATE_REGISTRY_NAME);
		PERSISTENT_REGISTRY.put("core/"+CONTENT_TYPE_ALIAS_REGISTRY_NAME);
		PERSISTENT_REGISTRY.put("core/"+CONTENT_TYPE_SUPERCLASS_REGISTRY_NAME);
		PERSISTENT_REGISTRY.put("core/"+CONTENT_TYPE_LOADER_REGISTRY_NAME);
		PERSISTENT_REGISTRY.put("core/"+SUFFIX_REGISTRY_NAME);
		PERSISTENT_REGISTRY.put("core/"+GLOB_REGISTRY_NAME);
		PERSISTENT_REGISTRY.put("core/"+MISC_REGISTRY_NAME);
		TEMPLATE_REGISTRY.putIfAbsent("name","");
		TEMPLATE_REGISTRY.putIfAbsent("module","core");
		TEMPLATE_REGISTRY.putIfAbsent("children",new ListRegistryNode<>());
		MISC_REGISTRY.put(ModuleRegistry.REPOSITORY,"https://raw.githubusercontent.com/chungkwong/fooledit/master/MODULES");
	}
	private static void onInit(){
		if(PROVIDER_REGISTRY!=null)
			return;
		REGISTRY=(RegistryNode<String,Object>)Registry.ROOT.getOrCreateChild(NAME);
		APPLICATION_REGISTRY=(RegistryNode<String,String>)REGISTRY.getOrCreateChild(APPLICATION_REGISTRY_NAME);
		CLIP_REGISTRY=(RegistryNode<String,Object>)REGISTRY.getOrCreateChild(CLIP_REGISTRY_NAME);
		CONTENT_TYPE_DETECTOR_REGISTRY=(RegistryNode<String,ContentTypeDetector>)REGISTRY.getOrCreateChild(CONTENT_TYPE_DETECTOR_REGISTRY_NAME);
		CONTENT_TYPE_ALIAS_REGISTRY=(RegistryNode<String,String>)REGISTRY.getOrCreateChild(CONTENT_TYPE_ALIAS_REGISTRY_NAME);
		CONTENT_TYPE_SUPERCLASS_REGISTRY=(RegistryNode<String,String>)REGISTRY.getOrCreateChild(CONTENT_TYPE_SUPERCLASS_REGISTRY_NAME);
		CONTENT_TYPE_LOADER_REGISTRY=(RegistryNode<String,String>)REGISTRY.getOrCreateChild(CONTENT_TYPE_LOADER_REGISTRY_NAME);
		SUFFIX_REGISTRY=(RegistryNode<String,ListRegistryNode<String>>)REGISTRY.getOrCreateChild(SUFFIX_REGISTRY_NAME);
		GLOB_REGISTRY=(RegistryNode<String,String>)REGISTRY.getOrCreateChild(GLOB_REGISTRY_NAME);
		DATA_OBJECT_REGISTRY=new NavigableRegistryNode<>();
		DATA_OBJECT_TYPE_REGISTRY=(RegistryNode<String,DataObjectType>)REGISTRY.getOrCreateChild(DATA_OBJECT_TYPE_REGISTRY_NAME);
		DATA_OBJECT_EDITOR_REGISTRY=(RegistryNode<String,DataEditor>)REGISTRY.getOrCreateChild(DATA_OBJECT_EDITOR_REGISTRY_NAME);
		DYNAMIC_MENU_REGISTRY=(RegistryNode<String,Consumer<ObservableList<MenuItem>>>)REGISTRY.getOrCreateChild(DYNAMIC_MENU_REGISTRY_NAME);
		TYPE_TO_EDITOR_REGISTRY=(RegistryNode<String,ListRegistryNode<String>>)REGISTRY.getOrCreateChild(TYPE_TO_EDITOR_REGISTRY_NAME);
		EDITOR_TO_TOOLBOX_REGISTRY=(RegistryNode<String,ListRegistryNode<String>>)REGISTRY.getOrCreateChild(EDITOR_TO_TOOLBOX_REGISTRY_NAME);
		EVENT_REGISTRY=(RegistryNode<String,ListRegistryNode<Consumer>>)REGISTRY.getOrCreateChild(EVENT_REGISTRY_NAME);
		HISTORY_REGISTRY=fromJSON("file_history.json",()->new ListRegistryNode<>(new LinkedList<>()));
		LOADED_MODULE_REGISTRY=(RegistryNode<String,RegistryNode<String,Object>>)REGISTRY.getOrCreateChild(LOADED_MODULE_REGISTRY_NAME);
		LOADING_MODULE_REGISTRY=(RegistryNode<String,RegistryNode<String,Object>>)REGISTRY.getOrCreateChild(LOADING_MODULE_REGISTRY_NAME);
		INSTALLED_MODULE_REGISTRY=(RegistryNode<String,Object>)REGISTRY.getOrCreateChild(INSTALLED_MODULE_REGISTRY_NAME);
		INSTALLING_MODULE_REGISTRY=(RegistryNode<String,Object>)REGISTRY.getOrCreateChild(INSTALLING_MODULE_REGISTRY_NAME);
		PROTOCOL_REGISTRY=(RegistryNode<String,URLStreamHandler>)REGISTRY.getOrCreateChild(PROTOCOL_REGISTRY_NAME);
		PROVIDER_REGISTRY=(RegistryNode<String,RegistryNode<Object,Object>>)REGISTRY.getOrCreateChild(PROVIDER_REGISTRY_NAME);
		PERSISTENT_REGISTRY=(ListRegistryNode<String>)REGISTRY.getOrCreateChild(PERSISTENT_REGISTRY_NAME,new ListRegistryNode<>());
		SERIALIZIER_REGISTRY=(RegistryNode<String,Serializier>)REGISTRY.getOrCreateChild(SERIALIZIER_REGISTRY_NAME);
		TASK_REGISTRY=(RegistryNode<String,Task>)REGISTRY.getOrCreateChild(TASK_REGISTRY_NAME);
		TEMPLATE_REGISTRY=(RegistryNode<String,Object>)REGISTRY.getOrCreateChild(TEMPLATE_REGISTRY_NAME);
		TEMPLATE_TYPE_REGISTRY=(RegistryNode<String,Function<Map<Object,Object>,Template>>)REGISTRY.getOrCreateChild(TEMPLATE_TYPE_REGISTRY_NAME);
		TOOLBOX_REGISTRY=(RegistryNode<String,ToolBox>)REGISTRY.getOrCreateChild(TOOLBOX_REGISTRY_NAME);
		MISC_REGISTRY=(RegistryNode<String,Object>)REGISTRY.getOrCreateChild(MISC_REGISTRY_NAME);
	}
	private static <T> T fromJSON(String file,Supplier<T> def){
		try{
			return (T)serializier.decode(Helper.readText(new File(Main.INSTANCE.getUserPath(),file)));
		}catch(Exception ex){
			Logger.getLogger(CoreModule.class.getName()).log(Level.INFO,null,ex);
			return def.get();
		}
	}
	static RegistryNode<String,Object> getINSTALLED_MODULE_REGISTRY(){
		if(INSTALLED_MODULE_REGISTRY==null)
			INSTALLED_MODULE_REGISTRY=(RegistryNode<String,Object>)getREGISTRY().getOrCreateChild(INSTALLED_MODULE_REGISTRY_NAME);
		return INSTALLED_MODULE_REGISTRY;
	}
	static RegistryNode<String,RegistryNode<String,Object>> getLOADED_MODULE_REGISTRY(){
		if(LOADED_MODULE_REGISTRY==null)
			LOADED_MODULE_REGISTRY=(RegistryNode<String,RegistryNode<String,Object>>)getREGISTRY().getOrCreateChild(LOADED_MODULE_REGISTRY_NAME);
		return LOADED_MODULE_REGISTRY;
	}
	static RegistryNode<String,Object> getINSTALLING_MODULE_REGISTRY(){
		if(INSTALLING_MODULE_REGISTRY==null)
			INSTALLING_MODULE_REGISTRY=(RegistryNode<String,Object>)getREGISTRY().getOrCreateChild(INSTALLING_MODULE_REGISTRY_NAME);
		return INSTALLING_MODULE_REGISTRY;
	}
	static RegistryNode<String,RegistryNode<String,Object>> getLOADING_MODULE_REGISTRY(){
		if(LOADING_MODULE_REGISTRY==null)
			LOADING_MODULE_REGISTRY=(RegistryNode<String,RegistryNode<String,Object>>)getREGISTRY().getOrCreateChild(LOADING_MODULE_REGISTRY_NAME);
		return LOADING_MODULE_REGISTRY;
	}
	static RegistryNode<String,URLStreamHandler> getPROTOCOL_REGISTRY(){
		if(PROTOCOL_REGISTRY==null)
			PROTOCOL_REGISTRY=(RegistryNode<String,URLStreamHandler>)getREGISTRY().getOrCreateChild(PROTOCOL_REGISTRY_NAME);
		return PROTOCOL_REGISTRY;
	}
	static RegistryNode<String,ListRegistryNode<Consumer>> getEVENT_REGISTRY(){
		if(EVENT_REGISTRY==null)
			EVENT_REGISTRY=(RegistryNode<String,ListRegistryNode<Consumer>>)getREGISTRY().getOrCreateChild(EVENT_REGISTRY_NAME);
		return EVENT_REGISTRY;
	}
	static RegistryNode<String,Object> getREGISTRY(){
		if(REGISTRY==null)
			REGISTRY=(RegistryNode<String,Object>)Registry.ROOT.getOrCreateChild(NAME);
		return REGISTRY;
	}
}
