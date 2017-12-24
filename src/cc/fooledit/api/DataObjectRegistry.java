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
import static cc.fooledit.api.CoreModule.DATA_OBJECT_REGISTRY;
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
	private static final Map<String,Object> HISTORY=(Map<String,Object>)PersistenceStatusManager.USER.
			getOrDefault(KEY,()->Helper.hashMap(LIMIT,20,ENTRIES,new LinkedList<>()));
	public static RegistryNode getDataObject(String name){
		return DATA_OBJECT_REGISTRY.getChild(name);
	}
	public static Collection<String> getDataObjectNames(){
		return DATA_OBJECT_REGISTRY.getChildNames();
	}
	public static RegistryNode get(Object json){
		Map<String,String> prop=(Map<String,String>)json;
		String type=prop.get(DataObject.TYPE);
		DataObjectType builder=DataObjectTypeRegistry.getDataObjectTypes().values().stream().filter((t)->t.getClass().getName().equals(type)).findFirst().get();
		RegistryNode object;
		if(prop.containsKey(DataObject.URI)){
			String uri=prop.get(DataObject.URI);
			Optional<RegistryNode> old=DATA_OBJECT_REGISTRY.toMap().values().stream().filter((o)->uri.equals(o.getChild(DataObject.URI))).findAny();
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
	public static void addDataObject(RegistryNode<String,Object,String> object){
		//if(DATA_OBJECT_REGISTRY.toMap().containsValue(data))
		//	return;
		String name=(String)object.getChildOrDefault(DataObject.DEFAULT_NAME,UNTITLED);
		if(DATA_OBJECT_REGISTRY.hasChild(name)){
			for(int i=1;;i++){
				String tmp=name+":"+i;
				if(!DATA_OBJECT_REGISTRY.hasChild(tmp)){
					name=tmp;
					break;
				}
			}
		}
		object.addChild(DataObject.BUFFER_NAME,name);
		object.addChild(DataObject.TYPE,((DataObject)object.getChild(DataObject.DATA)).getDataObjectType().getClass().getName());
		DATA_OBJECT_REGISTRY.addChild(name,object);
		addHistoryEntry(object.toMap());
	}
	public static void removeDataObject(RegistryNode<String,Object,String> data){
		DATA_OBJECT_REGISTRY.removeChild((String)data.getChild(DataObject.BUFFER_NAME));
	}
	private static void addHistoryEntry(Map<String,Object> prop){
		if(prop.containsKey(DataObject.URI)){
			String uri=(String)prop.get(DataObject.URI);
			List<Map<String,Object>> list=getHistoryList();
			Iterator<Map<String,Object>> iter=list.iterator();
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
	public static List<Map<String,Object>> getHistoryList(){
		return (List)HISTORY.get(ENTRIES);
	}
	public static RegistryNode getNextDataObject(RegistryNode<String,Object,String> curr){
		Map.Entry<String,RegistryNode> next=DATA_OBJECT_REGISTRY.higherEntry((String)curr.getChild(DataObject.BUFFER_NAME));
		if(next==null)
			return DATA_OBJECT_REGISTRY.firstEntry().getValue();
		else
			return next.getValue();
	}
	public static RegistryNode getPreviousDataObject(RegistryNode<String,Object,String> curr){
		Map.Entry<String,RegistryNode> prev=DATA_OBJECT_REGISTRY.lowerEntry((String)curr.getChild(DataObject.BUFFER_NAME));
		if(prev==null)
			return DATA_OBJECT_REGISTRY.lastEntry().getValue();
		else
			return prev.getValue();
	}
	public static RegistryNode<String,Object,String> create(DataObjectType<?> type){
		RegistryNode<String,Object,String> object=new SimpleRegistryNode<>();
		object.addChild(DataObject.TYPE,type.getClass().getName());
		object.addChild(DataObject.DEFAULT_NAME,type.getDisplayName());
		object.addChild(DataObject.DATA,type.create());
		return object;
	}
	public static RegistryNode<String,Object,String> readFrom(URL url)throws Exception{
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
	public static RegistryNode<String,Object,String> readFrom(URL url,MimeType mime)throws Exception{
		return readFrom(FoolURLConnection.open(url),mime);
	}
	public static RegistryNode<String,Object,String> readFrom(URLConnection connection,MimeType mime)throws Exception{
		for(DataObjectType type:DataObjectTypeRegistry.getPreferedDataObjectType(mime)){
			try{
				return readFrom(connection,type,mime);
			}catch(Exception ex){
				Logger.getGlobal().log(Level.INFO,null,ex);
			}
		}
		throw new Exception();
	}
	public static RegistryNode<String,Object,String> readFrom(URL url,DataObjectType type,MimeType mime)throws Exception{
		return readFrom(FoolURLConnection.open(url),type,mime);
	}
	public static RegistryNode<String,Object,String> readFrom(URLConnection connection,DataObjectType type,MimeType mime)throws Exception{
		RegistryNode<String,Object,String>  object=new SimpleRegistryNode<>();
		DataObject data=type.readFrom(connection,mime,object);
		object.addChild(DataObject.URI,connection.getURL().toString());
		object.addChild(DataObject.MIME,mime.toString());
		object.addChild(DataObject.DEFAULT_NAME,getLastComponent(connection.getURL().getPath()));
		object.addChild(DataObject.TYPE,type.getClass().getName());
		object.addChild(DataObject.DATA,data);
		addDataObject(object);
		return object;
	}
	private static String getLastComponent(String path){
		int i=path.lastIndexOf('/');
		return i==-1?path:path.substring(i+1);
	}
	public static void write(RegistryNode<String,Object,String> data) throws Exception{
		writeTo(data,new URL((String)data.getChild(DataObject.URI)));
	}
	public static void writeTo(RegistryNode<String,Object,String> object,URL url)throws Exception{
		DataObject data=((DataObject)object.getChild(DataObject.DATA));
		data.getDataObjectType().writeTo(data,FoolURLConnection.open(url),object);
		object.addChild(DataObject.URI,url.toString());
	}
}