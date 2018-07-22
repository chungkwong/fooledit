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
package cc.fooledit.editor.odf;
import cc.fooledit.core.*;
import cc.fooledit.editor.odf.calc.*;
import cc.fooledit.editor.odf.chart.*;
import cc.fooledit.editor.odf.draw.*;
import cc.fooledit.editor.odf.impress.*;
import cc.fooledit.editor.odf.writer.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static void onLoad(){
		DataObjectTypeRegistry.addDataObjectType(OdtObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(OdtEditor.INSTANCE,OdtObject.class);
		DataObjectTypeRegistry.addDataObjectType(OdsObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(OdsEditor.INSTANCE,OdtObject.class);
		DataObjectTypeRegistry.addDataObjectType(OdpObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(OdpEditor.INSTANCE,OdtObject.class);
		DataObjectTypeRegistry.addDataObjectType(OdgObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(OdgEditor.INSTANCE,OdtObject.class);
		DataObjectTypeRegistry.addDataObjectType(ChartObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(ChartEditor.INSTANCE,OdtObject.class);
	}
	public static void onUnLoad(){
	}
	public static void onInstall(){
		Registry.providesDataObjectType(OdtObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(OdtEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(OdtObject.class.getName(),NAME);
		Registry.providesDataObjectType(OdsObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(OdsEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(OdsObject.class.getName(),NAME);
		Registry.providesDataObjectType(OdpObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(OdpEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(OdpObject.class.getName(),NAME);
		Registry.providesDataObjectType(OdgObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(OdgEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(OdgObject.class.getName(),NAME);
		Registry.providesDataObjectType(ChartObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(ChartEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(ChartObject.class.getName(),NAME);
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
