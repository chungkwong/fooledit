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
import com.github.chungkwong.jschememin.type.*;
import com.github.chungkwong.jtk.model.*;
import com.github.chungkwong.jtk.util.*;
import java.util.*;
import java.util.function.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CommandRegistry{
	private final HashMap<String,Command> loaded=new HashMap<>();
	private final HashMap<String,Supplier<Command>> autoload=new HashMap<>();
	public void put(String name,Command command){
		loaded.put(name,command);
		autoload.remove(name);
	}
	public void put(String name,Runnable action){
		put(name,(t)->action.run());
		autoload.remove(name);
	}
	public void put(String name,Consumer<ScmPairOrNil> action){
		loaded.put(name,Command.create(MessageRegistry.getString(name.toUpperCase().replace('-','_')),action));
		autoload.remove(name);
	}
	public void putOnDemand(String name,Supplier<Command> supplier){
		autoload.put(name,supplier);
	}
	public boolean containsKey(String name){
		if(loaded.containsKey(name)){
			return true;
		}
		return tryLoad(name);
	}
	public Command get(String name){
		return getOrDefault(name,null);
	}
	public Command getOrDefault(String name,Command def){
		Command command=loaded.get(name);
		if(command!=null)
			return command;
		tryLoad(name);
		return loaded.getOrDefault(name,def);
	}
	public Set<String> keySet(){
		return new BiSet<>(loaded.keySet(),autoload.keySet());
	}
	public Collection<Command> values(){
		tryLoadAll();
		return loaded.values();
	}
	public Set<Map.Entry<String,Command>> entrySet(){
		tryLoadAll();
		return loaded.entrySet();
	}
	public int size(){
		return loaded.size()+autoload.size();
	}
	private boolean tryLoad(String name){
		Supplier<Command> supplier=autoload.remove(name);
		if(supplier!=null){
			loaded.put(name,supplier.get());
			return true;
		}
		return false;
	}
	private void tryLoadAll(){
		autoload.forEach((name,supp)->loaded.put(name,supp.get()));
		autoload.clear();
	}
	public boolean isEmpty(){
		return loaded.isEmpty()&&autoload.isEmpty();
	}
}
