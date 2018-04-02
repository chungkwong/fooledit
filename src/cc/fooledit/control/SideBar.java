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
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SideBar extends BorderPane{
	private final Pane header;
	private final ToggleGroup icons=new ToggleGroup();
	private final Side side;
	public SideBar(Side side){
		this.side=side;
		switch(side){
			case TOP:
				header=new HBox();
				setTop(header);
				break;
			case RIGHT:
				header=new VBox();
				setRight(header);
				break;
			case BOTTOM:
				header=new HBox();
				setBottom(header);
				break;
			case LEFT:
				header=new VBox();
				setLeft(header);
				break;
			default:
				header=null;
		}
	}
	public void addItem(String title,Node graphic,Node content){
		ToggleButton icon=new ToggleButton(title,graphic);
		icon.setOnMouseEntered((e)->{
			if(!icon.isSelected()){
				setCenter(content);
			}
		});
		icon.setOnMouseExited((e)->{
			if(!icon.isSelected()){
				setCenter(icons.getSelectedToggle()==null?null:(Node)icons.getSelectedToggle().getUserData());
			}
		});
		if(side==Side.LEFT)
			icon.setRotate(-90);
		else if(side==Side.RIGHT)
			icon.setRotate(90);
		icon.setUserData(content);
		icons.selectedToggleProperty().addListener((e,o,n)->setCenter(n==null?null:(Node)n.getUserData()));
		icons.getToggles().add(icon);
		header.getChildren().add(icon);
	}
	public int getItemsCount(){
		return icons.getToggles().size();
	}
	public enum Side{
		TOP,RIGHT,BOTTOM,LEFT
	}
}
