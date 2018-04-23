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
import cc.fooledit.*;
import static cc.fooledit.core.CoreModule.DATA_OBJECT_REGISTRY;
import cc.fooledit.spi.*;
import java.io.*;
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
	private static final String TRASH="trash";
	public static RegistryNode getDataObject(String name){
		return DATA_OBJECT_REGISTRY.get(name);
	}
	public static Collection<String> getDataObjectNames(){
		return DATA_OBJECT_REGISTRY.keySet();
	}
	public static RegistryNode get(RegistryNode<String,Object> prop){
		String type=(String)prop.get(DataObject.TYPE);
		DataObjectType builder=CoreModule.DATA_OBJECT_TYPE_REGISTRY.get(type);
		RegistryNode object;
		if(prop.containsKey(DataObject.URI)){
			String uri=(String)prop.get(DataObject.URI);
			Optional<RegistryNode> old=DATA_OBJECT_REGISTRY.values().stream().filter((o)->uri.equals(o.get(DataObject.URI))).findAny();
			if(old.isPresent())
				return old.get();
			try{
				String mime=(String)prop.get(DataObject.MIME);
				if(mime!=null){
					try{
						object=readFrom(new URL(uri),builder,new MimeType(mime));
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
	public static void addDataObject(RegistryNode<String,Object> object){
		//if(DATA_OBJECT_REGISTRY.toMap().containsValue(data))
		//	return;
		String name=(String)object.getOrDefault(DataObject.DEFAULT_NAME,UNTITLED);
		if(DATA_OBJECT_REGISTRY.containsKey(name)){
			for(int i=1;;i++){
				String tmp=name+":"+i;
				if(!DATA_OBJECT_REGISTRY.containsKey(tmp)){
					name=tmp;
					break;
				}
			}
		}
		object.put(DataObject.BUFFER_NAME,name);
		object.put(DataObject.TYPE,((DataObject)object.get(DataObject.DATA)).getDataObjectType().getClass().getName());
		DATA_OBJECT_REGISTRY.put(name,object);
		addHistoryEntry(object);
	}
	public static void removeDataObject(RegistryNode<String,Object> data){
		DATA_OBJECT_REGISTRY.remove((String)data.get(DataObject.BUFFER_NAME));
	}
	private static void addHistoryEntry(Map<String,Object> prop){
		if(prop.containsKey(DataObject.URI)){
			String uri=(String)prop.get(DataObject.URI);
			ListRegistryNode<RegistryNode<String,Object>> history=CoreModule.HISTORY_REGISTRY;
			for(int i=0;i<history.size();i++){
				RegistryNode<String,Object> entry=history.get(i);
				if(uri.equals(entry.get(DataObject.URI)))
					history.remove(i);
			}
			history.put(0,new SimpleRegistryNode<String,Object>(prop));
		}
	}
	public static RegistryNode getNextDataObject(RegistryNode<String,Object> curr){
		Map.Entry<String,RegistryNode> next=DATA_OBJECT_REGISTRY.higherEntry((String)curr.get(DataObject.BUFFER_NAME));
		if(next==null)
			return DATA_OBJECT_REGISTRY.firstEntry().getValue();
		else
			return next.getValue();
	}
	public static RegistryNode getPreviousDataObject(RegistryNode<String,Object> curr){
		Map.Entry<String,RegistryNode> prev=DATA_OBJECT_REGISTRY.lowerEntry((String)curr.get(DataObject.BUFFER_NAME));
		if(prev==null)
			return DATA_OBJECT_REGISTRY.lastEntry().getValue();
		else
			return prev.getValue();
	}
	public static RegistryNode<String,Object> create(DataObjectType<?> type){
		RegistryNode<String,Object> object=new SimpleRegistryNode<>();
		object.put(DataObject.TYPE,type.getClass().getName());
		object.put(DataObject.DEFAULT_NAME,type.getDisplayName());
		object.put(DataObject.DATA,type.create());
		return object;
	}
	public static RegistryNode<String,Object> readFrom(URL url)throws Exception{
		FoolURLConnection connection=FoolURLConnection.open(url);
		for(String mime:ContentTypeHelper.guess(connection)){
			try{
				return readFrom(url,new MimeType(mime));
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		}
		throw new Exception();
	}
	public static RegistryNode<String,Object> readFrom(URL url,MimeType mime)throws Exception{
		return readFrom(FoolURLConnection.open(url),mime);
	}
	public static RegistryNode<String,Object> readFrom(URLConnection connection,MimeType mime)throws Exception{
		for(DataObjectType type:DataObjectTypeRegistry.getPreferedDataObjectType(mime)){
			try{
				return readFrom(connection,type,mime);
			}catch(Exception ex){
				Logger.getGlobal().log(Level.INFO,null,ex);
			}
		}
		throw new Exception();
	}
	public static RegistryNode<String,Object> readFrom(URL url,DataObjectType type,MimeType mime)throws Exception{
		return readFrom(FoolURLConnection.open(url),type,mime);
	}
	public static RegistryNode<String,Object> readFrom(URLConnection connection,DataObjectType type,MimeType mime)throws Exception{
		RegistryNode<String,Object>  object=new SimpleRegistryNode<>();
		DataObject data=type.readFrom(connection,mime,object);
		object.put(DataObject.URI,connection.getURL().toString());
		object.put(DataObject.MIME,mime.toString());
		object.put(DataObject.DEFAULT_NAME,getLastComponent(connection.getURL().getPath()));
		object.put(DataObject.TYPE,type.getClass().getName());
		object.put(DataObject.DATA,data);
		addDataObject(object);
		return object;
	}
	private static String getLastComponent(String path){
		int i=path.lastIndexOf('/');
		return i==-1?path:path.substring(i+1);
	}
	public static void clean(RegistryNode<String,Object> data){
		if((Boolean)data.getOrDefault(DataObject.MODIFIED,false))
			try{
				write(data);
			}catch(Exception e){
				try{
					String origName=(String)data.getOrDefault(DataObject.DEFAULT_NAME,UNTITLED);
					writeTo(data,File.createTempFile("bak_",origName,getTrashDirectory()).toURI().toURL());
				}catch(Exception ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
				}
			}
	}
	public static void write(RegistryNode<String,Object> data) throws Exception{
		writeTo(data,new URL((String)data.get(DataObject.URI)));
	}
	public static void writeTo(RegistryNode<String,Object> object,URL url)throws Exception{
		DataObject data=((DataObject)object.get(DataObject.DATA));
		data.getDataObjectType().writeTo(data,FoolURLConnection.open(url),object);
		object.put(DataObject.URI,url.toString());
	}
	private static File getTrashDirectory(){
		return new File(Main.INSTANCE.getUserPath(),TRASH);
	}
}