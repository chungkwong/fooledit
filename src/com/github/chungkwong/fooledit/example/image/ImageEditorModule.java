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
package com.github.chungkwong.fooledit.example.image;
import static com.github.chungkwong.fooledit.api.DataObjectTypeRegistry.addDataEditor;
import static com.github.chungkwong.fooledit.api.DataObjectTypeRegistry.addDataObjectType;
import static com.github.chungkwong.fooledit.control.TemplateEditor.registerTemplateType;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ImageEditorModule{
	public static final String NAME="editor.image";
	public static void onLoad(){
		addDataObjectType(ImageObjectType.INSTANCE);
		addDataEditor(()->new IconEditor(),ImageObject.class);
		addDataEditor(()->new ImageEditor(),ImageObject.class);
		registerTemplateType("image",(obj)->new ImageTemplate((String)obj.get("name"),(String)obj.get("description"),(String)obj.get("file"),(String)obj.get("mime")));
	}
	public static void onUnLoad(){

	}
}
