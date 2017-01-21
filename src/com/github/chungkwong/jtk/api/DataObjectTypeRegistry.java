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
import com.github.chungkwong.jtk.example.audio.*;
import com.github.chungkwong.jtk.example.image.*;
import com.github.chungkwong.jtk.example.text.*;
import com.github.chungkwong.jtk.example.tool.*;
import com.github.chungkwong.jtk.model.*;
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DataObjectTypeRegistry{
	private static final HashMap<Class<? extends DataObject>,List<DataEditor>> editors=new HashMap<>();
	private static final HashMap<String,List<DataObjectType>> mime2type=new HashMap<>();
	private static final List<DataObjectType> types=new ArrayList<>();
	public static void addDataObjectType(DataObjectType type){
		types.add(type);
		for(String mime:type.getPreferedMIME())
			addDataObjectType(type,mime);
	}
	private static void addDataObjectType(DataObjectType type,String mime){
		if(!mime2type.containsKey(mime))
			mime2type.put(mime,new LinkedList<>());
		mime2type.get(mime).add(0,type);
	}
	public static void addDataEditor(DataEditor editor,Class<? extends DataObject> objectClass){
		if(!editors.containsKey(objectClass))
			editors.put(objectClass,new LinkedList<>());
		editors.get(objectClass).add(0,editor);
	}
	public static List<DataEditor> getDataEditors(Class<? extends DataObject> cls){
		return editors.getOrDefault(cls,Collections.EMPTY_LIST);
	}
	public static List<DataObjectType> getPreferedDataObjectType(String mime){
		return mime2type.getOrDefault(mime,Collections.EMPTY_LIST);
	}
	public static List<DataObjectType> getFallbackDataObjectType(String mime){
		List<DataObjectType> prefered=getPreferedDataObjectType(mime);
		return types.stream().filter((t)->t.canHandleMIME(mime)&&!prefered.contains(t)).collect(Collectors.toList());
	}
	public static List<DataObjectType> getDataObjectTypes(){
		return types;
	}
	static{
		addDataObjectType(TextObjectType.INSTANCE);
		addDataEditor(new TextEditor(),TextObject.class);
		addDataObjectType(ImageObjectType.INSTANCE);
		addDataEditor(new ImageEditor(),ImageObject.class);
		addDataObjectType(AudioObjectType.INSTANCE);
		addDataEditor(new AudioEditor(),AudioObject.class);
		addDataEditor(new Browser(),BrowserData.class);
	}
}
