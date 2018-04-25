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
	private final TabPane top=new DraggableTabPane();
	private final TabPane bottom=new DraggableTabPane();
	private final TabPane left=new DraggableTabPane();
	private final TabPane right=new DraggableTabPane();
	private final SplitPane middle=new SplitPane();
	private final Property<Node> center;
	public SideBarPane(Node center){
		top.setSide(Side.TOP);
		bottom.setSide(Side.BOTTOM);
		left.setSide(Side.LEFT);
		right.setSide(Side.RIGHT);
		middle.setOrientation(Orientation.HORIZONTAL);
		middle.getItems().setAll(left,center,right);
		setOrientation(Orientation.VERTICAL);
		getItems().setAll(top,middle,bottom);
		this.center=new SimpleObjectProperty<Node>(center);
		this.center.addListener((e,o,n)->middle.getItems().set(1,n));
		updateDivider();
	}
	public TabPane getSideBar(Side side){
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
			TabPane bar=getSideBar(side);
			if(bar.getTabs().size()==0){
				showToolBox(box,side);
				return;
			}
		}
		showToolBox(box,perfered.length>0?perfered[0]:Side.RIGHT);
	}
	void updateDivider(){
		double width=getWidth();
		double leftWidth=left.prefWidth(width);
		double rightWidth=right.prefWidth(width);
		double hd0=leftWidth/(width+2);
		double hd1=1-rightWidth/(width+2);
		middle.setDividerPositions(hd0,hd1);
		double height=getHeight();
		double topHeight=top.prefHeight(height);
		double bottomHeight=bottom.prefHeight(height);
		double vd0=topHeight/(height+2);
		double vd1=1-bottomHeight/(height+2);
		setDividerPositions(vd0,vd1);
	}
	public void showToolBox(ToolBox box,Side side){
		Tab tab=new Tab(box.getDisplayName(),box.createInstance());
		tab.setGraphic(box.getGraphic());
		getSideBar(side).getTabs().add(tab);
		updateDivider();
	}
	public Property<Node> centerProperty(){
		return center;
	}
}
