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
public class TreeViewWrapper<T> extends TreeView<T>{
	public TreeViewWrapper(){
		installCommands();
		installKeymap();
	}
	public TreeViewWrapper(TreeItem<T> root){
		super(root);
		installCommands();
		installKeymap();
	}
	private void installKeymap(){
		TreeMap<String,String> mapping=new TreeMap<>();
		File src=Main.getFile("keymaps/tree.json",CoreModule.NAME);
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
		addCommand("focus-previous-page",()->focusToPreviousPage(),registry);
		addCommand("focus-next-page",()->focusToNextPage(),registry);
		addCommand("select-previous",()->{clearSelection();getSelectionModel().selectPrevious();},registry);
		addCommand("select-next",()->{clearSelection();getSelectionModel().selectNext();},registry);
		addCommand("select-first",()->getSelectionModel().clearAndSelect(0),registry);
		addCommand("select-last",()->getSelectionModel().clearAndSelect(getExpandedItemCount()-1),registry);
		addCommand("select-first-in-directory",()->{clearSelection();selectBeginOfDirectory();},registry);
		addCommand("select-last-in-directory",()->{clearSelection();selectEndOfDirectory();},registry);
		addCommand("select-up",()->{getSelectionModel().clearSelection();selectUp();},registry);
		addCommand("select-down",()->{getSelectionModel().clearSelection();selectDown();},registry);
		addCommand("select-previous-page",()->{clearSelection();selectToPreviousPage();},registry);
		addCommand("select-next-page",()->{clearSelection();selectToNextPage();},registry);
		addCommand("select",()->getSelectionModel().clearAndSelect(getFocusModel().getFocusedIndex()),registry);
		addCommand("select-also-previous",()->getSelectionModel().selectPrevious(),registry);
		addCommand("select-also-next",()->getSelectionModel().selectNext(),registry);
		addCommand("select-also-first",()->getSelectionModel().select(0),registry);
		addCommand("select-also-last",()->getSelectionModel().select(getExpandedItemCount()-1),registry);
		addCommand("select-also-first-in-directory",()->selectBeginOfDirectory(),registry);
		addCommand("select-also-last-in-directory",()->selectEndOfDirectory(),registry);
		addCommand("select-also-up",()->selectUp(),registry);
		addCommand("select-also-down",()->selectDown(),registry);
		addCommand("select-also-previous-page",()->selectToPreviousPage(),registry);
		addCommand("select-also-next-page",()->selectToNextPage(),registry);
		addCommand("select-also",()->getSelectionModel().select(getFocusModel().getFocusedIndex()),registry);
		addCommand("select-to",()->selectTo(),registry);
		addCommand("select-to-first",()->getSelectionModel().selectRange(0,getSelectionModel().getSelectedIndex()+1),registry);
		addCommand("select-to-last",()->getSelectionModel().selectRange(getSelectionModel().getSelectedIndex(),getExpandedItemCount()),registry);
		addCommand("select-to-first-in-directory",()->selectToBeginOfDirectory(),registry);
		addCommand("select-to-last-in-directory",()->selectToEndOfDirectory(),registry);
		addCommand("deselect",()->getSelectionModel().clearSelection(getFocusModel().getFocusedIndex()),registry);
		addCommand("clear-selection",()->getSelectionModel().clearSelection(),registry);
		addCommand("toggle-selection",()->toggleSelection(),registry);
		addCommand("expand",()->getTreeItem(getFocusModel().getFocusedIndex()).setExpanded(true),registry);
		addCommand("fold",()->getTreeItem(getFocusModel().getFocusedIndex()).setExpanded(false),registry);
		getSelectionModel().selectedIndexProperty().addListener((e,o,n)->{
			if(n!=null)
				getFocusModel().focus(n.intValue());
		});

	}
	private void addCommand(String name,Runnable action,ObservableMap<String,Command> registry){
		registry.put(name,new Command(name,action,CoreModule.NAME));
	}
	private void focusToPreviousPage(){
		int newIndex=((TreeViewSkin)getSkin()).onScrollPageUp(true);
		getFocusModel().focus(newIndex);
	}
	private void focusToNextPage(){
		int newIndex=((TreeViewSkin)getSkin()).onScrollPageDown(true);
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
	private void selectToPreviousPage(){
		int newIndex=((TreeViewSkin)getSkin()).onScrollPageUp(false);
		getSelectionModel().select(newIndex);
	}
	private void selectToNextPage(){
		int newIndex=((TreeViewSkin)getSkin()).onScrollPageDown(false);
		getSelectionModel().select(newIndex);
	}
	private void selectUp(){
		TreeItem curr=getSelectionModel().getSelectedItem();
		if(curr.isExpanded()){
			curr.setExpanded(false);
		}else{
			getSelectionModel().select(getRow(curr.getParent()));
		}
	}
	private void selectDown(){
		TreeItem<T> curr=getSelectionModel().getSelectedItem();
		if(curr.isExpanded()){
			if(!curr.getChildren().isEmpty()){
				getSelectionModel().select(getRow(curr.getChildren().get(0)));
			}
		}else if(!curr.isLeaf()){
			curr.setExpanded(true);
			scrollTo(getSelectionModel().getSelectedIndex());
		}
	}
	private void selectBeginOfDirectory(){
		TreeItem<T> curr=getSelectionModel().getSelectedItem();
		getSelectionModel().select(getRow(curr.getParent().getChildren().get(0)));
	}
	private void selectEndOfDirectory(){
		TreeItem<T> curr=getSelectionModel().getSelectedItem();
		ObservableList<TreeItem<T>> sibling=curr.getParent().getChildren();
		getSelectionModel().select(getRow(sibling.get(sibling.size()-1)));
	}
	private void selectToBeginOfDirectory(){
		TreeItem<T> curr=getSelectionModel().getSelectedItem();
		getSelectionModel().selectRange(getRow(curr.getParent().getChildren().get(0)),getSelectionModel().getSelectedIndex()+1);
	}
	private void selectToEndOfDirectory(){
		TreeItem<T> curr=getSelectionModel().getSelectedItem();
		ObservableList<TreeItem<T>> sibling=curr.getParent().getChildren();
		getSelectionModel().selectRange(getSelectionModel().getSelectedIndex(),getRow(sibling.get(sibling.size()-1))+1);
	}
	private void toggleSelection(){
		int curr=getFocusModel().getFocusedIndex();
		if(getSelectionModel().isSelected(curr))
			getSelectionModel().clearSelection(curr);
		else
			getSelectionModel().select(curr);
	}
	private void clearSelection(){
		if(getSelectionModel().getSelectionMode()==SelectionMode.MULTIPLE){
			getSelectionModel().clearSelection();
		}
	}
	private void selectTo(){
		int last=getSelectionModel().getSelectedIndex();
		int curr=getFocusModel().getFocusedIndex();
		getSelectionModel().selectRange(Math.min(last,curr),Math.max(last,curr)+1);
	}
}