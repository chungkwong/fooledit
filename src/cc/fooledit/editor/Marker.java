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
package cc.fooledit.editor;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Marker implements Comparable<Marker>{
	private int offset;
	private Object tag;
	public Marker(int offset,Object tag){
		this.offset=offset;
		this.tag=tag;
	}
	public void setOffset(int offset){
		this.offset=offset;
	}
	public int getOffset(){
		return offset;
	}
	public Object getTag(){
		return tag;
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof Marker&&offset==((Marker)obj).offset&&Objects.equals(tag,((Marker)obj).tag);
	}
	@Override
	public int hashCode(){
		int hash=3;
		hash=13*hash+this.offset;
		hash=13*hash+Objects.hashCode(this.tag);
		return hash;
	}
	@Override
	public int compareTo(Marker o){
		return Integer.compare(offset,o.offset);
	}
	@Override
	public String toString(){
		return offset+":"+tag;
	}
}