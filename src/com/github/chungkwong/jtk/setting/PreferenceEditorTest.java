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
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PreferenceEditorTest extends Application{
	@Override
	public void start(Stage stage) throws Exception{
		stage.setScene(new Scene(new PreferenceEditor()));
		stage.show();
	}
	public static void main(String[] args){
		SettingManager.getOrCreate("com");
		SettingManager.getOrCreate("com.github.chungkwong");
		SettingManager.getOrCreate("xyz.hh");
		launch(args);
	}
}
