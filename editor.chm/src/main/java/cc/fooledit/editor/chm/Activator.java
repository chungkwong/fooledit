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
package cc.fooledit.editor.chm;
import cc.fooledit.core.*;
import cc.fooledit.editor.chm.Activator;
import cc.fooledit.spi.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.url.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getName();
	public static final String CONTENT_TYPE="application/vnd.ms-htmlhelp";
	public static void onLoad(){
		DataObjectTypeRegistry.addDataObjectType(ChmObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(ChmEditor.INSTANCE,ChmObject.class);
		DataObjectTypeRegistry.addToolBox(ContentsToolBox.INSTANCE,ChmEditor.class);
	}
	public static void onUnLoad(){
	}
	public static void onInstall(){
		Registry.providesDataObjectType(ChmObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(ChmEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(ChmObject.class.getName(),NAME);
		Registry.providesProtocol("chm",NAME);
		Registry.providesEditorToToolbox(ChmEditor.class.getName(),NAME);
		Registry.providesToolBox(ContentsToolBox.class.getName(),NAME);
		MultiRegistryNode.addChildElement("chm",CONTENT_TYPE,CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put(CONTENT_TYPE,cc.fooledit.editor.chm.ChmObjectType.class.getName());
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-chm",CONTENT_TYPE);
	}
	@Override
	public void start(BundleContext bc) throws Exception{
		onInstall();
		onLoad();
		Hashtable properties=new Hashtable();
		properties.put(URLConstants.URL_HANDLER_PROTOCOL,new String[]{"chm"});
		bc.registerService(URLStreamHandlerService.class.getName(),new ChmStreamHandler(),properties);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
