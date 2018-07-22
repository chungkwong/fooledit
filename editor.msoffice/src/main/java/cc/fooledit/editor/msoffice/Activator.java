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
package cc.fooledit.editor.msoffice;
import cc.fooledit.core.*;
import cc.fooledit.editor.msoffice.excel.*;
import cc.fooledit.editor.msoffice.powerpoint.*;
import cc.fooledit.editor.msoffice.word.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static void onLoad(){
		DataObjectTypeRegistry.addDataObjectType(DocObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(DocEditor.INSTANCE,DocObject.class);
		DataObjectTypeRegistry.addDataObjectType(DocxObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(DocxEditor.INSTANCE,DocObject.class);
		DataObjectTypeRegistry.addDataObjectType(XlsObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(XlsEditor.INSTANCE,DocObject.class);
		DataObjectTypeRegistry.addDataObjectType(PptObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(PptEditor.INSTANCE,DocObject.class);
		DataObjectTypeRegistry.addDataObjectType(PptxObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(PptxEditor.INSTANCE,DocObject.class);
	}
	public static void onUnLoad(){
	}
	public static void onInstall(){
		Registry.providesDataObjectType(DocObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(DocEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(DocObject.class.getName(),NAME);
		Registry.providesDataObjectType(DocxObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(DocxEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(DocxObject.class.getName(),NAME);
		Registry.providesDataObjectType(XlsObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(XlsEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(XlsObject.class.getName(),NAME);
		Registry.providesDataObjectType(PptObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(PptEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(PptObject.class.getName(),NAME);
		Registry.providesDataObjectType(PptxObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(PptxEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(PptxObject.class.getName(),NAME);
	}
	@Override
	public void start(BundleContext bc) throws Exception{
		onInstall();
		onLoad();
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
