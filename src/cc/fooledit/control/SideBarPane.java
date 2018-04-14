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
package cc.fooledit.control;
import cc.fooledit.core.*;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SideBarPane extends SplitPane{
	private final SideBar top=new SideBar(Side.TOP);
	private final SideBar bottom=new SideBar(Side.BOTTOM);
	private final SideBar left=new SideBar(Side.LEFT);
	private final SideBar right=new SideBar(Side.RIGHT);
	private final SplitPane middle=new SplitPane();
	private final Property<Node> center;
	public SideBarPane(Node center){
		middle.setOrientation(Orientation.HORIZONTAL);
		middle.getItems().setAll(left,center,right);
		middle.setDividerPositions(0.2,0.8);
		setOrientation(Orientation.VERTICAL);
		getItems().setAll(top,middle,bottom);
		setDividerPositions(0.2,0.8);
		this.center=new SimpleObjectProperty<Node>(center);
		this.center.addListener((e,o,n)->middle.getItems().set(1,n));
	}
	public SideBar getSideBar(Side side){
		switch(side){
			case LEFT:return left;
			case RIGHT:return right;
			case TOP:return top;
			case BOTTOM:return bottom;
			default:return null;
		}
	}
	public void showToolBox(ToolBox box){
		Side[] perfered=box.getPerferedSides();
		for(Side side:perfered){
			SideBar bar=getSideBar(side);
			if(bar.getItemsCount()==0){
				showToolBox(box,side);
				return;
			}
		}
		showToolBox(box,perfered.length>0?perfered[0]:Side.RIGHT);
	}
	public void showToolBox(ToolBox box,Side side){
		getSideBar(side).addItem(box.getDisplayName(),box.getGraphic(),box.createInstance());
	}
	public Property<Node> centerProperty(){
		return center;
	}
}
