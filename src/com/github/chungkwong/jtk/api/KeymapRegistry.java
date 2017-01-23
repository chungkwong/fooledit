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
package com.github.chungkwong.jtk.api;
import java.io.*;
import java.util.logging.*;
import java.util.prefs.*;
import javafx.scene.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class KeymapRegistry{
	private final Node node;
	private final Preferences pref=Preferences.userNodeForPackage(KeymapRegistry.class).node("keymap");
	public KeymapRegistry(Node node){
		this.node=node;
		try{
			Preferences.importPreferences(MenuRegistry.class.getResourceAsStream("/com/github/chungkwong/jtk/api/default_keymap.xml"));
		}catch(IOException|InvalidPreferencesFormatException ex){
			Logger.getGlobal().log(Level.SEVERE,ex.getLocalizedMessage(),ex);
		}
		node.setOnKeyTyped((e)->{
			
		});
	}


}
