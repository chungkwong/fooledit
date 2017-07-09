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
package com.github.chungkwong.fooledit.api;
import java.util.*;
import javafx.scene.input.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class KeymapRegistry{
	private final TreeMap<String,String> map=new TreeMap<>();
	private static final StringBuilder buf=new StringBuilder();
	public void registerKey(String key,String command){
		map.put(key,command);
	}
	public void registerKeys(Map<String,String> keys){
		map.putAll(keys);
	}
	public Map.Entry<String,String> ceilingEntry(String keycode){
		return map.ceilingEntry(keycode);
	}
	public static String encode(KeyEvent evt){
		buf.setLength(0);
		if(evt.isControlDown()||evt.isShortcutDown())
			buf.append("C-");
		if(evt.isAltDown())
			buf.append("M-");
		if(evt.isShiftDown())
			buf.append("S-");
		buf.append(evt.getCode().getName());
		return buf.toString();
	}
	@Override
	public String toString(){
		return map.toString();
	}
}
