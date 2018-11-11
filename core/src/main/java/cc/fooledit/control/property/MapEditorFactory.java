/*
 * Copyright (C) 2018 Chan Chung Kwong
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
package cc.fooledit.control.property;
import cc.fooledit.core.*;
import java.util.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class MapEditorFactory implements PropertyEditorFactory<Map,TableView>{
	@Override
	public TableView create(Map value,boolean editable,Class<Map> type){
		TableView<Map.Entry<Object,Object>> node=new TableView<>();
		TableColumn<Map.Entry<Object,Object>,Object> keyColumn=new TableColumn<>(MessageRegistry.getString("KEY",Activator.class));
		keyColumn.setCellValueFactory((param)->{
			return new SimpleObjectProperty<>(param.getValue().getKey());
		});
		TableColumn<Map.Entry<Object,Object>,Object> valueColumn=new TableColumn<>(MessageRegistry.getString("VALUE",Activator.class));
		valueColumn.setCellValueFactory((param)->{
			return new SimpleObjectProperty<>(param.getValue().getValue());
		});
		node.getColumns().addAll(keyColumn,valueColumn);
		setValue(value,node);
		return node;
	}
	@Override
	public Map getValue(TableView node){
		return (Map)node.getUserData();
	}
	@Override
	public void setValue(Map value,TableView node){
		node.setUserData(value);
		node.getItems().setAll(value.entrySet());
	}
	@Override
	public void addPropertyChangeListener(ChangeListener<? super Map> listener,TableView node){
	}
	@Override
	public void removePropertyChangeListener(ChangeListener<? super Map> listener,TableView node){
	}
}
