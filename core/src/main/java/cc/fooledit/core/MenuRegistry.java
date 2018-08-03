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
import cc.fooledit.control.*;
import cc.fooledit.spi.*;
import java.util.function.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MenuRegistry{
	private final Class module;
	private final MenuBar bar=new MenuBar();
	public MenuRegistry(){
		this.module=null;
	}
	public MenuRegistry(Class module){
		this.module=module;
		setMenus(((RegistryNode<String,ListRegistryNode<RegistryNode<String,Object>>>)Registry.ROOT.getOrCreateChild(module.getPackage().getName())).get(CoreModule.MENU_REGISTRY_NAME));
		HBox.setHgrow(bar,Priority.NEVER);
	}
	private void setMenus(ListRegistryNode<RegistryNode<String,Object>> json){
		bar.getMenus().setAll(json.values().stream().map((e)->addMenu(e)).toArray(Menu[]::new));
	}
	public Menu addMenu(RegistryNode<String,Object> json){
		return new OnDemandMenu(getName(json),(items)->{
			if(json.containsKey(CHILDREN)){
				ListRegistryNode<RegistryNode<String,Object>> children=(ListRegistryNode<RegistryNode<String,Object>>)json.get(CHILDREN);
				for(RegistryNode<String,Object> props:children.values()){
					if(!props.containsKey(NAME)){
						items.add(new SeparatorMenuItem());
					}else if(props.containsKey(COMMAND)){
						String commandName=(String)props.get(COMMAND);
						MenuItem mi=new MenuItem(getName(props));
						mi.setOnAction((e)->TaskManager.executeCommand(Main.INSTANCE.getCommand(commandName)));
						items.add(mi);
					}else{
						items.add(addMenu(props));
					}
				}
			}else{
				CoreModule.DYNAMIC_MENU_REGISTRY.get((String)json.get(PROVIDER)).accept(items);
			}
		});
	}
	private String getName(RegistryNode<String,Object> json){
		return MessageRegistry.getString((String)json.get(NAME),module);
	}
	public void registerDynamicMenu(String id,Consumer<ObservableList<MenuItem>> provider){
		CoreModule.DYNAMIC_MENU_REGISTRY.put(id,provider);
	}
	public MenuBar getMenuBar(){
		return bar;
	}
	private static final String CHILDREN="children";
	private static final String NAME="name";
	private static final String COMMAND="command";
	private static final String PROVIDER="provider";
}
