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
import java.util.function.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LazyRegistryNode<K,V,T> extends SimpleRegistryNode<K,V,T>{
	private Function<K,V> supplier;
	private Collection<K> keys;
	private final boolean cache;
	public LazyRegistryNode(Function<K,V> supplier,Collection<K> keys,boolean cache){
		this.supplier=supplier;
		this.keys=keys;
		this.cache=cache;
	}
	@Override
	public V getChildReal(K name){
		if(super.hasChildReal(name)){
			return super.getChildReal(name);
		}else if(supplier!=null){
			V value=supplier.apply(name);
			if(cache)
				addChild(name,value);
			return value;
		}else{
			return null;
		}
	}
	@Override
	public boolean hasChildReal(K name){
		return  super.hasChildReal(name)||keys.contains(name);
	}
	public void setKeys(Collection<K> keys){
		this.keys=keys;
	}
	public void setSupplier(Function<K,V> supplier){
		this.supplier=supplier;
	}
	@Override
	protected Collection<K> getChildNamesReal(){
		Set<K> childs=new HashSet<>(super.getChildNamesReal());
		childs.addAll(keys);
		return childs;
	}
}