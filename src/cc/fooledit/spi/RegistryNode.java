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
import java.util.stream.*;
import javafx.collections.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public abstract class RegistryNode<K,V> implements ObservableMap<K,V>{
	protected RegistryNode(){

	}
	public V getOrCreateChild(K name){
		return getOrCreateChild(name,(V)new SimpleRegistryNode());
	}
	public V getOrCreateChild(K name,V def){
		if(!containsKey(name))
			put(name,def);
		return get(name);
	}
	@Override
	public V get(Object name){
		V value=getReal((K)name);
		if(value instanceof ValueLoader){
			((ValueLoader)value).loadValue();
			value=getReal((K)name);
		}
		return value;
	}
	protected abstract V getReal(K name);
	protected void realizedAll(){
		List<V> loaders=keySet().stream().map((k)->getReal(k)).filter((v)->v instanceof ValueLoader).collect(Collectors.toList());
		while(!loaders.isEmpty()){
			loaders.forEach((loader)->((ValueLoader)loader).loadValue());
			loaders=keySet().stream().map((k)->getReal(k)).filter((v)->v instanceof ValueLoader).collect(Collectors.toList());
		}
	}
}
