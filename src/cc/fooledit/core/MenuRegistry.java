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
import com.github.chungkwong.jschememin.type.*;
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
	public MenuRegistry(){
		this.module=null;
	}
	public MenuRegistry(String module){
		this.module=module;
		setMenus(((RegistryNode<String,ListRegistryNode<RegistryNode<String,Object,String>,String>,String>)Registry.ROOT.getOrCreateChild(module)).getChild(CoreModule.MENU_REGISTRY_NAME));
		HBox.setHgrow(bar,Priority.NEVER);
	}
	private void setMenus(ListRegistryNode<RegistryNode<String,Object,String>,String> json){
		bar.getMenus().setAll(json.toMap().values().stream().map((e)->addMenu(e)).toArray(Menu[]::new));
	}
	public Menu addMenu(RegistryNode<String,Object,String> json){
		return new OnDemandMenu(getName(json),(items)->{
			if(json.hasChild(CHILDREN)){
				ListRegistryNode<RegistryNode<String,Object,String>,String> children=(ListRegistryNode<RegistryNode<String,Object,String>,String>)json.getChild(CHILDREN);
				for(RegistryNode<String,Object,String> props:children.toMap().values()){
					if(!props.hasChild(NAME)){
						items.add(new SeparatorMenuItem());
					}else if(props.hasChild(COMMAND)){
						String commandName=(String)props.getChild(COMMAND);
						MenuItem mi=new MenuItem(getName(props));
						mi.setOnAction((e)->Main.INSTANCE.getCommandRegistry().get(commandName).accept(ScmNil.NIL));
						items.add(mi);
					}else{
						items.add(addMenu(props));
					}
				}
			}else{
				CoreModule.DYNAMIC_MENU_REGISTRY.getChild((String)json.getChild(PROVIDER)).accept(items);
			}
		});
	}
	private String getName(RegistryNode<String,Object,String> json){
		return MessageRegistry.getString((String)json.getChild(NAME),module);
	}
	public void registerDynamicMenu(String id,Consumer<ObservableList<MenuItem>> provider){
		CoreModule.DYNAMIC_MENU_REGISTRY.addChild(id,provider);
	}
	public MenuBar getMenuBar(){
		return bar;
	}
	private static final String CHILDREN="children";
	private static final String NAME="name";
	private static final String COMMAND="command";
	private static final String PROVIDER="provider";
}
