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
import com.github.chungkwong.jtk.control.*;
import java.util.*;
import java.util.function.*;
import javafx.collections.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MenuRegistry{
	private final MenuBar bar;
	private final Main main;
	private final Map<String,Consumer<ObservableList<MenuItem>>> dynamic=new HashMap<>();
	public MenuRegistry(JSONObject json,Main main){
		this.main=main;
		this.bar=new MenuBar();
		List<JSONStuff> menus=getChildren(json);
		bar.getMenus().setAll(menus.stream().map((e)->makeMenu((JSONObject)e)).toArray(Menu[]::new));
	}
	private Menu makeMenu(JSONObject json){
		if(json.getMembers().containsKey(CHILDREN)){
			Menu menu=new Menu(getName(json));
			List<JSONStuff> children=getChildren(json);
			ObservableList<MenuItem> items=menu.getItems();
			for(JSONStuff child:children){
				Map<JSONStuff,JSONStuff> props=((JSONObject)child).getMembers();
				if(!props.containsKey(NAME)){
					items.add(new SeparatorMenuItem());
				}else if(props.containsKey(COMMAND)){
					String commandName=((JSONString)props.get(COMMAND)).getValue();
					MenuItem mi=new MenuItem(getName((JSONObject)child));
					mi.setOnAction((e)->main.getCommand(commandName).accept(main));
					items.add(mi);
				}else{
					items.add(makeMenu((JSONObject)child));
				}
			}
			return menu;
		}else{
			String id=((JSONString)json.getMembers().get(PROVIDER)).getValue();
			return new OnDemandMenu(getName(json),(items)->dynamic.get(id).accept(items));
		}
	}
	public MenuBar getMenuBar(){
		return bar;
	}
	public void registerDynamicMenu(String id,Consumer<ObservableList<MenuItem>> provider){
		dynamic.put(id,provider);
	}
	private static String getName(JSONObject obj){
		return MessageRegistry.getString(((JSONString)obj.getMembers().get(NAME)).getValue());
	}
	private List<JSONStuff> getChildren(JSONObject obj){
		return ((JSONArray)obj.getMembers().get(CHILDREN)).getElements();
	}
	private static final JSONString CHILDREN=new JSONString("children");
	private static final JSONString NAME=new JSONString("name");
	private static final JSONString COMMAND=new JSONString("command");
	private static final JSONString PROVIDER=new JSONString("provider");
}
