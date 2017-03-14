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
import com.github.chungkwong.jtk.setting.Group;
import java.util.*;
import javafx.scene.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Group implements Setting{
	private final String shortDescription,longDescription;
	private final List<Setting> items;
	public Group(String shortDescription,String longDescription,List<Setting> items){
		this.shortDescription=shortDescription;
		this.longDescription=longDescription;
		this.items=items;
	}
	public List<Setting> getSubItems(){
		return items;
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
		return "group";
	}
	public static class EditorFactory implements SettingEditorFactory<Group>{
		@Override
		public Node getEditor(Group group){
			return new VBox(group.items.stream().map((setting)->Settings.getEditorFactory(setting.getType()).getEditor(setting)).toArray(Node[]::new));
		}
	}
}