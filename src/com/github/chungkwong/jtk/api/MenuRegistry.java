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
import java.util.*;
import javafx.collections.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MenuRegistry{
	private final MenuBar bar;
	private final CommandRegistry commands;
	public MenuRegistry(JSONObject json,CommandRegistry commands){
		this.commands=commands;
		this.bar=new MenuBar();
		List<JSONStuff> menus=getChildren(json);
		bar.getMenus().setAll(menus.stream().map((e)->makeMenu((JSONObject)e)).toArray(Menu[]::new));
	}
	private Menu makeMenu(JSONObject json){
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
				mi.setOnAction((e)->commands.get(commandName).run());
				items.add(mi);
			}else{
				items.add(makeMenu((JSONObject)child));
			}
		}
		return menu;
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
}
