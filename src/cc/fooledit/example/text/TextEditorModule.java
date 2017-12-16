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
package cc.fooledit.example.text;
import cc.fooledit.api.*;
import cc.fooledit.control.*;
import cc.fooledit.model.*;
import cc.fooledit.spi.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TextEditorModule{
	public static final String NAME="editor.code";
	public static final RegistryNode<String,Object,String> REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,Command,String> COMMAND_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,String,String> KEYMAP_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,String,String> LOCALE_REGISTRY=new SimpleRegistryNode<>();
	public static final RegistryNode<String,Object,String> MENU_REGISTRY=new SimpleRegistryNode<>();
	public static void onLoad(){
		DataObjectTypeRegistry.addDataObjectType(TextObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(()->new StructuredTextEditor(),TextObject.class);
		TemplateEditor.registerTemplateType("text",(obj)->new TextTemplate((String)obj.get("name"),(String)obj.get("description"),(String)obj.get("file"),(String)obj.get("mime")));
	}
	public static void onUnLoad(){

	}
}
