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
package com.github.chungkwong.jtk.control;
import java.util.*;
import javafx.scene.*;
import javafx.scene.input.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CompoundKeyMap{
	private final TreeMap<String,Runnable> map=new TreeMap<>();
	private String curr="";
	public CompoundKeyMap(){}
	public CompoundKeyMap(Node node){
		node.addEventFilter(KeyEvent.KEY_PRESSED,(e)->{
			String code=curr+encode(e);
			String next=map.ceilingKey(code);
			if(code.equals(next)){
				map.get(code).run();
				e.consume();
			}else if(next!=null&&next.startsWith(code)){
				curr=code;
				e.consume();
			}else{
				curr="";
			}
		});
	}
	public void addAction(String key,Runnable action){
		map.put(key,action);
	}
	private static String encode(KeyEvent evt){
		StringBuilder buf=new StringBuilder();
		if(evt.isShiftDown())
			buf.append("S-");
		if(evt.isMetaDown())
			buf.append("M-");
		if(evt.isControlDown())
			buf.append("C-");
		buf.append(evt.getCode().getName());
		buf.append(' ');
		return buf.toString();
	}
}
