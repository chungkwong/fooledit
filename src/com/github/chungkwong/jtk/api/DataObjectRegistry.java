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
public class DataObjectRegistry{
	private static final String MIME="MIME";
	private static final String URI="URI";
	private final TreeMap<String,DataObject> objects=new TreeMap<>();
	private final IdentityHashMap<DataObject,Map<Object,Object>> properties=new IdentityHashMap<>();
	public DataObject getDataObject(String name){
		return objects.get(name);
	}
	public Object getProperties(Object key,DataObject data){
		return properties.get(data).get(key);
	}
	public String getURL(DataObject data){
		return (String)properties.get(data).get(URI);
	}
	public String getMIME(DataObject data){
		return (String)properties.get(data).get(MIME);
	}
	public void addDataObject(String name,DataObject data,Map<Object,Object> prop){
		objects.put(name,data);
		properties.put(data,prop);
	}
	public static Map<Object,Object> createProperties(String uri,String mime){
		HashMap<Object,Object> prop=new HashMap<Object,Object>();
		prop.put(MIME,mime);
		prop.put(URI,uri);
		return prop;
	}
}