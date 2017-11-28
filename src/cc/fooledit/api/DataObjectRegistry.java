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
import cc.fooledit.spi.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.activation.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DataObjectRegistry{
	private static final String UNTITLED=MessageRegistry.getString("UNTITLED",CoreModule.NAME);
	private static final String KEY="file_history.json";
	private static final String LIMIT="limit";
	private static final String ENTRIES="entries";
	private static final TreeMap<String,DataObject> objects=new TreeMap<>();
	private static final Map<String,Object> HISTORY=(Map<String,Object>)PersistenceStatusManager.USER.
			getOrDefault(KEY,()->Helper.hashMap(LIMIT,20,ENTRIES,new LinkedList<>()));
	public static DataObject getDataObject(String name){
		return objects.get(name);
	}
	public static Set<String> getDataObjectNames(){
		return objects.keySet();
	}
	public static DataObject get(Object json){
		Map<String,String> prop=(Map<String,String>)json;
		String type=prop.get(DataObject.TYPE);
		DataObjectType builder=DataObjectTypeRegistry.getDataObjectTypes().values().stream().filter((t)->t.getClass().getName().equals(type)).findFirst().get();
		DataObject object;
		if(prop.containsKey(DataObject.URI)){
			String uri=prop.get(DataObject.URI);
			Optional<DataObject> old=objects.values().stream().filter((o)->uri.equals(o.getProperties().get(DataObject.URI))).findAny();
			if(old.isPresent())
				return old.get();
			try{
				String mime=prop.get(DataObject.MIME);
				if(mime!=null){
					try{
						object=readFrom(new URL(uri),new MimeType(mime));
					}catch(Exception ex){
						object=readFrom(new URL(uri));
					}
				}else{
					object=readFrom(new URL(uri));
				}
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				object=create(builder);
			}
		}else{
			object=create(builder);
		}
		addDataObject(object);
		return object;
	}
	public static void addDataObject(DataObject data){
		if(objects.containsValue(data))
			return;
		Map<String,String> prop=data.getProperties();
		String name=(String)prop.getOrDefault(DataObject.DEFAULT_NAME,UNTITLED);
		if(objects.containsKey(name)){
			for(int i=1;;i++){
				String tmp=name+":"+i;
				if(!objects.containsKey(tmp)){
					name=tmp;
					break;
				}
			}
		}
		prop.put(DataObject.BUFFER_NAME,name);
		prop.put(DataObject.TYPE,data.getDataObjectType().getClass().getName());
		objects.put(name,data);
		addHistoryEntry(prop);
	}
	public static void removeDataObject(DataObject data){
		objects.remove((String)data.getProperties().get(DataObject.BUFFER_NAME));
	}
	private static void addHistoryEntry(Map<String,String> prop){
		if(prop.containsKey(DataObject.URI)){
			String uri=prop.get(DataObject.URI);
			List<Map<String,String>> list=getHistoryList();
			Iterator<Map<String,String>> iter=list.iterator();
			while(iter.hasNext()){
				if(uri.equals(iter.next().get(DataObject.URI))){
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
		Map.Entry<String,DataObject> next=objects.higherEntry((String)curr.getProperties().get(DataObject.BUFFER_NAME));
		if(next==null)
			return objects.firstEntry().getValue();
		else
			return next.getValue();
	}
	public static DataObject getPreviousDataObject(DataObject curr){
		Map.Entry<String,DataObject> prev=objects.lowerEntry((String)curr.getProperties().get(DataObject.BUFFER_NAME));
		if(prev==null)
			return objects.lastEntry().getValue();
		else
			return prev.getValue();
	}
	public static <T extends DataObject> T create(DataObjectType<T> type){
		T object=type.create();
		object.getProperties().put(DataObject.TYPE,type.getClass().getName());
		object.getProperties().putIfAbsent(DataObject.DEFAULT_NAME,type.getName());
		return object;
	}
	public static DataObject readFrom(URL url)throws Exception{
		FoolURLConnection connection=FoolURLConnection.open(url);
		for(String mime:ContentTypeDetectorRegistry.guess(connection)){
			try{
				return readFrom(url,new MimeType(mime));
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		}
		throw new Exception();
	}
	public static DataObject readFrom(URL url,MimeType mime)throws Exception{
		return readFrom(FoolURLConnection.open(url),mime);
	}
	public static DataObject readFrom(URLConnection connection,MimeType mime)throws Exception{
		for(DataObjectType type:DataObjectTypeRegistry.getPreferedDataObjectType(mime)){
			try{
				return readFrom(connection,type,mime);
			}catch(Exception ex){
				Logger.getGlobal().log(Level.INFO,null,ex);
			}
		}
		throw new Exception();
	}
	public static DataObject readFrom(URL url,DataObjectType type,MimeType mime)throws Exception{
		return readFrom(FoolURLConnection.open(url),type,mime);
	}
	public static DataObject readFrom(URLConnection connection,DataObjectType type,MimeType mime)throws Exception{
		DataObject data=type.readFrom(connection,mime);
		data.getProperties().put(DataObject.URI,connection.getURL().toString());
		data.getProperties().put(DataObject.MIME,mime.toString());
		data.getProperties().put(DataObject.DEFAULT_NAME,getLastComponent(connection.getURL().getPath()));
		data.getProperties().put(DataObject.TYPE,type.getClass().getName());
		addDataObject(data);
		return data;
	}
	private static String getLastComponent(String path){
		int i=path.lastIndexOf('/');
		return i==-1?path:path.substring(i+1);
	}
	public static void write(DataObject data) throws Exception{
		writeTo(data,new URL((String)data.getProperties().get(DataObject.URI)));
	}
	public static void writeTo(DataObject data,URL url)throws Exception{
		data.getDataObjectType().writeTo(data,FoolURLConnection.open(url));
		data.getProperties().put(DataObject.URI,url.toString());
	}
}