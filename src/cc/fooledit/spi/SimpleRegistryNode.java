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
public class SimpleRegistryNode<K,V,T> extends RegistryNode<K,V,T>{
	private final Map<K,V> children;
	public SimpleRegistryNode(){
		this.children=new HashMap<>();
	}
	public SimpleRegistryNode(Map<K,V> children){
		this.children=children;
	}
	@Override
	public V getChild(K name){
		return children.get(name);
	}
	@Override
	public boolean hasChild(K name){
		return children.containsKey(name);
	}
	@Override
	protected V addChildReal(K name,V value){
		return children.put(name,value);
	}
	@Override
	protected V removeChildReal(K name){
		return children.remove(name);
	}
	@Override
	public Collection<K> getChildNames(){
		return children.keySet();
	}
	public Map.Entry<K,V> getCeilingEntry(K key){
		return ((NavigableMap<K,V>)children).ceilingEntry(key);
	}
	public Map.Entry<K,V> getFloorEntry(K key){
		return ((NavigableMap<K,V>)children).floorEntry(key);
	}
	public Map.Entry<K,V> getHigherEntry(K key){
		return ((NavigableMap<K,V>)children).higherEntry(key);
	}
	public Map.Entry<K,V> getLowerEntry(K key){
		return ((NavigableMap<K,V>)children).lowerEntry(key);
	}
}
