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
import java.util.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class ListEditorFactory implements PropertyEditorFactory<List,ListView>{
	@Override
	public ListView create(List value,boolean editable,Class<List> type){
		ListView<Object> node=new ListView<>();
		node.setCellFactory(new Callback<ListView<Object>,ListCell<Object>>(){
			@Override
			public ListCell<Object> call(ListView<Object> param){
				return new ElementCell();
			}
		});
		setValue(value,node);
		return node;
	}
	@Override
	public List getValue(ListView node){
		return node.getItems();
	}
	@Override
	public void setValue(List value,ListView node){
		if(value instanceof ObservableList){
			node.setItems((ObservableList)value);
		}else{
			node.getItems().setAll(value);
		}
	}
	@Override
	public void addPropertyChangeListener(ChangeListener<? super List> listener,ListView node){
	}
	@Override
	public void removePropertyChangeListener(ChangeListener<? super List> listener,ListView node){
	}
	private static class ElementCell extends ListCell<Object>{
		@Override
		protected void updateItem(Object item,boolean empty){
			super.updateItem(item,empty); //To change body of generated methods, choose Tools | Templates.
		}
	}
}
