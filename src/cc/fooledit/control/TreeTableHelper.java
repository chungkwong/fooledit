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
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import com.sun.javafx.scene.control.skin.*;
import java.util.function.*;
import javafx.collections.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TreeTableHelper{
	public static <T> void installCommonCommands(Supplier<TreeTableView<T>> widget,RegistryNode<String,Command> commands,String module){
		addCommand("focus-previous",(view)->view.getFocusModel().focusPrevious(),widget,commands,module);
		addCommand("focus-next",(view)->view.getFocusModel().focusNext(),widget,commands,module);
		addCommand("focus-first",(view)->view.getFocusModel().focus(0),widget,commands,module);
		addCommand("focus-last",(view)->view.getFocusModel().focus(view.getExpandedItemCount()-1),widget,commands,module);
		addCommand("focus-first-in-directory",(view)->focusBeginOfDirectory(view),widget,commands,module);
		addCommand("focus-last-in-directory",(view)->focusEndOfDirectory(view),widget,commands,module);
		addCommand("focus-up",(view)->focusUp(view),widget,commands,module);
		addCommand("focus-down",(view)->focusDown(view),widget,commands,module);
		addCommand("move-to-previous-page",(view)->moveToPreviousPage(view),widget,commands,module);
		addCommand("move-to-next-page",(view)->moveToNextPage(view),widget,commands,module);
		addCommand("select",(view)->view.getSelectionModel().select(view.getFocusModel().getFocusedIndex()),widget,commands,module);
		addCommand("select-to",(view)->selectTo(view),widget,commands,module);
		addCommand("deselect",(view)->view.getSelectionModel().clearSelection(view.getFocusModel().getFocusedIndex()),widget,commands,module);
		addCommand("clear-selection",(view)->view.getSelectionModel().clearSelection(),widget,commands,module);
		addCommand("toggle-selection",(view)->toggleSelection(view),widget,commands,module);
		addCommand("expand",(view)->view.getTreeItem(view.getSelectionModel().getFocusedIndex()).setExpanded(true),widget,commands,module);
		addCommand("fold",(view)->view.getTreeItem(view.getSelectionModel().getFocusedIndex()).setExpanded(false),widget,commands,module);
	}
	private static <T> void addCommand(String name,Consumer<TreeTableView<T>> action,Supplier<TreeTableView<T>> widget,RegistryNode<String,Command> commands,String module){
		commands.put(name,new Command(name,()->action.accept(widget.get()),module));
	}
	private static <T> void moveToPreviousPage(TreeTableView<T> tree){
		int newIndex=((TreeTableViewSkin)tree.getSkin()).onScrollPageUp(true);
		tree.getFocusModel().focus(newIndex);
	}
	private static <T> void moveToNextPage(TreeTableView<T> tree){
		int newIndex=((TreeTableViewSkin)tree.getSkin()).onScrollPageDown(true);
		tree.getFocusModel().focus(newIndex);
	}
	private static <T> void focusUp(TreeTableView<T> tree){
		TreeItem curr=tree.getFocusModel().getFocusedItem();
		if(curr.isExpanded()){
			curr.setExpanded(false);
		}else{
			tree.getFocusModel().focus(tree.getRow(curr.getParent()));
		}
	}
	private static <T> void focusDown(TreeTableView<T> tree){
		TreeItem<T> curr=tree.getFocusModel().getFocusedItem();
		if(curr.isExpanded()){
			if(!curr.getChildren().isEmpty()){
				tree.getFocusModel().focus(tree.getRow(curr.getChildren().get(0)));
			}
		}else if(!curr.isLeaf()){
			curr.setExpanded(true);
			tree.scrollTo(tree.getFocusModel().getFocusedIndex());
		}
	}
	private static <T> void focusBeginOfDirectory(TreeTableView<T> tree){
		TreeItem<T> curr=tree.getFocusModel().getFocusedItem();
		tree.getFocusModel().focus(tree.getRow(curr.getParent().getChildren().get(0)));
	}
	private static <T> void focusEndOfDirectory(TreeTableView<T> tree){
		TreeItem<T> curr=tree.getFocusModel().getFocusedItem();
		ObservableList<TreeItem<T>> sibling=curr.getParent().getChildren();
		tree.getFocusModel().focus(tree.getRow(sibling.get(sibling.size()-1)));
	}
	private static <T> void toggleSelection(TreeTableView<T> tree){
		int curr=tree.getFocusModel().getFocusedIndex();
		if(tree.getSelectionModel().isSelected(curr))
			tree.getSelectionModel().clearSelection(curr);
		else
			tree.getSelectionModel().select(curr);
	}
	private static <T> void selectTo(TreeTableView<T> tree){
		int last=tree.getSelectionModel().getSelectedIndex();
		int curr=tree.getFocusModel().getFocusedIndex();
		tree.getSelectionModel().selectRange(Math.min(last,curr),Math.max(last,curr)+1);
	}
}
