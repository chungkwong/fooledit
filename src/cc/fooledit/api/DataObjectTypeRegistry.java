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
import cc.fooledit.control.*;
import cc.fooledit.example.binary.*;
import cc.fooledit.example.image.*;
import cc.fooledit.example.media.*;
import cc.fooledit.example.text.*;
import cc.fooledit.model.*;
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import javax.activation.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DataObjectTypeRegistry{
	private static final HashMap<Class<? extends DataObject>,List<Cache<DataEditor>>> editors=new HashMap<>();
	private static final Map<String,DataObjectType> types=new HashMap<>();
	private static final Map<String,String> mimes=new HashMap<>();
	public static void addDataObjectType(DataObjectType type){
		types.put(type.getName(),type);
	}
	public static void addDataEditor(Supplier<DataEditor> editor,Class<? extends DataObject> objectClass){
		if(!editors.containsKey(objectClass))
			editors.put(objectClass,new LinkedList<>());
		editors.get(objectClass).add(0,new Cache<>(editor));
	}
	public static List<DataEditor> getDataEditors(Class<? extends DataObject> cls){
		return ((List<Cache<DataEditor>>)editors.getOrDefault(cls,Collections.EMPTY_LIST)).stream().map((c)->c.get()).collect(Collectors.toList());
	}
	public static void registerMime(String mime,String type){
		mimes.put(mime,type);
	}
	public static List<DataObjectType> getPreferedDataObjectType(MimeType mime){
		LinkedList<DataObjectType> cand=new LinkedList<DataObjectType>();
		String t=mimes.get(mime.getBaseType());
		System.err.println(mime.toString());
		if(t!=null)
			cand.add(types.get(t));
		String type=mime.getPrimaryType();
		if(type.equals("video")||type.equals("audio"))
			cand.add(MediaObjectType.INSTANCE);
		else if(type.equals("image"))
			cand.add(ImageObjectType.INSTANCE);
		if(type.equals("text")||mime.getParameter("charset")!=null)
			cand.add(TextObjectType.INSTANCE);
		cand.add(BinaryObjectType.INSTANCE);
		return cand;
	}
	public static Map<String,DataObjectType> getDataObjectTypes(){
		return types;
	}
	static{
		//ModuleRegistry.ensureLoaded("editor.code");
		//addDataObjectType(TextObjectType.INSTANCE);
		//addDataEditor(()->new TextEditor(),TextObject.class);
		//addDataEditor(()->new StructuredTextEditor(),TextObject.class);
		ApplicationRegistry.register("template","fooledit/template",TemplateEditor.INSTANCE,TemplateEditor.class,()->TemplateEditor.INSTANCE);addDataObjectType(new TemplateEditor());//TODO
	}
}
