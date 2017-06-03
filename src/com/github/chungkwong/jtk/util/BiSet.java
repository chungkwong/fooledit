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
public class BiSet<T> extends AbstractSet<T>{
	private final Collection<T> set1, set2;
	public BiSet(Collection<T> set1,Collection<T> set2){
		this.set1=set1;
		this.set2=set2;
	}
	@Override
	public Iterator<T> iterator(){
		return new Iterator<T>(){
			Iterator<T> iter1=set1.iterator(), iter2=set2.iterator();
			@Override
			public boolean hasNext(){
				return iter1.hasNext()||iter2.hasNext();
			}
			@Override
			public T next(){
				return iter1.hasNext()?iter1.next():iter2.next();
			}
		};
	}
	@Override
	public int size(){
		return set1.size()+set2.size();
	}
}
