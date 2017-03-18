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
package com.github.chungkwong.jtk.util;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Pair<K,V>{
	private final K key;
	private final V value;
	public Pair(K key,V value){
		this.key=key;
		this.value=value;
	}
	public K getKey(){
		return key;
	}
	public V getValue(){
		return value;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof Pair&&((Pair)obj).key.equals(key)&&((Pair)obj).value.equals(value);
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
		return "("+key+","+value+";";
	}
}
