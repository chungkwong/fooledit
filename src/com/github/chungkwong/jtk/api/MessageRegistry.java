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
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MessageRegistry{
	private static final ResourceBundle jtkBundle=ResourceBundle.getBundle("com.github.chungkwong.jtk.default.locale.base");
	private static final HashMap<String,String> dynamicBundle=new HashMap<>();
	public static void addString(String key,String value){
		dynamicBundle.put(key,value);
	}
	public static void addBundle(ResourceBundle bundle){
		for(String key:bundle.keySet())
			addString(key,bundle.getString(key));
	}
	public static String getString(String key){
		try{
			return jtkBundle.getString(key);
		}catch(MissingResourceException ex){
			if(dynamicBundle.containsKey(key))
				return dynamicBundle.get(key);
			Logger.getGlobal().log(Level.INFO,"Missing string: {0}",key);
			return key;
		}
	}
}
