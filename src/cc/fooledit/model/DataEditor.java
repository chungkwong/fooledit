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
package cc.fooledit.model;
import cc.fooledit.api.*;
import javafx.scene.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public interface DataEditor<T extends DataObject>{
	Node edit(T data);
	default Node edit(T data,Object remark){
		return edit(data);
	}
	default CommandRegistry getCommandRegistry(){
		return new CommandRegistry();
	}
	default KeymapRegistry getKeymapRegistry(){
		return new KeymapRegistry();
	}
	default MenuRegistry getMenuRegistry(){
		return new MenuRegistry();
	}
	default Object getRemark(Node node){
		return null;
	}
	String getName();
}