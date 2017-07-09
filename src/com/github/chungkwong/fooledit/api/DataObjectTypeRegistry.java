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
package com.github.chungkwong.fooledit.api;
import com.github.chungkwong.fooledit.control.*;
import com.github.chungkwong.fooledit.example.binary.*;
import com.github.chungkwong.fooledit.example.filesystem.*;
import com.github.chungkwong.fooledit.example.image.*;
import com.github.chungkwong.fooledit.example.media.*;
import com.github.chungkwong.fooledit.example.text.*;
import com.github.chungkwong.fooledit.example.tool.*;
import com.github.chungkwong.fooledit.model.*;
import com.github.chungkwong.fooledit.util.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DataObjectTypeRegistry{
	private static final HashMap<Class<? extends DataObject>,List<Cache<DataEditor>>> editors=new HashMap<>();
	private static final List<DataObjectType> types=new ArrayList<>();
	public static void addDataObjectType(DataObjectType type){
		types.add(type);
	}
	public static void addDataEditor(Supplier<DataEditor> editor,Class<? extends DataObject> objectClass){
		if(!editors.containsKey(objectClass))
			editors.put(objectClass,new LinkedList<>());
		editors.get(objectClass).add(0,new Cache<>(editor));
	}
	public static List<DataEditor> getDataEditors(Class<? extends DataObject> cls){
		return ((List<Cache<DataEditor>>)editors.getOrDefault(cls,Collections.EMPTY_LIST)).stream().map((c)->c.get()).collect(Collectors.toList());
	}
	public static List<DataObjectType> getPreferedDataObjectType(MimeType mime){
		LinkedList<DataObjectType> cand=new LinkedList<DataObjectType>();
		String type=mime.getType();
		if(type.equals("video")||type.equals("audio"))
			cand.add(MediaObjectType.INSTANCE);
		else if(type.equals("image"))
			cand.add(ImageObjectType.INSTANCE);
		if(type.equals("text")||mime.getParameters().containsKey("charset"))
			cand.add(TextObjectType.INSTANCE);
		cand.add(BinaryObjectType.INSTANCE);
		return cand;
	}
	public static List<DataObjectType> getDataObjectTypes(){
		return types;
	}
	static{
		//ModuleRegistry.ensureLoaded("editor.code");
		//addDataObjectType(TextObjectType.INSTANCE);
		//addDataEditor(()->new TextEditor(),TextObject.class);
		//addDataEditor(()->new StructuredTextEditor(),TextObject.class);
		addDataEditor(()->new Browser(),BrowserData.class);
		addDataEditor(()->new FileSystemPrompt(),FileSystemPrompt.class);
		addDataObjectType(TemplateChooser.INSTANCE);
		addDataEditor(()->TemplateChooser.INSTANCE,TemplateChooser.class);
	}
}
