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
import cc.fooledit.editor.odf.Activator;
import cc.fooledit.editor.odf.calc.*;
import cc.fooledit.editor.odf.chart.*;
import cc.fooledit.editor.odf.draw.*;
import cc.fooledit.editor.odf.impress.*;
import cc.fooledit.editor.odf.writer.*;
import cc.fooledit.spi.*;
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
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.oasis.opendocument.spreadsheet","cc.fooledit.editor.odf.calc.OdsObjectType");
		MultiRegistryNode.addChildElement("ods","application/vnd.oasis.opendocument.spreadsheet",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.oasis.opendocument.spreadsheet-template","cc.fooledit.editor.odf.calc.OdsObjectType");
		MultiRegistryNode.addChildElement("ots","application/vnd.oasis.opendocument.spreadsheet-template",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.oasis.opendocument.text","cc.fooledit.editor.odf.writer.OdtObjectType");
		MultiRegistryNode.addChildElement("odt","application/vnd.oasis.opendocument.text",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.oasis.opendocument.text-master","cc.fooledit.editor.odf.writer.OdtObjectType");
		MultiRegistryNode.addChildElement("odm","application/vnd.oasis.opendocument.text-master",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.oasis.opendocument.text-master-template","cc.fooledit.editor.odf.writer.OdtObjectType");
		MultiRegistryNode.addChildElement("otm","application/vnd.oasis.opendocument.text-master-template",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.oasis.opendocument.text-web","cc.fooledit.editor.odf.writer.OdtObjectType");
		MultiRegistryNode.addChildElement("oth","application/vnd.oasis.opendocument.text-web",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.oasis.opendocument.presentation","cc.fooledit.editor.odf.impress.OdpObjectType");
		MultiRegistryNode.addChildElement("odp","application/vnd.oasis.opendocument.presentation",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.oasis.opendocument.presentation-template","cc.fooledit.editor.odf.impress.OdpObjectType");
		MultiRegistryNode.addChildElement("otp","application/vnd.oasis.opendocument.presentation-template",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.oasis.opendocument.graphics","cc.fooledit.editor.odf.draw.OdgObjectType");
		MultiRegistryNode.addChildElement("odg","application/vnd.oasis.opendocument.graphics",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.oasis.opendocument.graphics-template","cc.fooledit.editor.odf.draw.OdgObjectType");
		MultiRegistryNode.addChildElement("otg","application/vnd.oasis.opendocument.graphics-template",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.oasis.opendocument.image","cc.fooledit.editor.odf.draw.OdgObjectType");
		MultiRegistryNode.addChildElement("odi","application/vnd.oasis.opendocument.image",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.oasis.opendocument.chart","cc.fooledit.editor.odf.chart.ChartObjectType");
		MultiRegistryNode.addChildElement("odc","application/vnd.oasis.opendocument.chart",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.oasis.opendocument.chart-template","cc.fooledit.editor.odf.chart.ChartObjectType");
		MultiRegistryNode.addChildElement("otc","application/vnd.oasis.opendocument.chart-template",CoreModule.SUFFIX_REGISTRY);
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
