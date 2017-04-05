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
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PersistenceStatusManager{
	private static final Map<String,Object> objects=new HashMap<>();
	private static final Map<String,StringConverter> covertors=new HashMap<>();
	public static boolean tryLoad(String key){
		warnExists(key);
		return load(key);
	}
	public static boolean ensureLoaded(String key){
		return objects.containsKey(key)||load(key);
	}
	public static boolean load(String key){
		try{
			objects.put(key,getConvertor(key).fromString(Helper.readText(getFile(key))));
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
	public static void registerConvertor(String key,StringConverter converter){
		covertors.put(key,converter);
	}
	private static final StringConverter DEFAULT_CONVERTER=new StringConverter() {
		@Override
		public String toString(Object t){
			return toJSONStuff(t).toString();
		}
		@Override
		public Object fromString(String string){
			try{
				return fromJSONStuff(JSONParser.parse(string));
			}catch(IOException|SyntaxException ex){
				throw new RuntimeException(ex);
			}
		}
	};
	private static StringConverter getConvertor(String key){
		return covertors.getOrDefault(key,DEFAULT_CONVERTER);
	}
	public static void sync(){
		objects.entrySet().stream().forEach((e)->{
			try{
				Helper.writeText(getConvertor(e.getKey()).toString(e.getValue()),getFile(e.getKey()));
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		});
	}
	private static void warnExists(String key){
		if(objects.containsKey(key))
			Logger.getGlobal().info(key+" is going to be replaced");
	}
	private static final File DIRECTORY=new File(Main.getPath(),"status");
	private static File getFile(String key){
		File f=new File(DIRECTORY,key);
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
					collect(Collectors.toMap((e)->toJSONStuff(e.getKey()),(e)->toJSONStuff(e.getValue()))));
		}else if(obj==null){
			return JSONNull.INSTANCE;
		}else
			throw new RuntimeException();
	}
	private static Object fromJSONStuff(JSONStuff json){
		if(json instanceof JSONString){
			return ((JSONString)json).getValue();
		}else if(json instanceof JSONBoolean){
			return ((JSONBoolean)json).getValue();
		}else if(json instanceof JSONNumber){
			return ((JSONNumber)json).getValue();
		}else if(json instanceof JSONArray){
			return ((JSONArray)json).getElements().stream().map(PersistenceStatusManager::fromJSONStuff).collect(Collectors.toList());
		}else if(json instanceof JSONObject){
			return ((JSONObject)json).getMembers().entrySet().stream().
					collect(Collectors.toMap((e)->fromJSONStuff(e.getKey()),(e)->fromJSONStuff(e.getValue())));
		}else if(json instanceof JSONNull){
			return null;
		}else
			throw new RuntimeException();
	}
	static{
		EventManager.addEventListener(EventManager.SHUTDOWN,()->sync());
	}
}
