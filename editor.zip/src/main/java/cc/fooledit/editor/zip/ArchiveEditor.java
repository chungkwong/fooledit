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
package cc.fooledit.editor.zip;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import com.sun.javafx.scene.control.skin.*;
import java.net.*;
import java.util.function.*;
import java.util.logging.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import org.apache.commons.compress.archivers.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ArchiveEditor implements DataEditor<ArchiveObject>{
	public static final ArchiveEditor INSTANCE=new ArchiveEditor();
	private final MenuRegistry menuRegistry=Registry.ROOT.registerMenu(Activator.class);
	private final RegistryNode<String,Command> commandRegistry=Registry.ROOT.registerCommand(Activator.NAME);
	private final NavigableRegistryNode<String,String> keymapRegistry=Registry.ROOT.registerKeymap(Activator.class);
	private ArchiveEditor(){
		addCommand("focus-previous",(viewer)->viewer.getTree().getFocusModel().focusPrevious());
		addCommand("focus-next",(viewer)->viewer.getTree().getFocusModel().focusNext());
		addCommand("focus-first",(viewer)->viewer.getTree().getFocusModel().focus(0));
		addCommand("focus-last",(viewer)->viewer.getTree().getFocusModel().focus(viewer.getTree().getItems().size()-1));
		addCommand("focus-first-in-directory",(viewer)->focusBeginOfDirectory(viewer.getTree()));
		addCommand("focus-last-in-directory",(viewer)->focusEndOfDirectory(viewer.getTree()));
		addCommand("focus-up",(viewer)->focusUp(viewer.getTree()));
		addCommand("focus-down",(viewer)->focusDown(viewer.getTree()));
		addCommand("move-to-previous-page",(viewer)->moveToPreviousPage(viewer.getTree()));
		addCommand("move-to-next-page",(viewer)->moveToNextPage(viewer.getTree()));
		addCommand("select",(viewer)->viewer.getTree().getSelectionModel().select(viewer.getTree().getFocusModel().getFocusedIndex()));
		addCommand("select-to",(viewer)->selectTo(viewer.getTree()));
		addCommand("deselect",(viewer)->viewer.getTree().getSelectionModel().clearSelection(viewer.getTree().getFocusModel().getFocusedIndex()));
		addCommand("clear-selection",(viewer)->viewer.getTree().getSelectionModel().clearSelection());
		addCommand("toggle-selection",(viewer)->toggleSelection(viewer.getTree()));
		addCommand("mark",(viewer)->viewer.markPaths());
		addCommand("submit",(viewer)->viewer.fireAction());
	}
	private void addCommand(String name,Consumer<ArchiveViewer> action){
		commandRegistry.put(name,new Command(name,()->action.accept((ArchiveViewer)Main.INSTANCE.getCurrentNode()),Activator.class));
	}
	@Override
	public Node edit(ArchiveObject data,Object remark,RegistryNode<String,Object> meta){
		ArchiveViewer viewer=new ArchiveViewer(data.getEntries());
		viewer.setAction((entries)->{
			entries.forEach((entry)->{
				URL url=null;
				try{
					url=new URL("archive","",data.getUrl().toString()+"!/"+entry.getName());
					Main.INSTANCE.showOnNewTab(DataObjectRegistry.readFrom(url));
				}catch(Exception ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
				}
			});
		});
		return viewer;
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("ARCHIVE_EDITOR",Activator.class);
	}
	private void focusUp(TableView<ArchiveEntry> tree){
		focusBeginOfDirectory(tree);
		TableView.TableViewFocusModel<ArchiveEntry> model=tree.getFocusModel();
		int index=model.getFocusedIndex();
		if(index>0){
			model.focus(index-1);
		}
	}
	private void focusDown(TableView<ArchiveEntry> tree){
		TableView.TableViewFocusModel<ArchiveEntry> model=tree.getFocusModel();
		int index=model.getFocusedIndex();
		ArchiveEntry item=model.getFocusedItem();
		if(item.isDirectory()&&index+1<tree.getItems().size()&&tree.getItems().get(index+1).getName().startsWith(item.getName())){
			model.focus(index+1);
		}
	}
	private void focusBeginOfDirectory(TableView<ArchiveEntry> tree){
		int index=tree.getFocusModel().getFocusedIndex();
		ObservableList<ArchiveEntry> items=tree.getItems();
		while(index>0&&!items.get(index-1).isDirectory()){
			--index;
		}
		tree.getFocusModel().focus(index);
	}
	private void focusEndOfDirectory(TableView<ArchiveEntry> tree){
		int index=tree.getFocusModel().getFocusedIndex();
		ObservableList<ArchiveEntry> items=tree.getItems();
		while(index+1<items.size()&&!items.get(index+1).isDirectory()){
			++index;
		}
		tree.getFocusModel().focus(index);
	}
	private void moveToPreviousPage(TableView<ArchiveEntry> tree){
		int newIndex=((TableViewSkin)tree.getSkin()).onScrollPageUp(true);
		tree.getFocusModel().focus(newIndex);
	}
	private void moveToNextPage(TableView<ArchiveEntry> tree){
		int newIndex=((TableViewSkin)tree.getSkin()).onScrollPageDown(true);
		tree.getFocusModel().focus(newIndex);
	}
	private void selectTo(TableView<ArchiveEntry> tree){
		int last=tree.getSelectionModel().getSelectedIndex();
		int curr=tree.getFocusModel().getFocusedIndex();
		tree.getSelectionModel().selectRange(Math.min(last,curr),Math.max(last,curr)+1);
	}
	private void toggleSelection(TableView<ArchiveEntry> tree){
		int curr=tree.getFocusModel().getFocusedIndex();
		if(tree.getSelectionModel().isSelected(curr)){
			tree.getSelectionModel().clearSelection(curr);
		}else{
			tree.getSelectionModel().select(curr);
		}
	}
	@Override
	public RegistryNode<String,Command> getCommandRegistry(){
		return commandRegistry;
	}
	@Override
	public NavigableRegistryNode<String,String> getKeymapRegistry(){
		return keymapRegistry;
	}
	@Override
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
}
