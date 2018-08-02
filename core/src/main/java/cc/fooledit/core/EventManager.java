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
package cc.fooledit.core;
import cc.fooledit.spi.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class EventManager{
	public static final String SHUTDOWN="shutdown";
	public static final String SHOWN="shown";
	public static void addEventListener(String event,Consumer<Object> action){
		MultiRegistryNode.addChildElement(event,action,CoreModule.EVENT_REGISTRY);
	}
	public static void removeEventListener(String event,Consumer<Object> action){
		MultiRegistryNode.removeChildElement(event,action,CoreModule.EVENT_REGISTRY);
	}
	public static void fire(String event,Object parameter){
		List<Consumer> list=MultiRegistryNode.getChildElements(event,CoreModule.EVENT_REGISTRY);
		if(list!=null){
			for(Consumer<Object> action:list){
				try{
					action.accept(parameter);
				}catch(BreakException ex){
					Logger.getGlobal().log(Level.FINEST,null,ex);
					break;
				}catch(Exception ex){
					Logger.getGlobal().log(Level.INFO,null,ex);
				}
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
		Runtime.getRuntime().addShutdownHook(new Thread(()->fire(SHUTDOWN,null)));
	}
}
