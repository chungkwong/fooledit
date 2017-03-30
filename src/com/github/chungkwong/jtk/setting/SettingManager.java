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
package com.github.chungkwong.jtk.setting;
import com.github.chungkwong.json.*;
import com.github.chungkwong.jtk.*;
import com.github.chungkwong.jtk.api.*;
import com.github.chungkwong.jtk.util.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SettingManager{
	private static final Map<String,SettingEditorFactory> EDITORS=new HashMap<>();
	private static final Map<String,StringConverter> CONVERTORS=new HashMap<>();
	private static final Map<String,Group> GROUPS=new HashMap<>();
	public static Group getOrCreate(String key){
		if(!GROUPS.containsKey(key))
			GROUPS.put(key,new Group(key));
		return GROUPS.get(key);
	}
	public static void sync(){
		GROUPS.entrySet().stream().filter((e)->e.getValue().isModified()).forEach((e)->e.getValue().store());
	}
	private static File getFile(String key){
		return new File(new File(Main.getPath(),"settings"),key);
	}
	public static Set<String> getChildren(String parent){
		if(parent.isEmpty()){
			return GROUPS.keySet().stream().map((k)->{
				int i=k.indexOf('.');
				return i==-1?k:k.substring(0,i);
			}).collect(Collectors.toSet());
		}else{
			String prefix=parent+".";
			int after=prefix.length();
			return GROUPS.keySet().stream().filter((k)->k.startsWith(prefix)).map((k)->{
				int i=k.indexOf('.',after);
				return i==-1?k:k.substring(0,i);
			}).collect(Collectors.toSet());
		}
	}
	public static boolean isLeaf(String key){
		return GROUPS.containsKey(key);
	}
	public static String getLastPart(String grp){
		int i=grp.lastIndexOf('.');
		return i==-1?grp:grp.substring(i+1);
	}
	public static void registerSettingType(String type,StringConverter converter){
		CONVERTORS.put(type,converter);
	}
	public static void registerSettingEditorFactory(String type,SettingEditorFactory editorFactory){
		EDITORS.put(type,editorFactory);
	}
	public static class Group{
		private boolean modified=false;
		private final Map<String,Object> settings=new HashMap<>();
		private final Map<String,String> types=new HashMap<>();
		private final String id;
		private final Cache<Map<String,OptionDescriptor>> meta;
		Group(String id){
			this.id=id;
			this.meta=new Cache<>(()->{
				try{
					JSONObject json=(JSONObject)JSONParser.parse(new InputStreamReader(new FileInputStream(getFile(id+".json")),StandardCharsets.UTF_8));
					return json.getMembers().entrySet().stream().collect(Collectors.toMap((e)->((JSONString)e.getKey()).getValue(),(e)->OptionDescriptor.fromJSONObject((JSONObject)e.getValue())));
				}catch(IOException|SyntaxException ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
					return Collections.emptyMap();
				}
			});
			meta.get().forEach((k,d)->{
				settings.put(k,CONVERTORS.get(d.getType()).fromString(d.getDefaultValue()));
				types.put(k,d.getType());
			});
			try{
				File f=getFile(id+".properties");
				Properties properties=new Properties();
				properties.load(new InputStreamReader(new FileInputStream(f),StandardCharsets.UTF_8));
				properties.forEach((key,value)->settings.put(key.toString(),CONVERTORS.get(getType(key.toString())).fromString(value.toString())));
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,"A new group is created",ex);
			}
		}
		public boolean isModified(){
			return modified;
		}
		public Object get(String key,Object def){
			return settings.getOrDefault(key,def);
		}
		public Node getEditor(String key){
			return EDITORS.getOrDefault(getType(key),DEFAULT_EDITOR_FACTORY).getEditor(key,this);
		}
		public String getType(String key){
			return types.get(key);
		}
		public OptionDescriptor getMetaData(String key){
			return meta.get().get(key);
		}
		public Object put(String key,Object value){
			modified=true;
			return settings.put(key,value);
		}
		public Set<String> keys(){
			return settings.keySet();
		}
		public void store(){
			try{
				File f=getFile(id+".properties");
				f.getParentFile().mkdirs();
				Properties properties=new Properties();
				settings.entrySet().forEach((e)->properties.put(e.getKey(),CONVERTORS.get(getType(e.getKey())).toString(e.getValue())));
				properties.store(new OutputStreamWriter(new FileOutputStream(f),StandardCharsets.UTF_8),null);
				modified=false;
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		}
	}
	static{
		StringConverter stdConvertor=new StringConverter() {
			@Override
			public String toString(Object t){
				return t.toString();
			}
			@Override
			public Object fromString(String string){
				return string;
			}
		};
		CONVERTORS.put(null,stdConvertor);
		CONVERTORS.put("string",stdConvertor);
		CONVERTORS.put("boolean",new StringConverter() {
			@Override
			public String toString(Object t){
				return ((Boolean)t).toString();
			}
			@Override
			public Boolean fromString(String string){
				return Boolean.parseBoolean(string);
			}
		});
		EDITORS.put("boolean",(key,grp)->{
			CheckBox node=new CheckBox();
			node.setSelected((Boolean)grp.get(key,false));
			node.selectedProperty().addListener((e,o,n)->{
				grp.put(key,n);
			});
			return node;
		});
		EventManager.addEventListener(EventManager.SHUTDOWN,()->sync());
	}
	private static final SettingEditorFactory DEFAULT_EDITOR_FACTORY=(key,grp)->{
		TextArea node=new TextArea();
		String text=CONVERTORS.get(grp.getType(key)).toString(grp.get(key,""));
		node.setText(text);
		node.setPrefRowCount(node.getParagraphs().size());
		node.textProperty().addListener((e,o,n)->{
			try{
				grp.put(key,CONVERTORS.get(grp.getType(key)).fromString(n));
			}catch(Exception ex){
			}
		});
		return node;
	};
	public static void main(String[] args){
		//load("recent");
		//getOrCreate("recent").put("78"," ");
	}
}
