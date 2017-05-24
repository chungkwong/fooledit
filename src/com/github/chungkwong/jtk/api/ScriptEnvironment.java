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
import com.github.chungkwong.jschememin.lib.*;
import com.github.chungkwong.jschememin.type.*;
import com.github.chungkwong.jtk.*;
import com.github.chungkwong.jtk.model.*;
import com.github.chungkwong.jtk.util.*;
import java.util.*;
import java.util.stream.*;
import javax.script.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ScriptEnvironment implements Bindings{
	private final Main main;
	private final CommandRegistry commands;
	private final HashMap<String,Object> bindings=new HashMap<>();
	public ScriptEnvironment(Main main){
		this.main=main;
		this.commands=main.getCommandRegistry();
	}
	@Override
	public Object put(String name,Object value){
		return bindings.put(name,value);
	}
	@Override
	public void putAll(Map<? extends String,? extends Object> toMerge){
		bindings.putAll(toMerge);
	}
	@Override
	public boolean containsKey(Object key){
		return bindings.containsKey(key)||commands.containsKey((String)key);
	}
	@Override
	public Object get(Object key){
		return bindings.containsKey(key)?bindings.get(key):pack(commands.get((String)key));
	}
	@Override
	public Object remove(Object key){
		return bindings.remove(key);
	}
	@Override
	public int size(){
		return bindings.size()+commands.size();
	}
	@Override
	public boolean isEmpty(){
		return bindings.isEmpty()&&commands.isEmpty();
	}
	@Override
	public boolean containsValue(Object value){
		return bindings.containsValue(value)||commands.values().stream().map(this::pack).anyMatch((o)->o.equals(value));
	}
	@Override
	public void clear(){
		bindings.clear();
	}
	@Override
	public Set<String> keySet(){
		return new BiSet<>(bindings.keySet(),commands.keySet());
	}
	@Override
	public Collection<Object> values(){
		return new BiSet<>(bindings.values(),commands.values().stream().map(this::pack).collect(Collectors.toSet()));
	}
	@Override
	public Set<Entry<String,Object>> entrySet(){
		return new BiSet<>(bindings.entrySet(),commands.entrySet().stream().collect(Collectors.toMap((e)->e.getKey(),(e)->pack(e.getValue()))).entrySet());
	}
	private ScmObject pack(Command command){
		return new NativeEvaluable((o)->{
			command.accept(ScmNil.NIL);
			return ScmNil.NIL;
		});
	}
}