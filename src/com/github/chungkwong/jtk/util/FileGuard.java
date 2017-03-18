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
package com.github.chungkwong.jtk.util;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FileGuard{
	private static WatchService SERVICE;
	private static Map<Pair<Watchable,Path>,Consumer<WatchEvent.Kind>> actions=new HashMap<>();
	public static void register(Path path,Consumer<WatchEvent.Kind> onChange){
		try{
			path.getParent().register(SERVICE,StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE,StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.OVERFLOW);
			actions.put(new Pair<>(path.getParent(),path.getFileName()),onChange);
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	public static void unRegister(Path path,Consumer<WatchEvent.Kind> onChange){
		actions.remove(path,onChange);//FIXME may cause leak of resource
	}
	static{
		try{
			SERVICE=FileSystems.getDefault().newWatchService();
			Thread t=new Thread(()->{
				while(true){
					try{
						WatchKey key=SERVICE.take();
						for(WatchEvent<?> event:key.pollEvents()){
							if(event.kind().equals(StandardWatchEventKinds.OVERFLOW)){
								actions.values().forEach((action)->action.accept(StandardWatchEventKinds.OVERFLOW));
							}else{
								Pair<Watchable,Object> pair=new Pair<>(key.watchable(),event.context());
								if(actions.containsKey(pair)){
									actions.get(pair).accept(event.kind());
								}
							}
						}
						key.reset();
					}catch(InterruptedException ex){
						Logger.getGlobal().log(Level.SEVERE,null,ex);
						break;//FIXME
					}
				}
			});
			t.setDaemon(true);
			t.start();
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			SERVICE=null;
		}
	}
	public static void main(String[] args) throws InterruptedException{
		register(new File("/home/kwong/dead").toPath(),(k)->System.out.println("hello"));
		System.out.println(actions);
		Thread.sleep(100000);
	}
}
