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
public class AliasRegistryNode<K,V,T> extends RegistryNode<K,V,T>{
	private final RegistryNode<K,V,T> target;
	public AliasRegistryNode(RegistryNode<K,V,T> target){
		this.target=target instanceof AliasRegistryNode?((AliasRegistryNode<K,V,T>)target).target:target;
	}
	@Override
	public V getChildReal(K name){
		return target.getChild(name);
	}
	@Override
	public Collection<K> getChildNames(){
		return target.getChildNames();
	}
	@Override
	public boolean hasChildReal(K name){
		return target.hasChild(name);
	}
	@Override
	protected V addChildReal(K name,V value){
		return target.addChild(name,value);
	}
	@Override
	protected V removeChildReal(K name){
		return target.removeChild(name);
	}
}
