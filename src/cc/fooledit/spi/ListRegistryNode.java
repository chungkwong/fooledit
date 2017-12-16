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
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	public int size(){
		return children.size();
	}
}
