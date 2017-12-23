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
import java.util.logging.*;
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
	public V getChildOrDefault(K name,V def){
		return hasChild(name)?getChild(name):def;
	}
	public abstract V getChild(K name);
	public abstract boolean hasChild(K name);
	public V addChild(K name,RegistryNode<?,?,? super K> child){
		if(child.name!=null)
			Logger.getGlobal().log(Level.INFO,"Child already added to somewhere");
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
	public Map<K,V> toMap(){
		return new RegistryNodeMap();
	}
	private class RegistryNodeMap implements Map<K,V>{
		@Override
		public int size(){
			return getChildNames().size();
		}
		@Override
		public boolean isEmpty(){
			return getChildNames().isEmpty();
		}
		@Override
		public boolean containsKey(Object key){
			return getChildNames().contains(key);
		}
		@Override
		public boolean containsValue(Object value){
			return values().contains(value);
		}
		@Override
		public V get(Object key){
			return getChild((K)key);
		}
		@Override
		public V put(K key,V value){
			return addChild(key,value);
		}
		@Override
		public V remove(Object key){
			return removeChild((K)key);
		}
		@Override
		public void putAll(Map<? extends K,? extends V> m){
			m.forEach((k,v)->addChild(k,v));
		}
		@Override
		public void clear(){
			getChildNames().forEach((k)->removeChild(k));
		}
		@Override
		public Set<K> keySet(){
			return new AbstractSet<K>(){
				@Override
				public Iterator<K> iterator(){
					return getChildNames().iterator();
				}
				@Override
				public int size(){
					return getChildNames().size();
				}
			};
		}
		@Override
		public Collection<V> values(){
			return new AbstractList<V>(){
				@Override
				public int size(){
					return getChildNames().size();
				}
				@Override
				public V get(int index){
					Iterator<K> iter=getChildNames().iterator();
					while(--index>=0){
						iter.next();
					}
					return getChild(iter.next());
				}
			};
		}
		@Override
		public Set<Entry<K,V>> entrySet(){
			return new AbstractSet<Entry<K,V>>(){
				@Override
				public Iterator<Entry<K,V>> iterator(){
					return new Iterator<Entry<K,V>>(){
						Iterator<K> base=getChildNames().iterator();
						@Override
						public boolean hasNext(){
							return base.hasNext();
						}
						@Override
						public Entry<K,V> next(){
							K key=base.next();
							return new Map.Entry<K,V>(){
								@Override
								public K getKey(){
									return key;
								}
								@Override
								public V getValue(){
									return getChild(key);
								}
								@Override
								public V setValue(V value){
									return addChild(key,value);
								}
							};
						}
					};
				}
				@Override
				public int size(){
					return getChildNames().size();
				}
			};
		}
	}
}
