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
package cc.fooledit.vcs.git;
import cc.fooledit.control.*;
import java.util.*;
import java.util.function.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LazySimpleTreeItem<T> extends LazyTreeItem<T> implements NavigationTreeItem{
	private final MenuItem[] menuItems;
	public LazySimpleTreeItem(Supplier<Collection<TreeItem<T>>> supplier,T value){
		this(supplier,value,new MenuItem[0]);
	}
	public LazySimpleTreeItem(Supplier<Collection<TreeItem<T>>> supplier,T value,MenuItem[] menuItems){
		super(supplier,value);
		this.menuItems=menuItems;
	}
	@Override
	public MenuItem[] getContextMenuItems(){
		return menuItems;
	}
	@Override
	public String toString(){
		return Objects.toString(getValue());
	}
}
