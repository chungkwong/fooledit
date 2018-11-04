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
import javafx.beans.value.*;
import javafx.scene.*;
/**
 *
 * @author Chan Chung Kwong
 */
public interface PropertyEditorFactory<T,N extends Node>{
	N create(T value,boolean editable,Class<T> type);
	T getValue(N node);
	void setValue(T value,N node);
	void addPropertyChangeListener(ChangeListener<? super T> listener,N node);
	void removePropertyChangeListener(ChangeListener<? super T> listener,N node);
	public static <T> Node createEditor(T value,boolean editable,Class<? super T> type){
		PropertyEditorFactory factory=CoreModule.PROPERTY_EDITOR_REGISTRY.get(type);
		if(factory==null){
			for(Class<?> aInterface:type.getInterfaces()){
				factory=CoreModule.PROPERTY_EDITOR_REGISTRY.get(aInterface);
				if(factory!=null){
					break;
				}
			}
			if(factory==null){
				Class<?> parent=type.getSuperclass();
				while(parent!=null){
					factory=CoreModule.PROPERTY_EDITOR_REGISTRY.get(parent);
					if(factory!=null){
						break;
					}
				}
				if(factory==null){
					factory=new BeanEditorFactory();
				}
			}
		}
		return factory.create(value,editable,type);
	}
}
