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
package cc.fooledit.api;
import cc.fooledit.*;
import cc.fooledit.control.*;
import cc.fooledit.setting.*;
import com.github.chungkwong.jschememin.type.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MenuRegistry{
	private final String module;
	private final MenuBar bar=new MenuBar();
	private final Map<String,Consumer<ObservableList<MenuItem>>> dynamic=new HashMap<>();
	public MenuRegistry(){
		this.module=null;
	}
	public MenuRegistry(String module){
		this.module=module;
		setMenus(Main.INSTANCE.loadJSON((File)SettingManager.getOrCreate(module).get("menubar-file",null)));
		HBox.setHgrow(bar,Priority.NEVER);
	}
	private void setMenus(Map<Object,Object> json){
		List<Map<Object,Object>> menus=(List<Map<Object,Object>>)json.get(CHILDREN);
		bar.getMenus().setAll(menus.stream().map((e)->addMenu(e)).toArray(Menu[]::new));
	}
	public Menu addMenu(Map<Object,Object> json){
		if(json.containsKey(CHILDREN)){
			Menu menu=new Menu(getName(json));
			List<Map<Object,Object>> children=(List<Map<Object,Object>>)json.get(CHILDREN);
			ObservableList<MenuItem> items=menu.getItems();
			for(Map<Object,Object> props:children){
				if(!props.containsKey(NAME)){
					items.add(new SeparatorMenuItem());
				}else if(props.containsKey(COMMAND)){
					String commandName=(String)props.get(COMMAND);
					MenuItem mi=new MenuItem(getName(props));
					mi.setOnAction((e)->Main.INSTANCE.getCommandRegistry().get(commandName).accept(ScmNil.NIL));
					items.add(mi);
				}else{
					items.add(addMenu(props));
				}
			}
			return menu;
		}else{
			String id=(String)json.get(PROVIDER);
			return new OnDemandMenu(getName(json),(items)->dynamic.get(id).accept(items));
		}
	}
	private String getName(Map<Object,Object> json){
		return MessageRegistry.getString((String)json.get(NAME),module);
	}
	public void registerDynamicMenu(String id,Consumer<ObservableList<MenuItem>> provider){
		dynamic.put(id,provider);
	}
	public MenuBar getMenuBar(){
		return bar;
	}
	private static final String CHILDREN="children";
	private static final String NAME="name";
	private static final String COMMAND="command";
	private static final String PROVIDER="provider";
}
