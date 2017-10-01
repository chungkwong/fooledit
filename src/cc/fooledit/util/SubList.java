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
public class SubList<T> implements List<T>{
	private final int from;
	private int to;
	private List<T> base;
	public SubList(int from,int to,List<T> base){
		this.from=from;
		this.to=to;
		this.base=base;
	}
	@Override
	public int size(){
		return to-from;
	}
	@Override
	public boolean isEmpty(){
		return to==from;
	}
	@Override
	public boolean contains(Object o){
		return indexOf(o)!=-1;
	}
	@Override
	public Iterator<T> iterator(){
		return  listIterator();
	}
	@Override
	public Object[] toArray(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public <T> T[] toArray(T[] a){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean add(T e){
		base.add(to++,e);
		return true;
	}
	@Override
	public boolean remove(Object o){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean containsAll(Collection<?> c){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean addAll(Collection<? extends T> c){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean addAll(int index,Collection<? extends T> c){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean removeAll(Collection<?> c){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean retainAll(Collection<?> c){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void clear(){
		ListIterator<T> iter=listIterator();
		while(iter.hasNext()){
			iter.remove();
		}
	}
	@Override
	public T get(int index){
		return base.get(toIndex(index));
	}
	@Override
	public T set(int index,T element){
		return base.set(toIndex(index),element);
	}
	@Override
	public void add(int index,T element){
		base.add(toPosition(index),element);
		++to;
	}
	@Override
	public T remove(int index){
		T removed=base.remove(toIndex(index));
		--to;
		return removed;
	}
	@Override
	public int indexOf(Object o){
		ListIterator<T> iter=listIterator();
		while(iter.hasNext()){
			T next=iter.next();
			if(next.equals(o)){
				return iter.previousIndex();
			}
		}
		return -1;
	}
	@Override
	public int lastIndexOf(Object o){
		ListIterator<T> iter=listIterator(size());
		while(iter.hasPrevious()){
			T next=iter.previous();
			if(next.equals(o)){
				return iter.nextIndex();
			}
		}
		return -1;
	}
	@Override
	public ListIterator<T> listIterator(){
		return listIterator(0);
	}
	@Override
	public ListIterator<T> listIterator(int index){
		return new SubListIterator(base.listIterator(toPosition(index)));
	}
	@Override
	public List<T> subList(int fromIndex,int toIndex){
		return new SubList<>(fromIndex,toIndex,this);
	}
	private int toPosition(int index){
		index+=from;
		if(index>to)
			throw new IndexOutOfBoundsException();
		return index;
	}
	private int toIndex(int index){
		index+=from;
		if(index>=to)
			throw new IndexOutOfBoundsException();
		return index;
	}
	private class SubListIterator implements ListIterator<T>{
		private final ListIterator<T> iter;
		public SubListIterator(ListIterator<T> iter){
			this.iter=iter;
		}
		@Override
		public boolean hasNext(){
			return iter.nextIndex()<to;
		}
		@Override
		public T next(){
			return iter.next();
		}
		@Override
		public boolean hasPrevious(){
			return iter.previousIndex()>=from;
		}
		@Override
		public T previous(){
			return iter.previous();
		}
		@Override
		public int nextIndex(){
			return iter.nextIndex()-from;
		}
		@Override
		public int previousIndex(){
			return iter.previousIndex()-from;
		}
		@Override
		public void remove(){
			iter.remove();
			--to;
		}
		@Override
		public void set(T e){
			iter.add(e);
		}
		@Override
		public void add(T e){
			iter.add(e);
			++to;
		}
	}
}
