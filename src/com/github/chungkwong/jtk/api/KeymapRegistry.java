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
import com.github.chungkwong.jtk.model.*;
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
	boolean ignore=false;
	public KeymapRegistry(JSONObject json,Node node,Main main){
		this.node=node;
		this.main=main;
		map.putAll((Map<String,String>)JSONConvertor.fromJSONStuff(json));
		node.addEventFilter(KeyEvent.ANY,(KeyEvent e)->{
			if(e.getEventType().equals(KeyEvent.KEY_TYPED)){
				if(ignore){
					ignore=false;
					e.consume();
				}
			}else if(e.getEventType().equals(KeyEvent.KEY_PRESSED)){
				if(e.getCode().isModifierKey())
					return;
				String code=curr==null?encode(e):curr+' '+encode(e);
				String next=map.ceilingKey(code);
				if(code.equals(next)){
					e.consume();
					curr=null;
					Command command=main.getCommandRegistry().get(map.get(code));
					main.getNotifier().notify(MessageRegistry.getString("EXECUTING")+command.getDisplayName());
					command.accept(main);
					main.getNotifier().notify(MessageRegistry.getString("EXECUTED")+command.getDisplayName());
					ignore=true;
				}else if(next!=null&&next.startsWith(code+' ')){
					e.consume();
					curr=code;
					main.getNotifier().notify(MessageRegistry.getString("ENTERED")+code);
					ignore=true;
				}else{
					curr=null;
					main.getNotifier().notify("");
					ignore=false;
				}
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
