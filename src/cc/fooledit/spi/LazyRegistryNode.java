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
import java.util.function.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LazyRegistryNode<T> extends SimpleRegistryNode<T>{
	private final Function<String,T> supplier;
	public LazyRegistryNode(String name,RegistryNode parent,Function<String,T> supplier){
		super(name,parent);
		this.supplier=supplier;
	}
	@Override
	public T getChild(String name){
		if(hasChild(name)){
			return super.getChild(name);
		}else{
			T value=supplier.apply(name);
			addChild(name,value);
			return value;
		}
	}
}
