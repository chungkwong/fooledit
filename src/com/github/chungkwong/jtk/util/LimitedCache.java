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
public class LimitedCache<T> implements Iterable<T>{
	private int limit;
	private final LinkedList<T> cache;
	public LimitedCache(){
		this.limit=Integer.MAX_VALUE;
		this.cache=null;
	}
	public LimitedCache(int limit,Collection<T> cache){
		this.limit=limit;
		this.cache=new LinkedList<>(cache);
	}
	public int getLimit(){
		return limit;
	}
	public void setLimit(int limit){
		this.limit=limit;
		if(cache.size()>limit){
			cache.subList(limit,cache.size()).clear();
		}
	}
	public int size(){
		return cache.size();
	}
	public void add(T element){
		cache.addFirst(element);
		if(cache.size()>limit){
			cache.removeLast();
		}
	}
	public T get(int index){
		return cache.get(index);
	}
	@Override
	public Iterator<T> iterator(){
		return cache.iterator();
	}
}
