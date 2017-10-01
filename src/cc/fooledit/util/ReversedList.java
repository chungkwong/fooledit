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
import cc.fooledit.util.SubList;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ReversedList<T> implements List<T>{
	private final List<T> base;
	public ReversedList(List<T> base){
		this.base=base;
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
	public boolean contains(Object o){
		return base.contains(o);
	}
	@Override
	public Iterator<T> iterator(){
		return listIterator();
	}
	@Override
	public Object[] toArray(){
		return toArray(new Object[size()]);
	}
	@Override
	public <T> T[] toArray(T[] a){
		int size=size();
		if(a.length>=size){
			int i=0;
			for(int j=size-1;i<size;i++,j--)
				a[i]=(T)base.get(j);
			for(;i<a.length;i++)
				a[i]=null;
			return a;
		}else{
			return (T[])toArray();
		}
	}
	@Override
	public boolean add(T e){
		base.add(0,e);
		return true;
	}
	@Override
	public boolean remove(Object o){
		Iterator<T> iter=iterator();
		while(iter.hasNext()){
			T next=iter.next();
			if(next.equals(o)){
				iter.remove();
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean containsAll(Collection<?> c){
		return base.containsAll(c);
	}
	@Override
	public boolean addAll(Collection<? extends T> c){
		return addAll(size(),c);
	}
	@Override
	public boolean addAll(int index,Collection<? extends T> c){
		index=reversePosition(index);
		boolean changed=base.addAll(index,c);
		Collections.reverse(base.subList(index,index+c.size()));
		return changed;
	}
	@Override
	public boolean removeAll(Collection<?> c){
		return base.removeAll(c);
	}
	@Override
	public boolean retainAll(Collection<?> c){
		return base.retainAll(c);
	}
	@Override
	public void clear(){
		base.clear();
	}
	@Override
	public T get(int index){
		return base.get(reverseIndex(index));
	}
	@Override
	public T set(int index,T element){
		return base.set(reverseIndex(index),element);
	}
	@Override
	public void add(int index,T element){
		base.add(reversePosition(index),element);
	}
	@Override
	public T remove(int index){
		return base.remove(reverseIndex(index));
	}
	@Override
	public int indexOf(Object o){
		return base.lastIndexOf(o);
	}
	@Override
	public int lastIndexOf(Object o){
		return base.indexOf(o);
	}
	@Override
	public ListIterator<T> listIterator(){
		return listIterator(0);
	}
	@Override
	public ListIterator<T> listIterator(int index){
		return new ReversedListIterator(base.listIterator(reversePosition(index)));
	}
	@Override
	public List<T> subList(int fromIndex,int toIndex){
		return new SubList<>(fromIndex,toIndex,base);
	}
	private int reversePosition(int index){
		return base.size()-index;
	}
	private int reverseIndex(int index){
		return base.size()-index-1;
	}
	private class ReversedListIterator implements ListIterator<T>{
		private final ListIterator<T> iter;
		public ReversedListIterator(ListIterator<T> iter){
			this.iter=iter;
		}
		@Override
		public boolean hasNext(){
			return iter.hasPrevious();
		}
		@Override
		public T next(){
			return iter.previous();
		}
		@Override
		public boolean hasPrevious(){
			return iter.hasNext();
		}
		@Override
		public T previous(){
			return iter.next();
		}
		@Override
		public int nextIndex(){
			return iter.previousIndex();
		}
		@Override
		public int previousIndex(){
			return iter.nextIndex();
		}
		@Override
		public void remove(){
			iter.remove();
		}
		@Override
		public void set(T e){
			iter.add(e);
		}
		@Override
		public void add(T e){
			iter.add(e);
		}
	}
}