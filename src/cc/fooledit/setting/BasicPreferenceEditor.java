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
package cc.fooledit.setting;
import cc.fooledit.control.LazyTreeItem;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;
import java.util.stream.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BasicPreferenceEditor extends Application{
	@Override
	public void start(Stage stage) throws Exception{
		TreeTableView<Object> tree=new TreeTableView<Object>(createTreeNode(Preferences.userRoot()));
		tree.setEditable(true);
		TreeTableColumn<Object,String> key=new TreeTableColumn<>("KEY");
		key.prefWidthProperty().bind(tree.widthProperty().multiply(0.5));
		key.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue().getValue() instanceof Preferences)
					return new ReadOnlyStringWrapper(((Preferences)p.getValue().getValue()).name());
				else
					return new ReadOnlyStringWrapper(((PreferenceBean)p.getValue().getValue()).getKey());
			}
		});
		TreeTableColumn<Object,String> value=new TreeTableColumn<>("VALUE");
		value.setEditable(true);
		value.prefWidthProperty().bind(tree.widthProperty().multiply(0.5));
		
		value.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue().getValue() instanceof Preferences)
					return new ReadOnlyStringWrapper("");
				else
					return new SimpleStringProperty(p.getValue().getValue(),"Value",((PreferenceBean)p.getValue().getValue()).getValue());
			}
		});
		tree.getColumns().addAll(key,value);
		stage.setScene(new Scene(new BorderPane(tree)));
		stage.setMaximized(true);
		stage.show();
	}
	private static TreeItem<Object> createTreeNode(Preferences pref){
		return new LazyTreeItem<>(()->{
			try{
				List<TreeItem<Object>> child=new ArrayList<>();
				child.addAll(Arrays.stream(pref.childrenNames()).map(pref::node).map(BasicPreferenceEditor::createTreeNode)
						.collect(Collectors.toList()));
				child.addAll(Arrays.stream(pref.keys()).map((key)->new TreeItem<Object>(new PreferenceBean(key,pref))).collect(Collectors.toList()));
				return child;
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
	private static class PreferenceBean{
		private final String key;
		private final Preferences pref;
		public PreferenceBean(String key,Preferences pref){
			this.key=key;
			this.pref=pref;
		}
		public String getKey(){
			return key;
		}
		public String getValue(){
			return pref.get(key,"");
		}
		public void setValue(String value) throws BackingStoreException{
			pref.put(key,value);
			pref.sync();
		}
	}
}
