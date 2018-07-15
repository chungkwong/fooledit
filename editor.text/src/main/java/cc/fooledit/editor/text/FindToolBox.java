/*
 * Copyright (C) 2018 Chan Chung Kwong
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
package cc.fooledit.editor.text;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class FindToolBox implements ToolBox{
	public static final FindToolBox INSTANCE=new FindToolBox();
	private FindToolBox(){
	}
	@Override
	public String getName(){
		return "FIND";
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("FIND",TextEditorModule.NAME);
	}
	@Override
	public Node createInstance(Node viewer,Object remark,RegistryNode<String,Object> meta){
		BorderPane bar=new BorderPane();
		TextField findString=new TextField();
		return bar;
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public Side[] getPerferedSides(){
		return new Side[]{Side.BOTTOM,Side.TOP};
	}
}
