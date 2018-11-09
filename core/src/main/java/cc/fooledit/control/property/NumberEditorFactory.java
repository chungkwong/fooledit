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
public class NumberEditorFactory implements PropertyEditorFactory<Number,Spinner<Number>>{
	@Override
	public Spinner<Number> create(Number value,boolean editable,Class type){
		if(type.isPrimitive()){
			if(int.class.equals(type)){
				type=Integer.class;
			}else if(double.class.equals(type)){
				type=Double.class;
			}else if(long.class.equals(type)){
				type=Long.class;
			}else if(float.class.equals(type)){
				type=Float.class;
			}else if(short.class.equals(type)){
				type=Short.class;
			}else if(byte.class.equals(type)){
				type=Byte.class;
			}
		}
		Spinner<Number> node=new Spinner<>(Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,value.doubleValue());
		node.setUserData(type);
		return node;
	}
	@Override
	public Number getValue(Spinner<Number> node){
		Object type=node.getUserData();
		Number value=node.getValue();
		if(Integer.class.equals(type)){
			return value.intValue();
		}else if(Double.class.equals(type)){
			return value.doubleValue();
		}else if(Long.class.equals(type)){
			return value.longValue();
		}else if(Float.class.equals(type)){
			return value.floatValue();
		}else if(Short.class.equals(type)){
			return value.shortValue();
		}else if(Byte.class.equals(type)){
			return value.byteValue();
		}else{
			return value;
		}
	}
	@Override
	public void setValue(Number value,Spinner<Number> node){
		node.getValueFactory().setValue(value);
	}
	@Override
	public void addPropertyChangeListener(ChangeListener<? super Number> listener,Spinner<Number> node){
		node.getValueFactory().valueProperty().addListener(listener);
	}
	@Override
	public void removePropertyChangeListener(ChangeListener<? super Number> listener,Spinner<Number> node){
		node.getValueFactory().valueProperty().removeListener(listener);
	}
}
