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
import cc.fooledit.editor.msoffice.Activator;
import cc.fooledit.editor.msoffice.excel.*;
import cc.fooledit.editor.msoffice.powerpoint.*;
import cc.fooledit.editor.msoffice.word.*;
import cc.fooledit.spi.*;
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
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.ms-excel","cc.fooledit.editor.msoffice.excel.XlsObjectType");
		MultiRegistryNode.addChildElement("xls","application/vnd.ms-excel",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("xlc","application/vnd.ms-excel",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("xll","application/vnd.ms-excel",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("xlm","application/vnd.ms-excel",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("xlw","application/vnd.ms-excel",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("xla","application/vnd.ms-excel",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("xlt","application/vnd.ms-excel",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("xld","application/vnd.ms-excel",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/msexcel","application/vnd.ms-excel");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-msexcel","application/vnd.ms-excel");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("zz-application/zz-winassoc-xls","application/vnd.ms-excel");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet","cc.fooledit.editor.msoffice.excel.XlsObjectType");
		MultiRegistryNode.addChildElement("xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.openxmlformats-officedocument.spreadsheetml.template","cc.fooledit.editor.msoffice.excel.XlsObjectType");
		MultiRegistryNode.addChildElement("xltx","application/vnd.openxmlformats-officedocument.spreadsheetml.template",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.ms-word","cc.fooledit.editor.msoffice.word.DocObjectType");
		MultiRegistryNode.addChildElement("doc","application/vnd.ms-word",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document","cc.fooledit.editor.msoffice.word.DocxObjectType");
		MultiRegistryNode.addChildElement("docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.openxmlformats-officedocument.wordprocessingml.template","cc.fooledit.editor.msoffice.word.DocxObjectType");
		MultiRegistryNode.addChildElement("dotx","application/vnd.openxmlformats-officedocument.wordprocessingml.template",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.ms-powerpoint","cc.fooledit.editor.msoffice.powerpoint.PptObjectType");
		MultiRegistryNode.addChildElement("ppt","application/vnd.ms-powerpoint",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("pps","application/vnd.ms-powerpoint",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("pot","application/vnd.ms-powerpoint",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("ppz","application/vnd.ms-powerpoint",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/powerpoint","application/vnd.ms-powerpoint");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/mspowerpoint","application/vnd.ms-powerpoint");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-mspowerpoint","application/vnd.ms-powerpoint");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.openxmlformats-officedocument.presentationml.presentation","cc.fooledit.editor.msoffice.powerpoint.PptxObjectType");
		MultiRegistryNode.addChildElement("pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.openxmlformats-officedocument.presentationml.slideshow","cc.fooledit.editor.msoffice.powerpoint.PptxObjectType");
		MultiRegistryNode.addChildElement("ppsx","application/vnd.openxmlformats-officedocument.presentationml.slideshow",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.openxmlformats-officedocument.presentationml.template","cc.fooledit.editor.msoffice.powerpoint.PptxObjectType");
		MultiRegistryNode.addChildElement("potx","application/vnd.openxmlformats-officedocument.presentationml.template",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.openxmlformats-officedocument.presentationml.slide","cc.fooledit.editor.msoffice.powerpoint.PptxObjectType");
		MultiRegistryNode.addChildElement("sldx","application/vnd.openxmlformats-officedocument.presentationml.slide",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.ms-publisher","cc.fooledit.editor.msoffice.publisher.PubObjectType");
		MultiRegistryNode.addChildElement("pub","application/vnd.ms-publisher",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.visio","cc.fooledit.editor.msoffice.visio.VsdObjectType");
		MultiRegistryNode.addChildElement("vsd","application/vnd.visio",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("vst","application/vnd.visio",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("vsw","application/vnd.visio",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("vss","application/vnd.visio",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.ms-visio.drawing.main+xml","cc.fooledit.editor.msoffice.visio.VsdxObjectType");
		MultiRegistryNode.addChildElement("vsdx","application/vnd.ms-visio.drawing.main+xml",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.ms-visio.template.main+xml","cc.fooledit.editor.msoffice.visio.VsdxObjectType");
		MultiRegistryNode.addChildElement("vstx","application/vnd.ms-visio.template.main+xml",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.ms-visio.stencil.main+xml","cc.fooledit.editor.msoffice.visio.VsdxObjectType");
		MultiRegistryNode.addChildElement("vssx","application/vnd.ms-visio.stencil.main+xml",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.ms-visio.drawing.macroEnabled.main+xml","cc.fooledit.editor.msoffice.visio.VsdxObjectType");
		MultiRegistryNode.addChildElement("vsdm","application/vnd.ms-visio.drawing.macroEnabled.main+xml",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.ms-visio.template.macroEnabled.main+xml","cc.fooledit.editor.msoffice.visio.VsdxObjectType");
		MultiRegistryNode.addChildElement("vstm","application/vnd.ms-visio.template.macroEnabled.main+xml",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.ms-visio.stencil.macroEnabled.main+xml","cc.fooledit.editor.msoffice.visio.VsdxObjectType");
		MultiRegistryNode.addChildElement("vssm","application/vnd.ms-visio.stencil.macroEnabled.main+xml",CoreModule.SUFFIX_REGISTRY);
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
