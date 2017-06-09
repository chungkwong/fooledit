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
package com.github.chungkwong.fooledit.setting;
import com.github.chungkwong.fooledit.control.LazyTreeItem;
import java.util.*;
import java.util.stream.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PreferenceEditor extends BorderPane{
	public PreferenceEditor(){
		TreeTableView<Object> tree=new TreeTableView<Object>(createTreeNode(""));
		tree.setEditable(true);
		TreeTableColumn<Object,String> key=new TreeTableColumn<>("KEY");
		key.prefWidthProperty().bind(tree.widthProperty().multiply(0.5));
		key.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue().getValue() instanceof String)
					return new ReadOnlyStringWrapper(SettingManager.getLastPart((String)p.getValue().getValue()));
				else
					return new ReadOnlyStringWrapper(((PreferenceEditor.PreferenceBean)p.getValue().getValue()).getKey());
			}
		});
		TreeTableColumn<Object,PreferenceBean> value=new TreeTableColumn<>("VALUE");
		value.setEditable(true);
		value.prefWidthProperty().bind(tree.widthProperty().multiply(0.5));
		value.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Object, PreferenceBean>,ObservableValue<PreferenceBean>>(){
			@Override
			public ObservableValue<PreferenceBean> call(TreeTableColumn.CellDataFeatures<Object,PreferenceBean> p){
				if(p.getValue().getValue() instanceof String)
					return new ReadOnlyObjectWrapper(null);
				else
					return new ReadOnlyObjectWrapper(((PreferenceEditor.PreferenceBean)p.getValue().getValue()));
			}
		});
		value.setCellFactory((p)->new ValueCell());
		tree.getColumns().addAll(key,value);
		setCenter(tree);
		Label label=new Label();
		label.setWrapText(true);
		tree.getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
			if(n.getValue() instanceof PreferenceBean){
				PreferenceBean bean=(PreferenceBean)n.getValue();
				OptionDescriptor meta=bean.getGroup().getMetaData(bean.getKey());
				label.setText(meta!=null?meta.getLongDescription():"");
			}
		});

		setBottom(label);
	}
	private static TreeItem<Object> createTreeNode(String root){
		return new LazyTreeItem<>(()->{
			List<TreeItem<Object>> child=new ArrayList<>();
			child.addAll(SettingManager.getChildren(root).stream().map(PreferenceEditor::createTreeNode)
					.collect(Collectors.toList()));
			if(SettingManager.isLeaf(root)){
				SettingManager.Group group=SettingManager.getOrCreate(root);
				child.addAll(group.keys().stream().map((key)->new TreeItem<Object>(new PreferenceEditor.PreferenceBean(key,root))).collect(Collectors.toList()));
			}
			return child;
		},root);
	}
	private static class PreferenceBean{
		private final String key;
		private final SettingManager.Group grp;
		public PreferenceBean(String key,String grp){
			this.key=key;
			this.grp=SettingManager.getOrCreate(grp);
		}
		public String getKey(){
			return key;
		}
		public SettingManager.Group getGroup(){
			return grp;
		}
		public void setValue(String value){
			grp.put(key,value);
		}
	}
	private static class ValueCell extends TreeTableCell<Object,PreferenceBean>{
		public ValueCell(){
		}
		@Override
		protected void updateItem(PreferenceBean item,boolean empty){
			super.updateItem(item,empty);
			if(empty||item==null){
				setText(null);
				setGraphic(null);
			}else{
				setGraphic(item.getGroup().getEditor(item.getKey()));
			}
		}
	}
}
