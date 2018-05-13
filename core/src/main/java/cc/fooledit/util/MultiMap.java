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
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MultiMap<K,V>{
	private final Map<K,Set<V>> map=new HashMap<>();
	public boolean add(K key,Set<V> values){
		Set<V> set=get(key);
		if(set.containsAll(values)){
			return false;
		}else{
			set.addAll(values);
			return true;
		}
	}
	public boolean add(K key,V values){
		Set<V> set=get(key);
		if(set.contains(values)){
			return false;
		}else{
			set.add(values);
			return true;
		}
	}
	public Set<V> get(K key){
		Set<V> set=map.get(key);
		if(set==null){
			set=new HashSet<>();
			map.put(key,set);
		}
		return set;
	}
	public Map<K,Set<V>> getMap(){
		return map;
	}
	@Override
	public String toString(){
		return map.toString();
	}
}