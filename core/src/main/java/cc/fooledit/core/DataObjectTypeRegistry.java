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
import cc.fooledit.spi.*;
import java.util.*;
import java.util.stream.*;
import javax.activation.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DataObjectTypeRegistry{
	public static void addDataObjectType(DataObjectType type){
		CoreModule.DATA_OBJECT_TYPE_REGISTRY.put(type.getClass().getName(),type);
	}
	public static void addDataEditor(DataEditor editor,Class<? extends DataObject> objectClass){
		CoreModule.DATA_OBJECT_EDITOR_REGISTRY.put(editor.getClass().getName(),editor);
		MultiRegistryNode.addChildElement(objectClass.getName(),editor.getClass().getName(),CoreModule.TYPE_TO_EDITOR_REGISTRY);
	}
	public static List<DataEditor> getDataEditors(Class<? extends DataObject> cls){
		return MultiRegistryNode.getChildElements(cls.getName(),CoreModule.TYPE_TO_EDITOR_REGISTRY).stream().map((c)->CoreModule.DATA_OBJECT_EDITOR_REGISTRY.get(c)).collect(Collectors.toList());
	}
	public static void addToolBox(ToolBox toolBox,Class<? extends DataEditor> editorClass){
		CoreModule.TOOLBOX_REGISTRY.put(toolBox.getClass().getName(),toolBox);
		MultiRegistryNode.addChildElement(editorClass.getName(),toolBox.getClass().getName(),CoreModule.EDITOR_TO_TOOLBOX_REGISTRY);
	}
	public static List<ToolBox> getDataToolBoxs(Class<? extends DataEditor> cls){
		return MultiRegistryNode.getChildElements(cls.getName(),CoreModule.EDITOR_TO_TOOLBOX_REGISTRY).stream().map((c)->CoreModule.TOOLBOX_REGISTRY.get(c)).collect(Collectors.toList());
	}
	public static void registerMime(String mime,String type){
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put(mime,type);
	}
	public static List<DataObjectType> getPreferedDataObjectType(MimeType mime){
		LinkedList<DataObjectType> cand=new LinkedList<DataObjectType>();
		String t=CoreModule.CONTENT_TYPE_LOADER_REGISTRY.get(mime.getBaseType());
		//System.err.println(mime.toString());
		if(t!=null)
			cand.add(CoreModule.DATA_OBJECT_TYPE_REGISTRY.get(t));
		String type=mime.getPrimaryType();
		if(type.equals("video")||type.equals("audio"))
			cand.add(CoreModule.DATA_OBJECT_TYPE_REGISTRY.get("cc.fooledit.editor.media.MediaObjectType"));
		else if(type.equals("image"))
			cand.add(CoreModule.DATA_OBJECT_TYPE_REGISTRY.get("cc.fooledit.editor.image.GraphicsObjectType"));
		if(type.equals("text")||mime.getParameter("charset")!=null)
			cand.add(CoreModule.DATA_OBJECT_TYPE_REGISTRY.get("cc.fooledit.editor.text.TextObjectType"));
		cand.add(CoreModule.DATA_OBJECT_TYPE_REGISTRY.get("cc.fooledit.editor.binary.BinaryObjectType"));
		return cand;
	}
}
