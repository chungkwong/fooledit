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
public class Pair<K,V> implements Map.Entry<K,V>{
	private final K key;
	private V value;
	public Pair(K key,V value){
		this.key=key;
		this.value=value;
	}
	@Override
	public K getKey(){
		return key;
	}
	@Override
	public V getValue(){
		return value;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof Pair&&Objects.equals(((Pair)obj).key,key)&&Objects.equals(((Pair)obj).value,value);
	}
	@Override
	public int hashCode(){
		int hash=7;
		hash=79*hash+Objects.hashCode(this.key);
		hash=79*hash+Objects.hashCode(this.value);
		return hash;
	}
	@Override
	public String toString(){
		return "("+key+","+value+")";
	}
	@Override
	public V setValue(V value){
		V old=this.value;
		this.value=value;
		return old;
	}
}
