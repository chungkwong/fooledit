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
import cc.fooledit.util.*;
import java.util.*;
import java.util.stream.*;
import javafx.beans.*;
import javafx.collections.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SimpleRegistryNode<K,V> extends RegistryNode<K,V>{
	private final ObservableMap<K,V> base;
	public SimpleRegistryNode(){
		this.base=FXCollections.observableHashMap();
	}
	public SimpleRegistryNode(Map<K,V> base){
		this.base=FXCollections.observableMap(base);
	}
	@Override
	protected V getReal(K name){
		return base.get(name);
	}
	@Override
	public void addListener(MapChangeListener<? super K,? super V> listener){
		base.addListener(listener);
	}
	@Override
	public void removeListener(MapChangeListener<? super K,? super V> listener){
		base.removeListener(listener);
	}
	@Override
	public int size(){
		return base.size();
	}
	@Override
	public boolean isEmpty(){
		return base.isEmpty();
	}
	@Override
	public boolean containsKey(Object key){
		return base.containsKey(key);
	}
	@Override
	public boolean containsValue(Object value){
		return values().contains(value);
	}
	@Override
	public V put(K key,V value){
		return base.put(key,value);
	}
	@Override
	public V remove(Object key){
		return base.remove(key);
	}
	@Override
	public void putAll(Map<? extends K,? extends V> m){
		base.putAll(m);
	}
	@Override
	public void clear(){
		base.clear();
	}
	@Override
	public Set<K> keySet(){
		return base.keySet();
	}
	@Override
	public Collection<V> values(){
		return base.keySet().stream().map((k)->get(k)).collect(Collectors.toSet());
	}
	@Override
	public Set<Entry<K,V>> entrySet(){
		return base.keySet().stream().map((k)->new Pair<K,V>(k,get(k))).collect(Collectors.toSet());
	}
	@Override
	public void addListener(InvalidationListener listener){
		base.addListener(listener);
	}
	@Override
	public void removeListener(InvalidationListener listener){
		base.removeListener(listener);
	}
}
