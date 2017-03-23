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
package com.github.chungkwong.jtk.setting;
import java.util.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Group{
	private final String shortDescription,longDescription;
	private final List<Option> items;
	public Group(String shortDescription,String longDescription,List<Option> items){
		this.shortDescription=shortDescription;
		this.longDescription=longDescription;
		this.items=items;
	}
	public List<Option> getSubItems(){
		return items;
	}
	public String getShortDescription(){
		return shortDescription;
	}
	public String getLongDescription(){
		return longDescription;
	}
	public String getType(){
		return "group";
	}
	public Node getEditor(){
		VBox box=new VBox(new Label(getShortDescription()));
		box.getChildren().addAll(items.stream().map((setting)->Settings.getEditorFactory(setting.getType()).getEditor(setting)).toArray(Node[]::new));
		return box;
	}
}