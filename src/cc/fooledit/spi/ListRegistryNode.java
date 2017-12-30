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
package cc.fooledit.spi;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ListRegistryNode<V,T> extends RegistryNode<Integer,V,T>{
	private final List<V> children;
	public ListRegistryNode(){
		this.children=new ArrayList<>();
	}
	public ListRegistryNode(List<V> base){
		this.children=base;
	}
	@Override
	public V getChild(Integer index){
		return children.get(index);
	}
	@Override
	public boolean hasChild(Integer index){
		return index>=0&&index<children.size();
	}
	@Override
	protected V addChildReal(Integer index,V value){
		children.add(index,value);
		return null;
	}
	@Override
	protected V removeChildReal(Integer index){
		return children.remove((int)index);
	}
	@Override
	public Collection<Integer> getChildNames(){
		ArrayList<Integer> names=new ArrayList<>();
		int len=size();
		for(int i=0;i<len;i++)
			names.add(i);
		return names;
	}
	public void addChild(V value){
		addChild(size(),value);
	}
	public int size(){
		return children.size();
	}
	public void limit(int limit){
		addListener(new CountListener(limit));
	}
	List<V> getChildren(){
		return children;
	}
	private class CountListener implements RegistryChangeListener<Integer,V,T>{
		private int limit=Integer.MAX_VALUE;
		public CountListener(int limit){
			this.limit=limit;
			if(limit<0)
				throw new IllegalArgumentException();
		}
		@Override
		public void itemRemoved(Integer key,V oldValue,RegistryNode<Integer,V,T> node){

		}
		@Override
		public void itemAdded(Integer key,V newValue,RegistryNode<Integer,V,T> node){
			while(children.size()>limit)
				removeChild(0);
		}
	}
}
