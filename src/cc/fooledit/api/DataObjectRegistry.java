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
package cc.fooledit.api;
import cc.fooledit.model.*;
import cc.fooledit.setting.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DataObjectRegistry{
	public static final String MIME="MIME";
	public static final String URI="URI";
	public static final String DEFAULT_NAME="DEFAULT_NAME";
	public static final String BUFFER_NAME="BUFFER_NAME";
	public static final String TYPE="TYPE";
	private static final String UNTITLED=MessageRegistry.getString("UNTITLED");
	private static final String KEY="file_history.json";
	private static final String LIMIT="limit";
	private static final String ENTRIES="entries";
	private static final TreeMap<String,DataObject> objects=new TreeMap<>();
	private static final IdentityHashMap<DataObject,Map<String,String>> properties=new IdentityHashMap<>();
	private static final Map<String,Object> HISTORY=(Map<String,Object>)PersistenceStatusManager.USER.
			getOrDefault(KEY,()->Helper.hashMap(LIMIT,20,ENTRIES,new LinkedList<>()));
	public static DataObject getDataObject(String name){
		return objects.get(name);
	}
	public static Set<String> getDataObjectNames(){
		return objects.keySet();
	}
	public static String getProperty(String key,DataObject data){
		return properties.get(data).get(key);
	}
	public static Map<String,String> getProperties(DataObject data){
		return properties.get(data);
	}
	public static String getURL(DataObject data){
		return (String)properties.get(data).get(URI);
	}
	public static String getMIME(DataObject data){
		return (String)properties.get(data).get(MIME);
	}
	public static String getName(DataObject data){
		return (String)properties.get(data).get(BUFFER_NAME);
	}
	public static DataObject get(Object json){
		Map<String,String> prop=(Map<String,String>)json;
		String type=prop.get(TYPE);
		DataObjectType builder=DataObjectTypeRegistry.getDataObjectTypes().stream().filter((t)->t.getClass().getName().equals(type)).findFirst().get();
		DataObject object;
		if(prop.containsKey(URI)){
			String uri=prop.get(URI);
			Optional<DataObject> old=objects.values().stream().filter((o)->uri.equals(getURL(o))).findAny();
			if(old.isPresent())
				return old.get();
			try{
				object=builder.readFrom(new URL(uri).openConnection().getInputStream());
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				object=builder.create();
			}
		}else{
			object=builder.create();
		}
		addDataObject(object,prop);
		return object;
	}
	public static void addDataObject(DataObject data,Map<String,String> prop){
		if(properties.containsKey(data))
			return;
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
		addHistoryEntry(prop);
	}
	public static void removeDataObject(DataObject data){
		objects.remove(getName(data));
		properties.remove(data);
	}
	private static void addHistoryEntry(Map<String,String> prop){
		if(prop.containsKey(URI)){
			String uri=prop.get(URI);
			List<Map<String,String>> list=getHistoryList();
			Iterator<Map<String,String>> iter=list.iterator();
			while(iter.hasNext()){
				if(uri.equals(iter.next().get(URI))){
					iter.remove();
				}
			}
			Helper.addEntry(prop,getHistoryList(),getHistoryLimit());
		}
	}
	public static int getHistoryLimit(){
		return ((Number)HISTORY.get(LIMIT)).intValue();
	}
	public static List<Map<String,String>> getHistoryList(){
		return (List)HISTORY.get(ENTRIES);
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
	public static Map<String,String> createProperties(String name,String uri,String mime,String type){
		HashMap<String,String> prop=new HashMap<>();
		prop.put(DEFAULT_NAME,name);
		prop.put(MIME,mime);
		prop.put(URI,uri);
		prop.put(TYPE,type);
		return prop;
	}
}