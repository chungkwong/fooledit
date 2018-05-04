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
package cc.fooledit.editor.image;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SelectionToolBox implements ToolBox{
	public static final SelectionToolBox INSTANCE=new SelectionToolBox();
	private SelectionToolBox(){

	}
	@Override
	public String getName(){
		return "SELECTION";
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("SELECTION",ImageModule.NAME);
	}
	@Override
	public Node createInstance(Node viewer,Object remark,RegistryNode<String,Object> meta){
		return createInstance((GraphicsObject)meta.get(DataObject.DATA));
	}
	Node createInstance(GraphicsObject object){
		return new Canvas();
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public Side[] getPerferedSides(){
		return new Side[]{Side.TOP,Side.BOTTOM};
	}
}