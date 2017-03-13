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
package com.github.chungkwong.jtk.setting;
import com.github.chungkwong.jtk.control.*;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;
import java.util.stream.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BasicPreferenceEditor extends Application{
	@Override
	public void start(Stage stage) throws Exception{
		TreeView<Preferences> tree=new TreeView(createTreeNode(Preferences.userRoot()));
		tree.setCellFactory((view)->new NodeCell());
		TableView<Map.Entry<String,String>> table=new TableView<>();
		TableColumn<Map.Entry<String,String>,String> key=new TableColumn<>("KEY");
		key.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String,String>,String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String,String>,String> p){
				return new ReadOnlyObjectWrapper<>(p.getValue().getKey());
			}
		});
		TableColumn<Map.Entry<String,String>,String> value=new TableColumn<>("VALUE");
		value.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map.Entry<String,String>,String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<Map.Entry<String,String>,String> p){
				return new ReadOnlyObjectWrapper<>(p.getValue().getValue());
			}
		});
		tree.getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
			try{
				table.getItems().setAll(Arrays.stream(n.getValue().keys()).collect(Collectors.toMap((s)->s,s->n.getValue().get(s,null))).entrySet());
			}catch(BackingStoreException ex){
				Logger.getLogger(BasicPreferenceEditor.class.getName()).log(Level.SEVERE,null,ex);
			}
		});
		stage.setScene(new Scene(new SplitPane(tree,table)));
		stage.setMaximized(true);
		stage.show();
	}
	private static TreeItem<Preferences> createTreeNode(Preferences pref){
		return new LazyTreeItem<>(()->{
			try{
				return Arrays.stream(pref.childrenNames()).map(pref::node).map(BasicPreferenceEditor::createTreeNode)
						.collect(Collectors.toList());
			}catch(BackingStoreException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				return Collections.emptyList();
			}
		},pref);
	}
	public static void main(String[] args){
		launch(args);
	}
	private static class NodeCell extends TreeCell<Preferences>{
		@Override
		protected void updateItem(Preferences item,boolean empty){
			super.updateItem(item,empty);
			if(empty||item==null){
				setText(null);
				setGraphic(null);
			}else{
				setText(item.name());
			}
		}
	}
}
