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
import cc.fooledit.util.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.beans.property.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BeanViewer extends TreeTableView<Pair<String,Object>> {
	private final Object bean;
	public BeanViewer(Object bean){
		super(createTreeItem("",bean));
		this.bean=bean;
		getColumns().add(getNameColumn());
		getColumns().add(getValueColumn());
	}
	private static TreeTableColumn<Pair<String,Object>,String> getNameColumn(){
		TreeTableColumn<Pair<String,Object>,String> column=new TreeTableColumn<>("Key");
		column.setCellValueFactory((param)->new ReadOnlyStringWrapper(Objects.toString(param.getValue().getValue().getKey())));
		return column;
	}
	private static TreeTableColumn<Pair<String,Object>,String> getValueColumn(){
		TreeTableColumn<Pair<String,Object>,String> column=new TreeTableColumn<>("Value");
		column.setCellValueFactory((param)->new ReadOnlyStringWrapper(Objects.toString(param.getValue().getValue().getValue())));
		return column;
	}
	private static TreeItem<Pair<String,Object>> createTreeItem(String name,Object obj){
		Pair<String,Object> pair=new Pair<>(name,obj);
		if(obj==null||obj.getClass().isPrimitive()){
			return new TreeItem<>(pair);
		}else{
			return new LazyTreeItem<>(pair,()->{
				return getAttributes(obj.getClass()).stream().map((key)->createTreeItem(key,getValue(key,obj))).collect(Collectors.toList());
			});
		}
	}
	private static List<String> getAttributes(Class<?> cls){
		return Arrays.stream(cls.getMethods())
				.filter((m)->m.getParameterCount()==0&&(m.getName().startsWith("get")||m.getName().startsWith("is")))
				.map((m)->m.getName()).collect(Collectors.toList());
	}
	private static Object getValue(String key,Object obj){
		try{
			return obj.getClass().getMethod(key).invoke(obj);
		}catch(ReflectiveOperationException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return null;
		}
	}
	public static void main(String[] args){
		System.out.println(getAttributes(Date.class));
	}
}
