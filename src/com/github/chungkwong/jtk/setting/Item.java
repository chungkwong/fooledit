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
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Item<T> implements Setting{
	private final String shortDescription,longDescription,type;
	private T value;
	public Item(String shortDescription,String longDescription,String type){
		this.shortDescription=shortDescription;
		this.longDescription=longDescription;
		this.type=type;
	}
	public Item(String shortDescription,String longDescription,String type,T value){
		this.shortDescription=shortDescription;
		this.longDescription=longDescription;
		this.type=type;
		this.value=value;
	}
	@Override
	public String getShortDescription(){
		return shortDescription;
	}
	@Override
	public String getLongDescription(){
		return longDescription;
	}
	@Override
	public String getType(){
		return type;
	}
	public T getValue(){
		return value;
	}
	public void setValue(T value){
		this.value=value;
	}
	public static class StringEditorFactory implements SettingEditorFactory<Item<String>>{
		@Override
		public Node getEditor(Item<String> setting){
			TextArea area=new TextArea();
			area.textProperty().bindBidirectional(new SimpleStringProperty(setting,"Value"));
			return new HBox(new Label(setting.shortDescription),area,new Label(setting.longDescription));
		}
	}
}
