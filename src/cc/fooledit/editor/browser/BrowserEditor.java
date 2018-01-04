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
package cc.fooledit.editor.browser;
import cc.fooledit.core.Registry;
import cc.fooledit.core.MenuRegistry;
import cc.fooledit.core.MessageRegistry;
import cc.fooledit.core.Command;
import cc.fooledit.core.DataEditor;
import cc.fooledit.*;
import cc.fooledit.spi.*;
import java.util.function.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.web.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BrowserEditor implements DataEditor<BrowserData>{
	public static final BrowserEditor INSTANCE=new BrowserEditor();
	private final MenuRegistry menuRegistry=new MenuRegistry(BrowserModule.NAME);
	private final RegistryNode<String,Command,String> commandRegistry=Registry.ROOT.registerCommand(BrowserModule.NAME);
	private final NavigableRegistryNode<String,String,String> keymapRegistry=Registry.ROOT.registerKeymap(BrowserModule.NAME);
	private BrowserEditor(){
		addCommand("move-to-previous-page",(viewer)->viewer.backward());
		addCommand("move-to-next-page",(viewer)->viewer.forward());
		addCommand("refresh",(viewer)->viewer.refresh());
		addCommand("set-location",(viewer)->viewer.locate());
		menuRegistry.registerDynamicMenu("editor.browser.ForwardPages",(items)->{
			WebHistory history=((BrowserData)Main.INSTANCE.getCurrentData()).getWebView().getEngine().getHistory();
			ObservableList<WebHistory.Entry> entries=history.getEntries();
			items.clear();
			int curr=history.getCurrentIndex();
			for(int i=curr+1;i<entries.size();i++){
				MenuItem item=new MenuItem(entries.get(i).getTitle());
				int index=i-curr;
				item.setOnAction((event)->{
					history.go(index);
				});
				items.add(item);
			}
		});
		menuRegistry.registerDynamicMenu("editor.browser.BackwardPages",(items)->{
			WebHistory history=((BrowserData)Main.INSTANCE.getCurrentData()).getWebView().getEngine().getHistory();
			ObservableList<WebHistory.Entry> entries=history.getEntries();
			items.clear();
			int curr=history.getCurrentIndex();
			for(int i=curr-1;i>=0;i--){
				MenuItem item=new MenuItem(entries.get(i).getTitle());
				int index=i-curr;
				item.setOnAction((event)->{
					history.go(index);
				});
				items.add(item);
			}
		});
	}
	private void addCommand(String name,Consumer<BrowserViewer> action){
		commandRegistry.addChild(name,new Command(name,()->action.accept((BrowserViewer)Main.INSTANCE.getCurrentNode()),BrowserModule.NAME));
	}
	@Override
	public Node edit(BrowserData data,Object remark,RegistryNode<String,Object,String> meta){
		return data.getEditor();
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("BROWSER",BrowserModule.NAME);
	}
	@Override
	public RegistryNode<String,Command,String> getCommandRegistry(){
		return commandRegistry;
	}
	@Override
	public NavigableRegistryNode<String,String,String> getKeymapRegistry(){
		return keymapRegistry;
	}
	@Override
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
}
