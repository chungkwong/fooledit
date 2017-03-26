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
import com.github.chungkwong.jtk.*;
import com.github.chungkwong.jtk.api.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.logging.*;
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
	public static class Group{
		private boolean modified=false;
		private final Map<String,Object> settings=new HashMap<>();
		private final String id;
		Group(String id){
			this.id=id;
			try{
				File f=getFile(id+".properties");
				Properties properties=new Properties();
				properties.load(new InputStreamReader(new FileInputStream(f),StandardCharsets.UTF_8));
				properties.forEach((key,value)->settings.put(key.toString(),value.toString()));
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,"A new group is created",ex);
			}
		}
		public boolean isModified(){
			return modified;
		}
		public Object get(String key,String def){
			return settings.getOrDefault(key,def);
		}
		public Object put(String key,Object value){
			modified=true;
			return settings.put(key,value);
		}
		public Set<String> getKeys(){
			return settings.keySet();
		}
		/*public Map<String,OptionDescriptor> getDescriptors(){

		}*/
		public void store(){
			try{
				File f=getFile(id+".properties");
				f.getParentFile().mkdirs();
				Properties properties=new Properties();
				properties.putAll(settings);
				properties.store(new OutputStreamWriter(new FileOutputStream(f),StandardCharsets.UTF_8),null);
				modified=false;
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		}
	}
	static{
		EDITORS.put("string",(p)->{
			TextArea node=new TextArea();
			node.textProperty().bindBidirectional(p,CONVERTORS.get("string"));
			return node;
		});
		CONVERTORS.put("string",new StringConverter() {
			@Override
			public String toString(Object t){
				return t.toString();
			}
			@Override
			public Object fromString(String string){
				return string;
			}
		});
		EventManager.addEventListener(EventManager.SHUTDOWN,()->sync());
	}
	public static void main(String[] args){
		//load("recent");
		//getOrCreate("recent").put("78"," ");
	}
}
