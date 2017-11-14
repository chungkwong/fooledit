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
public class ListMultiMap<K,V>{
	private final Map<K,LinkedList<V>> map=new HashMap<>();
	public void add(K key,V value){
		LinkedList<V> set=map.get(key);
		if(set==null){
			set=new LinkedList<>();
			map.put(key,set);
		}
		set.addFirst(value);
	}
	public List<V> get(K key){
		List<V> set=map.get(key);
		if(set==null){
			set=Collections.emptyList();
		}else{
			set=Collections.unmodifiableList(set);
		}
		return set;
	}
	@Override
	public String toString(){
		return map.toString();
	}
}