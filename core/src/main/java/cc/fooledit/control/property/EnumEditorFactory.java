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
import javafx.beans.value.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class EnumEditorFactory<T extends Enum<T>> implements PropertyEditorFactory<T,ComboBox<T>>{
	enum Some{
	}
	@Override
	public ComboBox<T> create(T value,boolean editable,Class<T> type){
		ComboBox<T> node=new ComboBox<T>();//FIXME
		node.setValue(value);
		return node;
	}
	@Override
	public T getValue(ComboBox<T> node){
		return node.getValue();
	}
	@Override
	public void setValue(T value,ComboBox<T> node){
		node.setValue(value);;
	}
	@Override
	public void addPropertyChangeListener(ChangeListener<? super T> listener,ComboBox<T> node){
		node.valueProperty().addListener(listener);
	}
	@Override
	public void removePropertyChangeListener(ChangeListener<? super T> listener,ComboBox<T> node){
		node.valueProperty().removeListener(listener);
	}
}
