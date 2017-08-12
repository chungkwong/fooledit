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
package cc.fooledit.control;
import java.util.*;
import java.util.function.*;
import javafx.scene.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LazyTreeItem<T> extends TreeItem<T>{
	private final Supplier<Collection<TreeItem<T>>> supplier;
	public LazyTreeItem(T value){
		super(value);
		this.supplier=null;
	}
	public LazyTreeItem(T value,Node graphic){
		super(value,graphic);
		this.supplier=null;
	}
	public LazyTreeItem(Supplier<Collection<TreeItem<T>>> supplier,T value){
		this(supplier,value,null);
	}
	public LazyTreeItem(Supplier<Collection<TreeItem<T>>> supplier,T value,Node graphic){
		super(value,graphic);
		this.supplier=supplier;
		expandedProperty().addListener((e,o,n)->{
			if(n){
				getChildren().setAll(supplier.get());
			}else{
				getChildren().clear();
			}
		});
	}
	public void refresh(){
		getChildren().setAll(supplier.get());
	}
	@Override
	public boolean isLeaf(){
		return supplier==null;
	}
}
