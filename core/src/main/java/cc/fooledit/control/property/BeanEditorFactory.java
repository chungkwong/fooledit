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
import java.beans.*;
import java.util.*;
import java.util.logging.*;
import javafx.beans.value.*;
import javafx.scene.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class BeanEditorFactory implements PropertyEditorFactory<Object,Node>{
	@Override
	public Node create(Object value,boolean editable,Class<Object> type){
		try{
			Introspector.getBeanInfo(type);
		}catch(IntrospectionException ex){
			Logger.getLogger(BeanEditorFactory.class.getName()).log(Level.SEVERE,null,ex);
			return null;
		}
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public Object getValue(Node node){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void setValue(Object value,Node node){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void addPropertyChangeListener(ChangeListener<? super Object> listener,Node node){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void removePropertyChangeListener(ChangeListener<? super Object> listener,Node node){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	public static void main(String[] args) throws IntrospectionException{
		BeanInfo beanInfo=Introspector.getBeanInfo(List.class);
		for(PropertyDescriptor descriptor:beanInfo.getPropertyDescriptors()){
			System.out.println(descriptor);
		}
	}
}
