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
		if(value instanceof LoaderValue){
			remove(name);
			((LoaderValue)value).loadValue();
			value=getReal((K)name);
		}else if(value instanceof LazyValue){
			value=((LazyValue<V>)value).getValue();
		}
		return value;
	}
	protected abstract V getReal(K name);
	protected void realizedAll(){
		Collection<LoaderValue> loaders=collectLoader();
		while(!loaders.isEmpty()){
			loaders.forEach((loader)->loader.loadValue());
			loaders=collectLoader();
		}
	}
	private Collection<LoaderValue> collectLoader(){
		HashSet<K> keys=new HashSet<>();
		HashSet<LoaderValue> loaders=new HashSet<>();
		keySet().forEach((k)->{
			V v=getReal(k);
			if(v instanceof LoaderValue){
				keys.add(k);
				loaders.add((LoaderValue)v);
			}
		});
		keys.forEach((k)->remove(k));
		return loaders;
	}
}
