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
			GROUPS.put(key,new Group(new File(Main.getPath(),key)));
		return GROUPS.get(key);
	}
	public static void sync(){
		GROUPS.entrySet().stream().filter((e)->e.getValue().isModified()).forEach((e)->e.getValue().store(new File(Main.getPath(),e.getKey())));
	}
	public static class Group{
		private boolean modified=false;
		private final Properties properties=new Properties();
		public Group(File f){
			try{
				modified=true;
				properties.load(new InputStreamReader(new FileInputStream(f),StandardCharsets.UTF_8));
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,"A new group is created",ex);
			}
		}
		public boolean isModified(){
			return modified;
		}
		public String get(String key,String def){
			return properties.getProperty(key,def);
		}
		public Object put(String key,String value){
			modified=true;
			return properties.put(key,value);
		}
		public void store(File f){
			try{
				f.getParentFile().mkdirs();
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
