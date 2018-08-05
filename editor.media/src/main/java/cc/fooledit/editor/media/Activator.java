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
package cc.fooledit.editor.media;
import cc.fooledit.core.*;
import static cc.fooledit.core.DataObjectTypeRegistry.addDataEditor;
import static cc.fooledit.core.DataObjectTypeRegistry.addDataObjectType;
import cc.fooledit.editor.media.Activator;
import cc.fooledit.spi.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.url.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static void onLoad(){
		addDataObjectType(MediaObjectType.INSTANCE);
		addDataEditor(MediaEditor.INSTANCE,MediaObject.class);
		DataObjectTypeRegistry.addToolBox(new ControlToolBox(),MediaEditor.class);
		addDataObjectType(MidiObjectType.INSTANCE);
		addDataEditor(MidiEditor.INSTANCE,MidiObject.class);
	}
	public static void onUnLoad(){
	}
	public static void onInstall(){
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("audio/midi","cc.fooledit.editor.media.MidiObjectType");
		MultiRegistryNode.addChildElement("midi","audio/midi",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("mid","audio/midi",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("kar","audio/midi",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("audio/x-midi","audio/midi");
	}
	@Override
	public void start(BundleContext bc) throws Exception{
		onInstall();
		onLoad();
		Hashtable properties=new Hashtable();
		properties.put(URLConstants.URL_HANDLER_PROTOCOL,new String[]{"cdda","dv","dvd","mms","pvr","rtp","rtsp","simpledvd","vcdx","vlc","v4l2"});
		bc.registerService(URLStreamHandlerService.class.getName(),new NaiveStreamHandler(),properties);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
