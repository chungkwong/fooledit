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
import java.util.prefs.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class RegistryEditor extends Prompt{
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("REGISTRY_EDITOR",CoreModule.NAME);
	}
	@Override
	public Node edit(Prompt data,Object remark,RegistryNode<String,Object,String> meta){
		return new RegistryViewer();
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("REGISTRY_EDITOR",CoreModule.NAME);
	}
}
class RegistryViewer extends BorderPane{
	public RegistryViewer(){
		TreeTableView<Object> tree=new TreeTableView<>(createTreeNode(Registry.ROOT));
		tree.setEditable(true);
		TreeTableColumn<Object,String> key=new TreeTableColumn<>("KEY");
		key.prefWidthProperty().bind(tree.widthProperty().multiply(0.5));
		/*key.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue().getValue() instanceof Preferences)
					return new ReadOnlyStringWrapper(((Preferences)p.getValue().getValue()).name());
				else
					return new ReadOnlyStringWrapper(((BasicPreferenceEditor.PreferenceBean)p.getValue().getValue()).getKey());
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
					return new SimpleStringProperty(p.getValue().getValue(),"Value",((BasicPreferenceEditor.PreferenceBean)p.getValue().getValue()).getValue());
			}
		});
		tree.getColumns().addAll(key,value);*/
		setCenter(tree);
	}
	private static TreeItem<Object> createTreeNode(RegistryNode pref){
		/*return new LazyTreeItem<Object>(()->{
			return pref.getChildNames().stream().map((child)->{
				return pref.getChild(child)instanceof RegistryNode?createTreeNode((RegistryNode)pref.getChild(child)):new TreeItem<Object>(pref.getChild(child));
			}).collect(Collectors.toList());
		},pref);*/
		return null;
	}
	private static class NodeCell extends TreeCell<RegistryNode>{
		@Override
		protected void updateItem(RegistryNode item,boolean empty){
			super.updateItem(item,empty);
			if(empty||item==null){
				setText(null);
				setGraphic(null);
			}else{
				setText(item.toString());
			}
		}
	}
	private static class LeafCell{
		private final Object key;
		private final RegistryNode pref;
		public LeafCell(Object key,RegistryNode pref){
			this.key=key;
			this.pref=pref;
		}
		public Object getKey(){
			return key;
		}
		public Object getValue(){
			return pref.getChildOrDefault(key,null);
		}
		public void setValue(String value) throws BackingStoreException{
			pref.addChild(key,value);
		}
	}
}
