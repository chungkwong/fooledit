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
package com.github.chungkwong.fooledit.util;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BiMap<K,V> implements Map<K,V>{
	private Map<K,V> global,local;
	public BiMap(Map<K,V> global,Map<K,V> local){
		this.global=global;
		this.local=local;
	}
	public void setGlobal(Map<K,V> global){
		this.global=global;
	}
	public void setLocal(Map<K,V> local){
		this.local=local;
	}
	@Override
	public V put(K name,V value){
		return local.put(name,value);
	}
	@Override
	public void putAll(Map<? extends K,? extends V> toMerge){
		local.putAll(toMerge);
	}
	@Override
	public boolean containsKey(Object key){
		return local.containsKey(key)||global.containsKey(key);
	}
	@Override
	public V get(Object key){
		return local.containsKey(key)?local.get(key):global.get(key);
	}
	@Override
	public V remove(Object key){
		return global.remove(key);
	}
	@Override
	public int size(){
		return keySet().size();
	}
	@Override
	public boolean isEmpty(){
		return global.isEmpty()&&local.isEmpty();
	}
	@Override
	public boolean containsValue(Object value){
		return values().contains(value);
	}
	@Override
	public void clear(){
		global.clear();
	}
	@Override
	public Set<K> keySet(){
		Set<K> set=new HashSet<>(global.keySet());
		set.addAll(local.keySet());
		return set;
	}
	@Override
	public Collection<V> values(){
		Map<K,V> map=new HashMap<>(global);
		map.putAll(local);
		return map.values();
	}
	@Override
	public Set<Map.Entry<K,V>> entrySet(){
		Map<K,V> map=new HashMap<>(global);
		map.putAll(local);
		return map.entrySet();
	}
}
