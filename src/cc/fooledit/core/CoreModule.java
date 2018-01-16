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
	public static final String COMMAND_REGISTRY_NAME="command";
	public static final String DATA_OBJECT_REGISTRY_NAME="data_object";
	public static final String DATA_OBJECT_TYPE_REGISTRY_NAME="data_object_type";
	public static final String DATA_OBJECT_EDITOR_REGISTRY_NAME="data_object_editor";
	public static final String DYNAMIC_MENU_REGISTRY_NAME="dynamic_menu";
	public static final String TYPE_TO_EDITOR_REGISTRY_NAME="type_to_editor";
	public static final String EVENT_REGISTRY_NAME="event";
	public static final String HISTORY_REGISTRY_NAME="history";
	public static final String MODULE_REGISTRY_NAME="module";
	public static final String INSTALLED_MODULE_REGISTRY_NAME="installed_module";
	public static final String KEYMAP_REGISTRY_NAME="keymap";
	public static final String MESSAGE_REGISTRY_NAME="message";
	public static final String MENU_REGISTRY_NAME="menu";
	public static final String PROTOCOL_REGISTRY_NAME="protocol";
	public static final String PROVIDER_REGISTRY_NAME="provider";
	public static final String PERSISTENT_REGISTRY_NAME="persistent";
	public static final String SERIALIZIER_REGISTRY_NAME="serializier";
	public static final String TEMPLATE_REGISTRY_NAME="template";
	public static final String TEMPLATE_TYPE_REGISTRY_NAME="template_type";
	public static final String WINDOW_REGISTRY_NAME="window";
	public static final RegistryNode<String,Object,String> REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,String,String> APPLICATION_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,Module,String> CLIP_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,ContentTypeDetector,String> CONTENT_TYPE_DETECTOR_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,String,String> CONTENT_TYPE_ALIAS_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,String,String> CONTENT_TYPE_SUPERCLASS_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,String,String> CONTENT_TYPE_LOADER_REGISTRY=new SimpleRegistryNode<>();
	public static final MultiRegistryNode<String,String,String> SUFFIX_REGISTRY=new MultiRegistryNode<>();
	public static final NavigableRegistryNode<String,RegistryNode,String> DATA_OBJECT_REGISTRY=new NavigableRegistryNode<>();
	public static final RegistryNode<String,DataObjectType,String> DATA_OBJECT_TYPE_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,DataEditor,String> DATA_OBJECT_EDITOR_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,Consumer<ObservableList<MenuItem>>,String> DYNAMIC_MENU_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,ListRegistryNode<String,String>,String> TYPE_TO_EDITOR_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,List<Consumer>,String> EVENT_REGISTRY=new SimpleRegistryNode<>();
	public static final ListRegistryNode<RegistryNode<String,Object,String>,String> HISTORY_REGISTRY
			=fromJSON("file_history.json",()->new ListRegistryNode<>(new LinkedList<>()));
	public static final RegistryNode<String,RegistryNode<String,Object,String>,String> MODULE_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,Object,String> INSTALLED_MODULE_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,URLStreamHandler,String> PROTOCOL_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,RegistryNode<Object,Object,String>,String> PROVIDER_REGISTRY=new SimpleRegistryNode<>();
	public static final ListRegistryNode<String,String> PERSISTENT_REGISTRY=new ListRegistryNode<>();
	public static final RegistryNode<String,Serializier,String> SERIALIZIER_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,Object,String> TEMPLATE_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,Function<Map<Object,Object>,Template>,String> TEMPLATE_TYPE_REGISTRY=new SimpleRegistryNode<>();
	public static void onLoad(){
		Registry.ROOT.addChild(NAME,REGISTRY);
		REGISTRY.addChild(APPLICATION_REGISTRY_NAME,APPLICATION_REGISTRY);
		REGISTRY.addChild(CLIP_REGISTRY_NAME,CLIP_REGISTRY);
		REGISTRY.addChild(CONTENT_TYPE_LOADER_REGISTRY_NAME,CONTENT_TYPE_LOADER_REGISTRY);
		REGISTRY.addChild(CONTENT_TYPE_DETECTOR_REGISTRY_NAME,CONTENT_TYPE_DETECTOR_REGISTRY);
		REGISTRY.addChild(CONTENT_TYPE_ALIAS_REGISTRY_NAME,CONTENT_TYPE_ALIAS_REGISTRY);
		REGISTRY.addChild(CONTENT_TYPE_SUPERCLASS_REGISTRY_NAME,CONTENT_TYPE_SUPERCLASS_REGISTRY);
		REGISTRY.addChild(SUFFIX_REGISTRY_NAME,SUFFIX_REGISTRY);
		REGISTRY.addChild(DATA_OBJECT_REGISTRY_NAME,DATA_OBJECT_REGISTRY);
		REGISTRY.addChild(DATA_OBJECT_TYPE_REGISTRY_NAME,DATA_OBJECT_TYPE_REGISTRY);
		REGISTRY.addChild(DATA_OBJECT_EDITOR_REGISTRY_NAME,DATA_OBJECT_EDITOR_REGISTRY);
		REGISTRY.addChild(DYNAMIC_MENU_REGISTRY_NAME,DYNAMIC_MENU_REGISTRY);
		REGISTRY.addChild(TYPE_TO_EDITOR_REGISTRY_NAME,TYPE_TO_EDITOR_REGISTRY);
		REGISTRY.addChild(EVENT_REGISTRY_NAME,EVENT_REGISTRY);
		HISTORY_REGISTRY.limit(20);
		REGISTRY.addChild(HISTORY_REGISTRY_NAME,HISTORY_REGISTRY);
		REGISTRY.addChild(MODULE_REGISTRY_NAME,MODULE_REGISTRY);
		REGISTRY.addChild(INSTALLED_MODULE_REGISTRY_NAME,INSTALLED_MODULE_REGISTRY);
		REGISTRY.addChild(PROTOCOL_REGISTRY_NAME,PROTOCOL_REGISTRY);
		REGISTRY.addChild(PROVIDER_REGISTRY_NAME,PROVIDER_REGISTRY);
		REGISTRY.addChild(PERSISTENT_REGISTRY_NAME,PERSISTENT_REGISTRY);
		REGISTRY.addChild(SERIALIZIER_REGISTRY_NAME,SERIALIZIER_REGISTRY);
		TEMPLATE_REGISTRY.addChild("name","");
		TEMPLATE_REGISTRY.addChild("module","core");
		TEMPLATE_REGISTRY.addChild("children",new ListRegistryNode<>());
		REGISTRY.addChild(TEMPLATE_REGISTRY_NAME,TEMPLATE_REGISTRY);
		REGISTRY.addChild(TEMPLATE_TYPE_REGISTRY_NAME,TEMPLATE_TYPE_REGISTRY);
		REGISTRY.addChild(COMMAND_REGISTRY_NAME,Main.INSTANCE.getGlobalCommandRegistry());
		REGISTRY.addChild(ModuleRegistry.REPOSITORY,"https://raw.githubusercontent.com/chungkwong/fooledit/master/MODULES");
		Registry.registerApplication("template","fooledit/template",TemplateEditor.INSTANCE,TemplateEditor.class,TemplateEditor.INSTANCE);
		Registry.registerApplication("registry","fooledit/registry",RegistryEditor.INSTANCE,RegistryEditor.class,RegistryEditor.INSTANCE);
		Registry.ROOT.loadPreference();
		EventManager.addEventListener(EventManager.SHUTDOWN,(obj)->{
			try{
				Helper.writeText(serializier.encode(HISTORY_REGISTRY),new File(Main.INSTANCE.getUserPath(),"file_history.json"));
			}catch(Exception ex){
				Logger.getLogger(CoreModule.class.getName()).log(Level.SEVERE,null,ex);
			}
		});
	}
	public static void onUnLoad(){

	}
	public static void onInstall(){
		PERSISTENT_REGISTRY.addChild("core/"+PROVIDER_REGISTRY_NAME);
		PERSISTENT_REGISTRY.addChild("core/"+PROTOCOL_REGISTRY_NAME);
		PERSISTENT_REGISTRY.addChild("core/"+PERSISTENT_REGISTRY_NAME);
		PERSISTENT_REGISTRY.addChild("core/"+INSTALLED_MODULE_REGISTRY_NAME);
		PERSISTENT_REGISTRY.addChild("core/"+TEMPLATE_REGISTRY_NAME);
		PERSISTENT_REGISTRY.addChild("core/"+CONTENT_TYPE_ALIAS_REGISTRY_NAME);
		PERSISTENT_REGISTRY.addChild("core/"+CONTENT_TYPE_SUPERCLASS_REGISTRY_NAME);
		PERSISTENT_REGISTRY.addChild("core/"+CONTENT_TYPE_LOADER_REGISTRY_NAME);
		PERSISTENT_REGISTRY.addChild("core/"+SUFFIX_REGISTRY_NAME);
	}
	private static <T> T fromJSON(String file,Supplier<T> def){
		try{
			return (T)serializier.decode(Helper.readText(new File(Main.INSTANCE.getUserPath(),file)));
		}catch(Exception ex){
			Logger.getLogger(CoreModule.class.getName()).log(Level.INFO,null,ex);
			return def.get();
		}
	}
}
