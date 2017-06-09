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
public class Tuple<A,B,C>{
	private final A first;
	private final B second;
	private final C third;
	public Tuple(A first,B second,C third){
		this.first=first;
		this.second=second;
		this.third=third;
	}
	public A getFirst(){
		return first;
	}
	public B getSecond(){
		return second;
	}
	public C getThird(){
		return third;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof Tuple&&((Tuple)obj).first.equals(first)&&
				((Tuple)obj).second.equals(second)&&((Tuple)obj).third.equals(third);
	}
	@Override
	public int hashCode(){
		int hash=3;
		hash=29*hash+Objects.hashCode(this.first);
		hash=29*hash+Objects.hashCode(this.second);
		hash=29*hash+Objects.hashCode(this.third);
		return hash;
	}
	@Override
	public String toString(){
		return "("+first+","+second+","+third+")";
	}
}
