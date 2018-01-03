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
public class RegistryViewer extends BorderPane{
	public RegistryViewer(RegistryNode root){
		TreeTableView<Object> tree=new TreeTableView<>(createTreeNode(root));
		tree.setEditable(true);
		tree.setShowRoot(false);
		TreeTableColumn<Object,String> key=new TreeTableColumn<>(MessageRegistry.getString("KEY",CoreModule.NAME));
		key.prefWidthProperty().bind(tree.widthProperty().multiply(0.5));
		key.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue().getValue() instanceof RegistryNode)
					return new ReadOnlyStringWrapper(Objects.toString(((RegistryNode)p.getValue().getValue()).getName()));
				else
					return new ReadOnlyStringWrapper(Objects.toString(((Pair<?,?>)p.getValue().getValue()).getKey()));
			}
		});
		TreeTableColumn<Object,String> value=new TreeTableColumn<>(MessageRegistry.getString("KEY",CoreModule.NAME));
		value.setEditable(true);
		value.prefWidthProperty().bind(tree.widthProperty().multiply(0.5));

		value.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue().getValue() instanceof RegistryNode)
					return new ReadOnlyStringWrapper("");
				else
					return new ReadOnlyStringWrapper(Objects.toString(((Pair<?,?>)p.getValue().getValue()).getValue()));
					//return new SimpleStringProperty(p.getValue().getValue(),"Value",((BasicPreferenceEditor.PreferenceBean)p.getValue().getValue()).getValue());
			}
		});
		tree.getColumns().addAll(key,value);
		setCenter(tree);
	}
	private static TreeItem<Object> createTreeNode(RegistryNode pref){
		return new LazyTreeItem(()->{
			return pref.getChildNames().stream().map((child)->{
				return pref.getChild(child)instanceof RegistryNode?
						createTreeNode((RegistryNode)pref.getChild(child)):
						new TreeItem<Object>(new Pair<>(child,pref.getChild(child)));
			}).collect(Collectors.<Object>toList());
		},pref);
	}
}
