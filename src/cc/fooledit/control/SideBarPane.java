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
import cc.fooledit.spi.*;
import java.util.*;
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
		setDividerPositions(0,0.0);
		setDividerPositions(1,1.0);
		middle.setDividerPositions(0,0.0);
		middle.setDividerPositions(1,1.0);
		SplitPane.setResizableWithParent(top,false);
		SplitPane.setResizableWithParent(bottom,false);
		SplitPane.setResizableWithParent(left,false);
		SplitPane.setResizableWithParent(right,false);
	}
	public ListRegistryNode<Number> getRatios(){
		return new ListRegistryNode<>(Arrays.asList(getDividerPositions()[0],getDividerPositions()[1],
				middle.getDividerPositions()[0],middle.getDividerPositions()[1]));
	}
	public void setRatios(ListRegistryNode<Number> positions){
		EventManager.addEventListener(EventManager.SHOWN,(obj)->{
			setDividerPosition(0,positions.getOrDefault(0,0.0).doubleValue());
			setDividerPosition(1,positions.getOrDefault(1,1.0).doubleValue());
			middle.setDividerPosition(0,positions.getOrDefault(2,0.0).doubleValue());
			middle.setDividerPosition(1,positions.getOrDefault(3,1.0).doubleValue());
		});
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
	public void showToolBox(Tab tab,Side[] perfered){
		for(Side side:perfered){
			TabPane bar=getSideBar(side);
			if(bar.getTabs().size()==0){
				showToolBox(tab,side);
				return;
			}
		}
		showToolBox(tab,perfered.length>0?perfered[0]:Side.RIGHT);
	}
	public void showToolBox(Tab tab,Side side){
		TabPane sideBar=getSideBar(side);
		sideBar.getTabs().add(tab);
		if(sideBar.getTabs().size()==1){
			switch(side){
				case LEFT:
					middle.setDividerPosition(0,Helper.truncate(sideBar.getTabMinWidth()/(getWidth()+1),0,middle.getDividerPositions()[1]));
					break;
				case RIGHT:
					middle.setDividerPosition(1,Helper.truncate(1-sideBar.getTabMinWidth()/(getWidth()+1),middle.getDividerPositions()[0],1));
					break;
				case TOP:
					setDividerPosition(0,Helper.truncate(sideBar.getTabMinHeight()/(getHeight()+1),0,getDividerPositions()[1]));
					break;
				case BOTTOM:
					setDividerPosition(1,Helper.truncate(1-sideBar.getTabMinHeight()/(getHeight()+1),getDividerPositions()[0],1));
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
