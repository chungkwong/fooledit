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
import com.github.chungkwong.jtk.model.*;
import java.util.*;
import javax.script.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ScriptEnvironment implements Bindings{
	private final CommandRegistry commands;
	public ScriptEnvironment(){
		this.commands=null;
	}
	public ScriptEnvironment(CommandRegistry commands){
		this.commands=commands;
	}
	@Override
	public Object put(String name,Object value){
		Command old=commands.getCommand(name);
		commands.addCommand(name,(Command)value);
		return old;
	}
	@Override
	public void putAll(Map<? extends String,? extends Object> toMerge){
		toMerge.forEach((name,value)->commands.addCommand(name,(Command)value));
	}
	@Override
	public boolean containsKey(Object key){
		return commands.getCommand(key.toString())!=null;
	}
	@Override
	public Object get(Object key){
		return commands.getCommand(key.toString());
	}
	@Override
	public Object remove(Object key){
		return commands.removeCommand(key.toString());
	}
	@Override
	public int size(){
		return commands.getCommandNames().size();
	}
	@Override
	public boolean isEmpty(){
		return size()==0;
	}
	@Override
	public boolean containsValue(Object value){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void clear(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public Set<String> keySet(){
		return commands.getCommandNames();
	}
	@Override
	public Collection<Object> values(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public Set<Entry<String,Object>> entrySet(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	public static void main(String[] args){
		ScriptEngineManager scriptEngineManager=new ScriptEngineManager();
		ScriptEngine engine=scriptEngineManager.getEngineByName("nashorn");
		Bindings bindings=new SimpleBindings();
		bindings.put("x","hello");
		try{
			engine.eval("print('Hello, World!');x='world';",bindings);
		}catch(final ScriptException se){
			se.printStackTrace();
		}
		System.out.println(bindings.get("x"));
		System.err.println(bindings.entrySet());
	}
}
