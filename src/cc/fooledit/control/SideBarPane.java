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
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SideBarPane extends SplitPane{
	private final DraggableTabPane top=new DraggableTabPane();
	private final DraggableTabPane bottom=new DraggableTabPane();
	private final DraggableTabPane left=new DraggableTabPane();
	private final DraggableTabPane right=new DraggableTabPane();
	private final SplitPane middle=new SplitPane();
	private final Property<Node> center;
	public SideBarPane(Node center){
		top.setSide(Side.TOP);
		bottom.setSide(Side.BOTTOM);
		left.setSide(Side.LEFT);
		right.setSide(Side.RIGHT);
		top.setTag(center);
		bottom.setTag(center);
		left.setTag(center);
		right.setTag(center);
		middle.setOrientation(Orientation.HORIZONTAL);
		middle.getItems().setAll(left,center,right);
		setOrientation(Orientation.VERTICAL);
		getItems().setAll(top,middle,bottom);
		top.getTabs().addListener((ListChangeListener.Change<? extends Tab> c)->{
			if(c.getList().isEmpty())
				setDividerPosition(0,0.0);
		});
		bottom.getTabs().addListener((ListChangeListener.Change<? extends Tab> c)->{
			if(c.getList().isEmpty())
				setDividerPosition(1,1.0);
		});
		left.getTabs().addListener((ListChangeListener.Change<? extends Tab> c)->{
			if(c.getList().isEmpty())
				middle.setDividerPosition(0,0.0);
		});
		right.getTabs().addListener((ListChangeListener.Change<? extends Tab> c)->{
			if(c.getList().isEmpty())
				middle.setDividerPosition(1,1.0);
		});
		this.center=new SimpleObjectProperty<Node>(center);
		this.center.addListener((e,o,n)->middle.getItems().set(1,n));
		setDividerPositions(0,1.0);
		middle.setDividerPositions(0,1.0);
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
	public void showToolBox(ToolBox box,Side side){
		Tab tab=new Tab(box.getDisplayName(),box.createInstance());
		tab.setGraphic(box.getGraphic());
		TabPane sideBar=getSideBar(side);
		sideBar.getTabs().add(tab);
		if(sideBar.getTabs().size()==0){
			switch(side){
				case LEFT:
					middle.setDividerPosition(0,sideBar.getPrefWidth()/(getWidth()+1));
					break;
				case RIGHT:
					middle.setDividerPosition(1,sideBar.getPrefWidth()/(getWidth()+1));
					break;
				case TOP:
					setDividerPosition(0,sideBar.getPrefHeight()/(getHeight()+1));
					break;
				case BOTTOM:
					setDividerPosition(1,sideBar.getPrefHeight()/(getHeight()+1));
					break;
			}
		}
	}
	public Property<Node> centerProperty(){
		return center;
	}
	@Override
	public void requestFocus(){
		center.getValue().requestFocus();
	}
}
