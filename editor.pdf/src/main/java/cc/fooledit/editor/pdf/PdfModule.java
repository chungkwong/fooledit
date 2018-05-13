/*
 * Copyright (C) 2018 Chan Chung Kwong <1m02math@126.com>
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
package cc.fooledit.editor.pdf;
import cc.fooledit.core.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PdfModule{
	public static final String NAME="editor.pdf";
		public static void onLoad(){
		DataObjectTypeRegistry.addDataObjectType(PdfObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(PdfEditor.INSTANCE,PdfObject.class);
		DataObjectTypeRegistry.addToolBox(PageToolBox.INSTANCE,PdfEditor.class);
		DataObjectTypeRegistry.addToolBox(ContentsToolBox.INSTANCE,PdfEditor.class);
		DataObjectTypeRegistry.addToolBox(PropertiesToolBox.INSTANCE,PdfEditor.class);

	}
	public static void onUnLoad(){

	}
	public static void onInstall(){
		Registry.providesDataObjectType(PdfObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(PdfEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(PdfObject.class.getName(),NAME);
		Registry.providesToolBox(PageToolBox.class.getName(),NAME);
		Registry.providesToolBox(ContentsToolBox.class.getName(),NAME);
		Registry.providesToolBox(PropertiesToolBox.class.getName(),NAME);
		Registry.providesEditorToToolbox(PdfEditor.class.getName(),NAME);
	}
}
