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
public class NavigableRegistryNode<K,V> extends SimpleRegistryNode<K,V>{
	private final NavigableMap<K,V> underlying;
	public NavigableRegistryNode(){
		this(new TreeMap<>());
	}
	public NavigableRegistryNode(NavigableMap<K,V> children){
		super(children);
		this.underlying=children;
	}
	public Map.Entry<K,V> firstEntry(){
		return underlying.firstEntry();
	}
	public Map.Entry<K,V> lastEntry(){
		return underlying.lastEntry();
	}
	public Map.Entry<K,V> ceilingEntry(K key){
		return underlying.ceilingEntry(key);
	}
	public Map.Entry<K,V> floorEntry(K key){
		return underlying.floorEntry(key);
	}
	public Map.Entry<K,V> higherEntry(K key){
		return underlying.higherEntry(key);
	}
	public Map.Entry<K,V> lowerEntry(K key){
		return underlying.lowerEntry(key);
	}
}
