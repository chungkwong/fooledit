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
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public abstract class RegistryNode<T>{
	private final LinkedList<RegistryChangeListener<T>>  listeners=new LinkedList<>();
	public void addListener(RegistryChangeListener<T> listener){
		listeners.addFirst(listener);
	}
	public void removeListener(RegistryChangeListener<T> listener){
		listeners.remove(listener);
	}
	public abstract RegistryNode getParent();
	public T getOrCreateChild(String name){
		if(!hasChild(name))
			addChild(new SimpleRegistryNode(name,this));
		return getChild(name);
	}
	public abstract T getChild(String name);
	public abstract boolean hasChild(String name);
	public T addChild(RegistryNode child){
		return addChild(child.getName(),(T)child);
	}
	public T addChild(String name,T value){
		boolean exist=hasChild(name);
		T oldValue=addChildReal(name,value);
		if(exist)
			listeners.forEach((l)->l.itemChanged(name,oldValue,value,this));
		else
			listeners.forEach((l)->l.itemAdded(name,value,this));
		return oldValue;
	}
	protected abstract T addChildReal(String name,T value);
	public T removeChild(String name){
		T oldValue=removeChildReal(name);
		listeners.forEach((l)->l.itemRemoved(name,oldValue,this));
		return oldValue;
	}
	protected abstract T removeChildReal(String name);
	public abstract Collection<String> getChildNames();
	public abstract String getName();
}
