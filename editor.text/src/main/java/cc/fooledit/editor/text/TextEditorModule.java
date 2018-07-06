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
package cc.fooledit.editor.text;
import cc.fooledit.*;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TextEditorModule{
	public static final String NAME="editor.text";
	public static final RegistryNode<String,Object> REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,Command> COMMAND_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,String> KEYMAP_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,String> LOCALE_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,Object> MENU_REGISTRY=new SimpleRegistryNode<>();
	public static void onLoad(){
		DataObjectTypeRegistry.addDataObjectType(TextObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(StructuredTextEditor.INSTANCE,TextObject.class);
		CoreModule.TEMPLATE_TYPE_REGISTRY.put(TextTemplate.class.getName(),(obj)->new TextTemplate((String)obj.get("name"),(String)obj.get("description"),(String)obj.get("file"),(String)obj.get("mime"),(String)obj.get("module")));
		TextObject logObject=new TextObject("");
		logObject.getText().bind(Notifier.MESSAGES);
		RegistryNode<String,Object> log=new SimpleRegistryNode<>();
		log.put(DataObject.DEFAULT_NAME,MessageRegistry.getString("LOG",NAME));
		log.put(DataObject.TYPE,TextObjectType.class.getName());
		log.put(DataObject.MIME,"text/plain");
		log.put(DataObject.DATA,logObject);
		DataObjectRegistry.addDataObject(log);
	}
	public static void onInstall(){
		Registry.providesDataObjectType(TextObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(StructuredTextEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(TextObject.class.getName(),NAME);
		Registry.providesTemplateType(TextTemplate.class.getName(),NAME);
		try{
			((ListRegistryNode)CoreModule.TEMPLATE_REGISTRY.getOrCreateChild("children")).put(
					StandardSerializiers.JSON_SERIALIZIER.decode(Helper.readText(Main.INSTANCE.getFile("templates.json",NAME))));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.INFO,null,ex);
		}
	}
	public static void onUnLoad(){
	}
	public static void main(String[] args){
		System.out.println(TextEditorModule.class.getName());
	}
}