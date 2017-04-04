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
package com.github.chungkwong.jtk.setting;
import com.github.chungkwong.json.*;
import com.github.chungkwong.jtk.*;
import com.github.chungkwong.jtk.api.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javax.xml.bind.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PersistenceStatusManager{
	private static final Map<String,Object> objects=new HashMap<>();
	public static boolean tryLoad(String key,Class<?> cls){
		warnExists(key);
		return load(key,cls);
	}
	public static boolean ensureLoaded(String key,Class<?> cls){
		return objects.containsKey(key)||load(key,cls);
	}
	public static boolean load(String key,Class<?> cls){
		try{
			objects.put(key,JAXB.unmarshal(getFile(key),cls));
			return true;
		}catch(Exception ex){
			return false;
		}
	}
	public static boolean containsKey(String key){
		return objects.containsKey(key);
	}
	public static Object get(String key){
		return objects.get(key);
	}
	public static Object put(String key,Object obj){
		warnExists(key);
		return objects.put(key,obj);
	}
	public static void sync(){
		objects.entrySet().stream().forEach((e)->JAXB.marshal(e.getValue(),getFile(e.getKey())));
	}
	private static void warnExists(String key){
		if(objects.containsKey(key))
			Logger.getGlobal().info(key+" is going to be replaced");
	}
	private static File getFile(String key){
		File f=new File(new File(Main.getPath(),"status"),key);
		f.getParentFile().mkdirs();
		return f;
	}
	private static JSONStuff toJSONStuff(Object obj){
		if(obj instanceof String){
			return new JSONString((String)obj);
		}else if(obj instanceof Boolean){
			return ((Boolean)obj)?JSONBoolean.TRUE:JSONBoolean.FALSE;
		}else if(obj instanceof Number){
			return new JSONNumber((Number)obj);
		}else if(obj instanceof List){
			return new JSONArray(((List<JSONStuff>)obj).stream().map(PersistenceStatusManager::toJSONStuff).collect(Collectors.toList()));
		}else if(obj instanceof Map){
			return new JSONObject(((Map<JSONStuff,JSONStuff>)obj).entrySet().stream().
					collect(Collectors.toMap(PersistenceStatusManager::toJSONStuff,PersistenceStatusManager::toJSONStuff)));
		}else if(obj==null){
			return JSONNull.INSTANCE;
		}else
			throw new RuntimeException();
	}

	static{
		EventManager.addEventListener(EventManager.SHUTDOWN,()->sync());
	}
}
