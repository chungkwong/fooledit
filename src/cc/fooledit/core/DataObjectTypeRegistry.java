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
import cc.fooledit.control.*;
import cc.fooledit.editor.binary.*;
import cc.fooledit.editor.image.*;
import cc.fooledit.editor.media.*;
import cc.fooledit.editor.text.*;
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
	public static void addDataObjectType(DataObjectType type){
		CoreModule.DATA_OBJECT_TYPE_REGISTRY.addChild(type.getClass().getName(),type);
	}
	public static void addDataEditor(Supplier<DataEditor> editor,Class<? extends DataObject> objectClass){
		if(!CoreModule.DATA_OBJECT_EDITOR_REGISTRY.hasChild(objectClass.getName()))
			CoreModule.DATA_OBJECT_EDITOR_REGISTRY.addChild(objectClass.getName(),new ListRegistryNode<>());
		CoreModule.DATA_OBJECT_EDITOR_REGISTRY.getChild(objectClass.getName()).addChild(0,new Cache<>(editor));
	}
	public static List<DataEditor> getDataEditors(Class<? extends DataObject> cls){
		if(CoreModule.DATA_OBJECT_EDITOR_REGISTRY.hasChild(cls.getName()))
			return ((ListRegistryNode<Cache<DataEditor>,String>)CoreModule.DATA_OBJECT_EDITOR_REGISTRY.getChild(cls.getName())).toMap().values().stream().map((c)->c.get()).collect(Collectors.toList());
		return Collections.emptyList();
	}
	public static void registerMime(String mime,String type){
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.addChild(mime,type);
	}
	public static List<DataObjectType> getPreferedDataObjectType(MimeType mime){
		LinkedList<DataObjectType> cand=new LinkedList<DataObjectType>();
		String t=CoreModule.CONTENT_TYPE_LOADER_REGISTRY.getChild(mime.getBaseType());
		//System.err.println(mime.toString());
		if(t!=null)
			cand.add(CoreModule.DATA_OBJECT_TYPE_REGISTRY.getChild(t));
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
	static{
		Registry.registerApplication("template","fooledit/template",TemplateEditor.INSTANCE,TemplateEditor.class,()->TemplateEditor.INSTANCE);addDataObjectType(TemplateEditor.INSTANCE);//TODO
		Registry.registerApplication("registry","fooledit/registry",RegistryEditor.INSTANCE,RegistryEditor.class,()->RegistryEditor.INSTANCE);addDataObjectType(RegistryEditor.INSTANCE);//TODO
	}
}
