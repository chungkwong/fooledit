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
public class NavigableRegistryNode<K,V,T> extends RegistryNode<K,V,T>{
	private final NavigableMap<K,V> children;
	public NavigableRegistryNode(){
		this.children=new TreeMap<>();
	}
	public NavigableRegistryNode(NavigableMap<K,V> children){
		this.children=children;
	}
	@Override
	public V getChildReal(K name){
		return children.get(name);
	}
	@Override
	public boolean hasChildReal(K name){
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
	public Collection<K> getChildNamesReal(){
		return children.keySet();
	}
	public Map.Entry<K,V> firstEntry(){
		return children.firstEntry();
	}
	public Map.Entry<K,V> lastEntry(){
		return children.lastEntry();
	}
	public Map.Entry<K,V> ceilingEntry(K key){
		return children.ceilingEntry(key);
	}
	public Map.Entry<K,V> floorEntry(K key){
		return children.floorEntry(key);
	}
	public Map.Entry<K,V> higherEntry(K key){
		return children.higherEntry(key);
	}
	public Map.Entry<K,V> lowerEntry(K key){
		return children.lowerEntry(key);
	}
}
