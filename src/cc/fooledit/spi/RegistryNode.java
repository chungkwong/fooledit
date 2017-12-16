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
public abstract class RegistryNode<K,V,T>{
	private final LinkedList<RegistryChangeListener<K,V>>  listeners=new LinkedList<>();
	private T name;
	private RegistryNode<?,?,?> parent;
	public void addListener(RegistryChangeListener<K,V> listener){
		listeners.addFirst(listener);
	}
	public void removeListener(RegistryChangeListener<K,V> listener){
		listeners.remove(listener);
	}
	public RegistryNode<?,?,?> getParent(){
		return parent;
	}
	public V getOrCreateChild(K name){
		if(!hasChild(name))
			addChild(name,new SimpleRegistryNode());
		return getChild(name);
	}
	public abstract V getChild(K name);
	public abstract boolean hasChild(K name);
	public V addChild(K name,RegistryNode<?,?,? super K> child){
		if(child.name!=null)
			throw new RuntimeException("Child already added to somewhere");
		child.parent=this;
		child.name=name;
		return addChild(name,(V)child);
	}
	public V addChild(K name,V value){
		boolean exist=hasChild(name);
		V oldValue=addChildReal(name,value);
		if(exist)
			listeners.forEach((l)->l.itemChanged(name,oldValue,value,this));
		else
			listeners.forEach((l)->l.itemAdded(name,value,this));
		return oldValue;
	}
	protected abstract V addChildReal(K name,V value);
	public V removeChild(K name){
		V oldValue=removeChildReal(name);
		listeners.forEach((l)->l.itemRemoved(name,oldValue,this));
		return oldValue;
	}
	protected abstract V removeChildReal(K name);
	public abstract Collection<K> getChildNames();
	public T getName(){
		return name;
	}
}
