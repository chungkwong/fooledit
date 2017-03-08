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
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CommandRegistry{
	private final HashMap<String,Command> registry=new HashMap<>();
	public Command getCommand(String name){
		return registry.get(name);
	}
	public void addCommand(String name,Command command){
		registry.put(name,command);
	}
	public void addCommand(String name,Runnable action){
		addCommand(name,Command.create(MessageRegistry.getString(name.toUpperCase()),action));
	}
	public Command removeCommand(String name){
		return registry.remove(name);
	}
	public Set<String> getCommandNames(){
		return registry.keySet();
	}
}
