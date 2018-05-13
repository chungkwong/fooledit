/*
 * Copyright (C) 2018 Chan Chung Kwong <1m02math@126.com>
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
package cc.fooledit.control;
import cc.fooledit.*;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import com.sun.javafx.scene.control.skin.*;
import java.io.*;
import java.util.*;
import javafx.collections.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TreeTableWrapper<T> extends TreeTableView<T>{
	public TreeTableWrapper(){
		installCommands();
		installKeymap();
	}
	public TreeTableWrapper(TreeItem<T> root){
		super(root);
		installCommands();
		installKeymap();
	}
	private void installKeymap(){
		TreeMap<String,String> mapping=new TreeMap<>();
		File src=Main.getFile("keymaps/treetable.json",CoreModule.NAME);
		if(src!=null)
			mapping.putAll((Map<String,String>)(Object)Main.loadJSON(src));
		NavigableRegistryNode<String,String> registry=new NavigableRegistryNode<>(mapping);
		getProperties().put(WorkSheet.KEYMAP_NAME,registry);
	}
	private void installCommands(){
		RegistryNode<String,Command> registry=new SimpleRegistryNode<>();
		getProperties().put(WorkSheet.COMMANDS_NAME,registry);
		addCommand("focus-previous",()->getFocusModel().focusPrevious(),registry);
		addCommand("focus-next",()->getFocusModel().focusNext(),registry);
		addCommand("focus-first",()->getFocusModel().focus(0),registry);
		addCommand("focus-last",()->getFocusModel().focus(getExpandedItemCount()-1),registry);
		addCommand("focus-first-in-directory",()->focusBeginOfDirectory(),registry);
		addCommand("focus-last-in-directory",()->focusEndOfDirectory(),registry);
		addCommand("focus-up",()->focusUp(),registry);
		addCommand("focus-down",()->focusDown(),registry);
		addCommand("move-to-previous-page",()->moveToPreviousPage(),registry);
		addCommand("move-to-next-page",()->moveToNextPage(),registry);
		addCommand("select",()->getSelectionModel().select(getFocusModel().getFocusedIndex()),registry);
		addCommand("select-to",()->selectTo(),registry);
		addCommand("deselect",()->getSelectionModel().clearSelection(getFocusModel().getFocusedIndex()),registry);
		addCommand("clear-selection",()->getSelectionModel().clearSelection(),registry);
		addCommand("toggle-selection",()->toggleSelection(),registry);
		addCommand("expand",()->getTreeItem(getSelectionModel().getFocusedIndex()).setExpanded(true),registry);
		addCommand("fold",()->getTreeItem(getSelectionModel().getFocusedIndex()).setExpanded(false),registry);
	}
	private void addCommand(String name,Runnable action,ObservableMap<String,Command> registry){
		registry.put(name,new Command(name,action,CoreModule.NAME));
	}
	private void moveToPreviousPage(){
		int newIndex=((TreeTableViewSkin)getSkin()).onScrollPageUp(true);
		getFocusModel().focus(newIndex);
	}
	private void moveToNextPage(){
		int newIndex=((TreeTableViewSkin)getSkin()).onScrollPageDown(true);
		getFocusModel().focus(newIndex);
	}
	private void focusUp(){
		TreeItem curr=getFocusModel().getFocusedItem();
		if(curr.isExpanded()){
			curr.setExpanded(false);
		}else{
			getFocusModel().focus(getRow(curr.getParent()));
		}
	}
	private void focusDown(){
		TreeItem<T> curr=getFocusModel().getFocusedItem();
		if(curr.isExpanded()){
			if(!curr.getChildren().isEmpty()){
				getFocusModel().focus(getRow(curr.getChildren().get(0)));
			}
		}else if(!curr.isLeaf()){
			curr.setExpanded(true);
			scrollTo(getFocusModel().getFocusedIndex());
		}
	}
	private void focusBeginOfDirectory(){
		TreeItem<T> curr=getFocusModel().getFocusedItem();
		getFocusModel().focus(getRow(curr.getParent().getChildren().get(0)));
	}
	private void focusEndOfDirectory(){
		TreeItem<T> curr=getFocusModel().getFocusedItem();
		ObservableList<TreeItem<T>> sibling=curr.getParent().getChildren();
		getFocusModel().focus(getRow(sibling.get(sibling.size()-1)));
	}
	private void toggleSelection(){
		int curr=getFocusModel().getFocusedIndex();
		if(getSelectionModel().isSelected(curr))
			getSelectionModel().clearSelection(curr);
		else
			getSelectionModel().select(curr);
	}
	private void selectTo(){
		int last=getSelectionModel().getSelectedIndex();
		int curr=getFocusModel().getFocusedIndex();
		getSelectionModel().selectRange(Math.min(last,curr),Math.max(last,curr)+1);
	}
}
