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
import cc.fooledit.spi.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String CONTENT_TYPE="application/epub+zip";
	public static void onLoad(){
		DataObjectTypeRegistry.addDataObjectType(EpubObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(EpubEditor.INSTANCE,EpubObject.class);
		DataObjectTypeRegistry.addToolBox(ContentsToolBox.INSTANCE,EpubEditor.class);
	}
	public static void onUnLoad(){
	}
	public static void onInstall(){
		Registry.providesDataObjectType(EpubObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(EpubEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(EpubObject.class.getName(),NAME);
		Registry.providesToolBox(ContentsToolBox.class.getName(),NAME);
		Registry.providesEditorToToolbox(EpubEditor.class.getName(),NAME);
		CoreModule.CONTENT_TYPE_SUPERCLASS_REGISTRY.put(CONTENT_TYPE,"application/zip");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put(CONTENT_TYPE,EpubObjectType.class.getName());
		MultiRegistryNode.addChildElement("epub",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
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
