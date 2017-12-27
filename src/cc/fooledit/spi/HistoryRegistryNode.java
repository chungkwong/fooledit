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
public class HistoryRegistryNode<V,T> extends RegistryNode<Integer,V,T>{
	private int limit;
	private final ListRegistryNode<V,T> base;
	public HistoryRegistryNode(int limit){
		this(new ListRegistryNode<>(),limit);
	}
	public HistoryRegistryNode(ListRegistryNode<V,T> base,int limit){
		this.limit=limit;
		this.base=base;
		if(limit<0)
			throw new IllegalArgumentException();
	}
	@Override
	public V addChild(Integer name,V value){
		V old=super.addChild(name,value);
		if(base.size()>limit)
			removeChild(0);
		return old;
	}
	public void setLimit(int limit){
		this.limit=limit;
	}
	public int getLimit(){
		return limit;
	}
	@Override
	public V getChild(Integer name){
		return base.getChild(name);
	}
	@Override
	public boolean hasChild(Integer name){
		return base.hasChild(name);
	}
	@Override
	protected V addChildReal(Integer name,V value){
		return base.addChildReal(name,value);
	}
	@Override
	protected V removeChildReal(Integer name){
		return base.removeChildReal(name);
	}
	@Override
	public Collection<Integer> getChildNames(){
		return base.getChildNames();
	}
	public int size(){
		return base.size();
	}
}
