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
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BeanRegistryNode implements RegistryNode{
	private final String name;
	private final RegistryNode parent;
	private final Object object;
	public BeanRegistryNode(Object object,String name,RegistryNode parent){
		this.object=object;
		this.name=name;
		this.parent=parent;
	}
	@Override
	public RegistryNode getParent(){
		return parent;
	}
	@Override
	public Object getChild(String name){
		try{
			return object.getClass().getMethod("get"+name).invoke(object);
		}catch(ReflectiveOperationException|SecurityException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return null;
		}
	}
	@Override
	public void addChild(String name,Object value){
		String methodName="set"+name;
		for(Method method:object.getClass().getMethods()){
			if(method.getName().equals(methodName)&&method.getParameterCount()==1)
				try{
					method.invoke(object,value);
					return;
				}catch(IllegalAccessException|IllegalArgumentException|InvocationTargetException e){

				}
		}
		Logger.getGlobal().log(Level.INFO,"Method {0}is not founded in {1}",new Object[]{methodName,object.getClass()});
	}
	@Override
	public void addChild(RegistryNode child){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public Collection<String> getChildNames(){
		return Arrays.stream(object.getClass().getMethods())
				.filter((m)->m.getParameterCount()==0&&m.getName().startsWith("get"))
				.map((m)->m.getName().substring(3)).collect(Collectors.toList());
	}
	@Override
	public String getName(){
		return name;
	}
}
