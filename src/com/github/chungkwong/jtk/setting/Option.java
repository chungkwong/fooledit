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
public class Option<T>{
	private final String shortDescription,longDescription,type;
	private T value;
	public Option(String shortDescription,String longDescription,String type){
		this.shortDescription=shortDescription;
		this.longDescription=longDescription;
		this.type=type;
	}
	public Option(String shortDescription,String longDescription,String type,T value){
		this.shortDescription=shortDescription;
		this.longDescription=longDescription;
		this.type=type;
		this.value=value;
	}
	public String getShortDescription(){
		return shortDescription;
	}
	public String getLongDescription(){
		return longDescription;
	}
	public String getType(){
		return type;
	}
	public T getValue(){
		return value;
	}
	public void setValue(T value){
		this.value=value;
	}
	public static class StringEditorFactory implements SettingEditorFactory<Option<String>>{
		@Override
		public Node getEditor(Option<String> setting){
			TextArea area=new TextArea();
			area.textProperty().bindBidirectional(new SimpleStringProperty(setting,"Value"));
			return new HBox(new Label(setting.shortDescription),area,new Label(setting.longDescription));
		}
	}
}
