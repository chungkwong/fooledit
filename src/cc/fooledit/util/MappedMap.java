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
package cc.fooledit.util;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MappedMap<K,U,V> implements Map<K,V>{
	private final Map<K,U> base;
	private final Function<U,V> function;
	private final Function<V,U> inverse;
	public MappedMap(Map<K,U> base,Function<U,V> function,Function<V,U> inverse){
		this.base=base;
		this.function=function;
		this.inverse=inverse;
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
	public V get(Object key){
		return function.apply(base.get(key));
	}
	@Override
	public V put(K key,V value){
		U old=base.put(key,inverse.apply(value));
		if(base.containsKey(key))
			return function.apply(old);
		else
			return null;
	}
	@Override
	public V remove(Object key){
		return function.apply(base.get(key));
	}
	@Override
	public void putAll(Map<? extends K,? extends V> m){
		m.forEach((k,v)->base.put(k,inverse.apply(v)));
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
		return base.values().stream().map(function).collect(Collectors.toList());
	}
	@Override
	public Set<Entry<K,V>> entrySet(){
		return base.entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),(e)->function.apply(e.getValue()))).entrySet();
	}
}
