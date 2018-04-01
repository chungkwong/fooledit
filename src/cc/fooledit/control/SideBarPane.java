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
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SideBarPane extends BorderPane{
	private final SideBar top=new SideBar(SideBar.Side.TOP);
	private final SideBar bottom=new SideBar(SideBar.Side.BOTTOM);
	private final SideBar left=new SideBar(SideBar.Side.LEFT);
	private final SideBar right=new SideBar(SideBar.Side.RIGHT);
	public SideBarPane(){
		setLeft(left);
		setRight(right);
		setTop(top);
		setBottom(bottom);
	}
	public SideBar getLeftSideBar(){
		return left;
	}
	public SideBar getRightSideBar(){
		return right;
	}
	public SideBar getTopSideBar(){
		return top;
	}
	public SideBar getBottomSideBar(){
		return bottom;
	}
	public void showToolBox(ToolBox box){
		SideBar.Side[] perfered=box.getPerferedSides();
		for(SideBar.Side side:perfered){
			switch(side){
				case TOP:
					top.addItem(box.getDisplayName(),null,box.createInstance());
					return;
				case BOTTOM:
					bottom.addItem(box.getDisplayName(),null,box.createInstance());
					return;
				case LEFT:
					left.addItem(box.getDisplayName(),null,box.createInstance());
					return;
				case RIGHT:
					right.addItem(box.getDisplayName(),null,box.createInstance());
					return;
			}
		}
	}
}
