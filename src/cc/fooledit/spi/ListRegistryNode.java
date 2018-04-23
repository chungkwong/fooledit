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
import java.util.stream.*;
import javafx.beans.*;
import javafx.collections.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ListRegistryNode<V> extends RegistryNode<Integer,V>{
	private final ObservableList<V> children;
	public ListRegistryNode(){
		this.children=FXCollections.observableArrayList();
	}
	public ListRegistryNode(List<V> base){
		this.children=FXCollections.observableArrayList(base);
	}
	public List<V> getChildren(){
		return children;
	}
	@Override
	protected V getReal(Integer name){
		return children.get(name);
	}
	@Override
	public void addListener(MapChangeListener<? super Integer,? super V> listener){
		children.addListener(new MapListChangeListener(listener,this));
	}
	@Override
	public void removeListener(MapChangeListener<? super Integer,? super V> listener){
		children.removeListener(new MapListChangeListener(listener,this));
	}
	@Override
	public boolean isEmpty(){
		return children.isEmpty();
	}
	@Override
	public boolean containsKey(Object key){
		return key instanceof Integer&&(Integer)key>=0&&(Integer)key<children.size();
	}
	@Override
	public boolean containsValue(Object value){
		return children.contains(value);
	}
	@Override
	public V put(Integer key,V value){
		children.add(key,value);
		return null;
	}
	public void put(V value){
		children.add(value);
	}
	public void removeValue(V value){
		children.remove(value);
	}
	@Override
	public V remove(Object key){
		return children.remove(((Number)key).intValue());
	}
	@Override
	public void putAll(Map<? extends Integer,? extends V> m){
		m.forEach((k,v)->put(k,v));
	}
	@Override
	public void clear(){
		children.clear();
	}
	@Override
	public Set<Integer> keySet(){
		return Stream.iterate(0,(i)->i+1).limit(children.size()).collect(Collectors.toSet());
	}
	@Override
	public Collection<V> values(){
		return children;
	}
	@Override
	public Set<Entry<Integer,V>> entrySet(){
		return Stream.iterate(0,(i)->i+1).limit(children.size()).collect(Collectors.toMap((i)->i,(i)->children.get(i))).entrySet();
	}
	@Override
	public void addListener(InvalidationListener listener){
		children.addListener(listener);
	}
	@Override
	public void removeListener(InvalidationListener listener){
		children.removeListener(listener);
	}
	@Override
	public int size(){
		return children.size();
	}
}
class MapListChangeListener<V> implements ListChangeListener<V>{
	private final MapChangeListener<? super Integer,? super V> base;
	private final ListRegistryNode<V> list;
	public MapListChangeListener(MapChangeListener<? super Integer,? super V> base,ListRegistryNode<V> list){
		this.base=base;
		this.list=list;
	}
	@Override
	public void onChanged(ListChangeListener.Change<? extends V> c){
		int from=c.getFrom();
		for(V value:c.getRemoved())
			base.onChanged(new ListChange(from++,value,false));
		from=c.getFrom();
		for(V value:c.getAddedSubList())
			base.onChanged(new ListChange(from++,value,true));
	}
	@Override
	public boolean equals(Object obj){
		return (obj instanceof MapListChangeListener)&&((MapListChangeListener)obj).base.equals(base);
	}
	@Override
	public int hashCode(){
		int hash=7;
		hash=23*hash+Objects.hashCode(this.base);
		return hash;
	}
	private class ListChange extends MapChangeListener.Change<Integer,V>{
		private final Integer key;
		private final V value;
		private final boolean added;
		public ListChange(Integer key,V value,boolean added){
			super(list);
			this.key=key;
			this.value=value;
			this.added=added;
		}
		@Override
		public boolean wasAdded(){
			return added;
		}
		@Override
		public boolean wasRemoved(){
			return !added;
		}
		@Override
		public Integer getKey(){
			return key;
		}
		@Override
		public V getValueAdded(){
			return added?value:null;
		}
		@Override
		public V getValueRemoved(){
			return added?null:value;
		}
	}
}
