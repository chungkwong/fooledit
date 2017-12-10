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
public class SimpleRegistryNode<T> extends RegistryNode<T>{
	private final String name;
	private final RegistryNode parent;
	private final Map<String,T> children;
	public SimpleRegistryNode(String name,RegistryNode parent){
		this.name=name;
		this.parent=parent;
		this.children=new HashMap<>();
	}
	public SimpleRegistryNode(String name,RegistryNode parent,Map<String,T> children){
		this.name=name;
		this.parent=parent;
		this.children=children;
	}
	@Override
	public RegistryNode getParent(){
		return parent;
	}
	@Override
	public T getChild(String name){
		return children.get(name);
	}
	@Override
	public boolean hasChild(String name){
		return children.containsKey(name);
	}
	@Override
	protected T addChildReal(String name,T value){
		return children.put(name,value);
	}
	@Override
	protected T removeChildReal(String name){
		return children.remove(name);
	}
	@Override
	public Collection<String> getChildNames(){
		return children.keySet();
	}
	@Override
	public String getName(){
		return name;
	}
}
