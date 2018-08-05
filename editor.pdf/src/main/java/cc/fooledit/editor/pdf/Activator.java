/*
 * Copyright (C) 2018 Chan Chung Kwong
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
import cc.fooledit.editor.pdf.Activator;
import cc.fooledit.spi.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	@Override
	public void start(BundleContext bc) throws Exception{
		DataObjectTypeRegistry.addDataObjectType(PdfObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(PdfEditor.INSTANCE,PdfObject.class);
		DataObjectTypeRegistry.addToolBox(PageToolBox.INSTANCE,PdfEditor.class);
		DataObjectTypeRegistry.addToolBox(ContentsToolBox.INSTANCE,PdfEditor.class);
		DataObjectTypeRegistry.addToolBox(PropertiesToolBox.INSTANCE,PdfEditor.class);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/pdf","cc.fooledit.editor.pdf.PdfObjectType");
		MultiRegistryNode.addChildElement("pdf","application/pdf",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("image/pdf","application/pdf");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-pdf","application/pdf");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/acrobat","application/pdf");
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
