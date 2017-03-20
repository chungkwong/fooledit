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
import com.github.chungkwong.jtk.model.*;
import java.io.*;
import java.util.logging.*;
import java.util.prefs.*;
import javafx.collections.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MenuRegistry{
	private final ObservableList<Menu> menus;
	private final CommandRegistry commands;
	private final Preferences pref=Preferences.userNodeForPackage(MenuRegistry.class).node("menu");
	public MenuRegistry(ObservableList<Menu> menus,CommandRegistry commands){
		this.menus=menus;
		this.commands=commands;
		try{
			pref.clear();//FIXME:Comment it out after debug
		}catch(BackingStoreException ex){
			Logger.getLogger(MenuRegistry.class.getName()).log(Level.SEVERE,null,ex);
		}
		String root=pref.get("ROOT",null);
		if(root==null)
			try{
				Preferences.importPreferences(MenuRegistry.class.getResourceAsStream("/com/github/chungkwong/jtk/default/menu.xml"));
				root=pref.get("ROOT","");
			}catch(IOException|InvalidPreferencesFormatException ex){
				Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
				root="";
			}
		for(String name:root.split(":")){
			menus.add(createMenu(name));
		}
	}
	private Menu createMenu(String name){
		String sub=pref.get(name,null);
		Menu menu=new Menu(MessageRegistry.getString(name));
		if(sub!=null){
			ObservableList<MenuItem> items=menu.getItems();
			for(String item:sub.split(":")){
				if(item.isEmpty()){
					items.add(new SeparatorMenuItem());
				}else if(pref.get(item,null)!=null){
					items.add(createMenu(item));
				}else{
					Command command=commands.get(item);
					if(command==null){
						Logger.getGlobal().log(Level.INFO,"Unknown command: {0}",item);
					}else{
						MenuItem mi=new MenuItem(command.getDisplayName());
						mi.setOnAction((e)->command.run());
						items.add(mi);
					}
				}
			}
		}
		return menu;
	}
}
