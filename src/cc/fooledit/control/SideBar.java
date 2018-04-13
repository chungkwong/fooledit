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
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SideBar extends SplitPane{
	private final TabPane header=new TabPane();
	private final Side side;
	public SideBar(Side side){
		this.side=side;
		switch(side){
			case TOP:
				setOrientation(Orientation.VERTICAL);
				header.setSide(Side.TOP);
				break;
			case RIGHT:
				setOrientation(Orientation.HORIZONTAL);
				header.setSide(Side.RIGHT);
				break;
			case BOTTOM:
				setOrientation(Orientation.VERTICAL);
				header.setSide(Side.BOTTOM);
				break;
			case LEFT:
				setOrientation(Orientation.HORIZONTAL);
				header.setSide(Side.LEFT);
				break;
		}
		getItems().add(header);
	}
	public void addItem(String title,Node graphic,Node content){
		Tab tab=new Tab(title,content);
		tab.setGraphic(content);
		header.getTabs().add(tab);
	}
	public int getItemsCount(){
		return header.getTabs().size();
	}
}
