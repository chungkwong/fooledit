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
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Settings extends Application{
	private static final Map<String,SettingEditorFactory> TYPES=new HashMap<>();
	private static final Group ROOT=new Group("","",new ArrayList<>());
	public static void registerEditorFactory(String cls,SettingEditorFactory factory){
		TYPES.put(cls,factory);
	}
	public static SettingEditorFactory getEditorFactory(String cls){
		return TYPES.get(cls);
	}
	public static Setting getRoot(){
		return ROOT;
	}
	static{
		TYPES.put("group",new Group.EditorFactory());
		TYPES.put("string",new Item.StringEditorFactory());
	}
	@Override
	public void start(Stage stage) throws Exception{
		Group root=new Group("a","aaa",Arrays.asList(new Item<String>("b","bbb","string"),new Item<String>("c","cccc","string")));
		stage.setScene(new Scene(new BorderPane(Settings.getEditorFactory("group").getEditor(root))));
		stage.show();
	}
	public static void main(String[] args){
		launch(args);
	}
}
