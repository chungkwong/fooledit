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
public class SimpleRegistryNode implements RegistryNode{
	private final String name;
	private final RegistryNode parent;
	private final Map<String,Object> children;
	public SimpleRegistryNode(String name,RegistryNode parent){
		this.name=name;
		this.parent=parent;
		this.children=new HashMap<>();
	}
	@Override
	public RegistryNode getParent(){
		return parent;
	}
	@Override
	public Object getChild(String name){
		return children.get(name);
	}
	@Override
	public Collection<String> getChildNames(){
		return children.keySet();
	}
	@Override
	public String getName(){
		return name;
	}
	@Override
	public void addChild(String name,Object value){
		children.put(name,value);
	}
	@Override
	public void addChild(RegistryNode child){
		children.put(child.getName(),child);
	}
}
