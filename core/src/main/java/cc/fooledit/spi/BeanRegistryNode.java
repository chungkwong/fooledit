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
package cc.fooledit.spi;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.beans.*;
import javafx.collections.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BeanRegistryNode extends RegistryNode<String,Object>{
	private final Object object;
	public BeanRegistryNode(Object object){
		this.object=object;
	}
	@Override
	protected Object getReal(String name){
		try{
			return object.getClass().getMethod("get"+name).invoke(object);
		}catch(ReflectiveOperationException|SecurityException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return null;
		}
	}
	@Override
	public void addListener(MapChangeListener<? super String,? super Object> listener){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void removeListener(MapChangeListener<? super String,? super Object> listener){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public int size(){
		return keySet().size();
	}
	@Override
	public boolean isEmpty(){
		return keySet().size()==0;
	}
	@Override
	public boolean containsKey(Object key){
		try{
			object.getClass().getMethod("get"+key);
			return true;
		}catch(NoSuchMethodException|SecurityException ex){
			return false;
		}
	}
	@Override
	public boolean containsValue(Object value){
		return values().contains(value);
	}
	@Override
	public Object put(String key,Object value){
		Object oldValue=get(key);
		String methodName="set"+key;
		for(Method method:object.getClass().getMethods()){
			if(method.getName().equals(methodName)&&method.getParameterCount()==1)
				try{
					method.invoke(object,value);
					return oldValue;
				}catch(IllegalAccessException|IllegalArgumentException|InvocationTargetException e){

				}
		}
		Logger.getGlobal().log(Level.INFO,"Method {0} is not founded in {1}",new Object[]{methodName,object.getClass()});
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	public Object remove(Object key){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void putAll(Map<? extends String,? extends Object> m){
		m.forEach((k,v)->put(k,v));
	}
	@Override
	public void clear(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public Set<String> keySet(){
		return Arrays.stream(object.getClass().getMethods())
				.filter((m)->m.getParameterCount()==0&&m.getName().startsWith("get"))
				.map((m)->m.getName().substring(3)).collect(Collectors.toSet());
	}
	@Override
	public Collection<Object> values(){
		return keySet().stream().map((k)->get(k)).collect(Collectors.toList());
	}
	@Override
	public Set<Entry<String,Object>> entrySet(){
		return keySet().stream().collect(Collectors.toMap((k)->k,(k)->get(k))).entrySet();
	}
	@Override
	public void addListener(InvalidationListener listener){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void removeListener(InvalidationListener listener){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
