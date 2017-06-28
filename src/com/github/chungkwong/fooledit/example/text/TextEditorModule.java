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
package com.github.chungkwong.fooledit.example.text;
import com.github.chungkwong.fooledit.api.*;
import com.github.chungkwong.fooledit.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TextEditorModule implements Module{
	@Override
	public ModuleDescriptor getModuleDescriptor(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void onLoad(){
		DataObjectTypeRegistry.addDataObjectType(TextObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(()->new StructuredTextEditor(),TextObject.class);
		TemplateChooser.registerTemplateType("text",(obj)->new TextTemplate((String)obj.get("name"),(String)obj.get("description"),(String)obj.get("file"),(String)obj.get("mime")));
	}
	@Override
	public void onUnLoad(){

	}

}
