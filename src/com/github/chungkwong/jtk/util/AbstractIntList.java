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

/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public abstract class AbstractIntList implements IntList{
	protected transient int modCount=0;
	@Override
	public boolean contains(int o){
		return indexOf(o)>=0;
	}
	@Override
	public boolean add(int e){
		add(size(),e);
		return true;
	}
	@Override
	public boolean removeElement(int o){
		boolean changed=false;
		IntListIterator it=listIterator();
		while(it.hasNext())
			if(o==it.nextInt()){
				it.remove();
				changed=true;
			}
		return changed;
	}
	@Override
	public boolean containsAll(IntList c){
		IntListIterator it=c.listIterator();
		while(it.hasNext())
			if(!contains(it.nextInt())){
				return false;
			}
		return true;
	}
	@Override
	public boolean addAll(IntList c){
		return addAll(size(),c);
	}
	@Override
	public boolean addAll(int index,IntList c){
		IntListIterator it=c.listIterator();
		while(it.hasNext())
			add(it.next());
		return c.isEmpty();
	}
	@Override
	public boolean removeAll(IntList c){
		boolean changed=false;
		IntListIterator it=listIterator();
		while(it.hasNext())
			if(c.contains(it.nextInt())){
				it.remove();
				changed=true;
			}
		return changed;
	}
	@Override
	public boolean retainAll(IntList c){
		boolean changed=false;
		IntListIterator it=listIterator();
		while(it.hasNext())
			if(!c.contains(it.nextInt())){
				it.remove();
				changed=true;
			}
		return changed;
	}
	@Override
	public void clear(){
		removeRange(0,size());
	}
	@Override
	public int indexOf(int o){
		IntListIterator it=listIterator();
		while(it.hasNext()){
			if(o==it.nextInt()){
				return it.previousIndex();
			}
		}
		return -1;
	}
	@Override
	public int lastIndexOf(int o){
		IntListIterator it=listIterator(size());
		while(it.hasPrevious()){
			if(o==it.previous()){
				return it.nextIndex();
			}
		}
		return -1;
	}
	@Override
	public IntListIterator listIterator(){
		return listIterator(0);
	}
	public boolean equals(Object o){
		if(o==this){
			return true;
		}
		if(!(o instanceof IntList)){
			return false;
		}
		IntListIterator e1=listIterator();
		IntListIterator e2=((IntList)o).listIterator();
		while(e1.hasNext()&&e2.hasNext()){
			int o1=e1.nextInt();
			int o2=e2.nextInt();
			if(o1!=o2){
				return false;
			}
		}
		return !(e1.hasNext()||e2.hasNext());
	}
	public int hashCode(){
		int hashCode=1;
		IntListIterator it=listIterator();
		while(it.hasNext())
			hashCode=31*hashCode+it.nextInt();
		return hashCode;
	}
	protected void removeRange(int fromIndex,int toIndex){
		IntListIterator it=listIterator(fromIndex);
		for(int i=0, n=toIndex-fromIndex;i<n;i++){
			it.next();
			it.remove();
		}
	}
}
