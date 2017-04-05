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
	public static final String MIME="MIME";
	public static final String URI="URI";
	public static final String DEFAULT_NAME="DEFAULT_NAME";
	public static final String BUFFER_NAME="BUFFER_NAME";
	private static final String UNTITLED=MessageRegistry.getString("UNTITLED");
	private static final TreeMap<String,DataObject> objects=new TreeMap<>();
	private static final IdentityHashMap<DataObject,Map<Object,Object>> properties=new IdentityHashMap<>();
	public static DataObject getDataObject(String name){
		return objects.get(name);
	}
	public static Set<String> getDataObjectNames(){
		return objects.keySet();
	}
	public static Object getProperties(Object key,DataObject data){
		return properties.get(data).get(key);
	}
	public static String getURL(DataObject data){
		return (String)properties.get(data).get(URI);
	}
	public static String getMIME(DataObject data){
		return (String)properties.get(data).get(MIME);
	}
	public static void addDataObject(DataObject data,Map<Object,Object> prop){
		String name=(String)prop.getOrDefault(DEFAULT_NAME,UNTITLED);
		if(objects.containsKey(name)){
			for(int i=1;;i++){
				String tmp=name+":"+i;
				if(!objects.containsKey(tmp)){
					name=tmp;
					break;
				}
			}
		}
		prop.put(BUFFER_NAME,name);
		objects.put(name,data);
		properties.put(data,prop);
	}
	public static DataObject getNextDataObject(DataObject curr){
		Map.Entry<String,DataObject> next=objects.higherEntry((String)properties.get(curr).get(BUFFER_NAME));
		if(next==null)
			return objects.firstEntry().getValue();
		else
			return next.getValue();
	}
	public static DataObject getPreviousDataObject(DataObject curr){
		Map.Entry<String,DataObject> prev=objects.lowerEntry((String)properties.get(curr).get(BUFFER_NAME));
		if(prev==null)
			return objects.lastEntry().getValue();
		else
			return prev.getValue();
	}
	public static Map<Object,Object> createProperties(String name,String uri,String mime){
		HashMap<Object,Object> prop=new HashMap<>();
		prop.put(DEFAULT_NAME,name);
		prop.put(MIME,mime);
		prop.put(URI,uri);
		return prop;
	}
}