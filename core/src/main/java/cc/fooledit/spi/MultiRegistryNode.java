/*
 * Copyright (C) 2018 Chan Chung Kwong <1m02math@126.com>
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
public class MultiRegistryNode{
	public static <K,V> void addChildElement(K name,V value,RegistryNode<K,ListRegistryNode<V>> registry){
		ListRegistryNode<V> list=registry.get(name);
		if(list==null){
			list=new ListRegistryNode<>();
			registry.put(name,list);
		}
		list.put(value);
	}
	public static <K,V> void removeChildElement(K name,V value,RegistryNode<K,ListRegistryNode<V>> registry){
		ListRegistryNode<V> list=registry.get(name);
		if(list!=null){
			list.removeValue(value);
			if(list.isEmpty())
				registry.remove(name);
		}
	}
	public static <K,V> List<V> getChildElements(K name,RegistryNode<K,ListRegistryNode<V>> registry){
		ListRegistryNode<V> child=registry.get(name);
		return child!=null?child.getChildren():Collections.emptyList();
	}
}
