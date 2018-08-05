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
package cc.fooledit.editor.email;
import cc.fooledit.core.*;
import cc.fooledit.editor.email.Activator;
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
	public static final String APPLICATION_NAME="email";
	public static final String CONTENT_TYPE_NAME="fooledit/email";
	public static void onLoad(){
		Registry.registerApplication(APPLICATION_NAME,CONTENT_TYPE_NAME,MailBoxObjectType.INSTANCE,MailBoxObject.class,MailBoxEditor.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(MultipartEditor.INSTANCE,MultipartObject.class);
		DataObjectTypeRegistry.addDataObjectType(MultipartObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(MessageEditor.INSTANCE,MessageObject.class);
		DataObjectTypeRegistry.addDataObjectType(MessageObjectType.INSTANCE);
		CoreModule.COMMAND_REGISTRY.put("email",new Command("email",()->Main.INSTANCE.addAndShow(DataObjectRegistry.create(MailBoxObjectType.INSTANCE)),Activator.class));
	}
	public static void onUnLoad(){
	}
	public static void onInstall(){
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("multipart/alternative","cc.fooledit.editor.email.MultipartObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("multipart/digest","cc.fooledit.editor.email.MultipartObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("multipart/mixed","cc.fooledit.editor.email.MultipartObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("multipart/parallel","cc.fooledit.editor.email.MultipartObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("multipart/encrypted","cc.fooledit.editor.email.MultipartObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("multipart/signed","cc.fooledit.editor.email.MultipartObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("multipart/report","cc.fooledit.editor.email.MultipartObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("message/rfc822","cc.fooledit.editor.email.MessageObjectType");
		MultiRegistryNode.addChildElement("eml","message/rfc822",CoreModule.SUFFIX_REGISTRY);
	}
	@Override
	public void start(BundleContext bc) throws Exception{
		onInstall();
		onLoad();
		Hashtable properties=new Hashtable();
		properties.put(URLConstants.URL_HANDLER_PROTOCOL,new String[]{"imap","pop3","smtp"});
		bc.registerService(URLStreamHandlerService.class.getName(),new NaiveStreamHandler(),properties);
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
