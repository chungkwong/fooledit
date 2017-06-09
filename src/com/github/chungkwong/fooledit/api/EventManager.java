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
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class EventManager{
	public static final String SHUTDOWN="shutdown";
	private static final Map<String,LinkedList<Runnable>> table=new HashMap<>();
	public static void addEventListener(String event,Runnable action){
		LinkedList<Runnable> list=table.get(event);
		if(list==null){
			list=new LinkedList<>();
			table.put(event,list);
		}
		list.addFirst(action);
	}
	public static void removeEventListener(String event,Runnable action){
		LinkedList<Runnable> list=table.get(event);
		if(list!=null){
			list.remove(action);
			if(list.isEmpty())
				table.remove(event);
		}
	}
	public static void fire(String event){
		LinkedList<Runnable> list=table.get(event);
		if(list!=null){
			try{
				for(Runnable action:list)
					action.run();
			}catch(BreakException ex){
				Logger.getGlobal().log(Level.FINEST,null,ex);
			}
		}
	}
	public static class BreakException extends RuntimeException{
		public BreakException(){
		}
		public BreakException(String message){
			super(message);
		}
		public BreakException(Throwable cause){
			super(cause);
		}
		public BreakException(String message,Throwable cause){
			super(message,cause);
		}
	}
	static{
		Runtime.getRuntime().addShutdownHook(new Thread(()->fire(SHUTDOWN)));
	}
}
