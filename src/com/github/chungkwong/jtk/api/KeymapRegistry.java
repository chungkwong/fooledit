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
import com.github.chungkwong.json.*;
import com.github.chungkwong.jtk.*;
import java.util.*;
import javafx.scene.*;
import javafx.scene.input.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class KeymapRegistry{
	private final Node node;
	private final Main main;
	private final TreeMap<String,String> map=new TreeMap<>();
	private String curr=null;
	private static final StringBuilder buf=new StringBuilder();
	public KeymapRegistry(JSONObject json,Node node,Main main){
		this.node=node;
		this.main=main;
		map.putAll((Map<String,String>)JSONConvertor.fromJSONStuff(json));
		node.addEventFilter(KeyEvent.KEY_PRESSED,(KeyEvent e)->{
			String code=curr==null?encode(e):curr+' '+encode(e);
			String next=map.ceilingKey(code);
			if(code.equals(next)){
				String command=map.get(code);
				main.getNotifier().notify(MessageRegistry.getString("EXECUTING")+command);
				main.getCommandRegistry().get(command).run();
				main.getNotifier().notify(MessageRegistry.getString("EXECUTED")+command);
				e.consume();
			}else if(next!=null&&next.startsWith(code)){
				curr=code;
				main.getNotifier().notify(MessageRegistry.getString("ENTERED")+code);
				e.consume();
			}else{
				curr=null;
				main.getNotifier().notify("");
			}
		});
	}
	private static String encode(KeyEvent evt){
		buf.setLength(0);
		if(evt.isControlDown())
			buf.append("C-");
		if(evt.isMetaDown())
			buf.append("M-");
		if(evt.isShiftDown())
			buf.append("S-");
		buf.append(evt.getCode().getName());
		return buf.toString();
	}
}
