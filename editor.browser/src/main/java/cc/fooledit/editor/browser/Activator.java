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
package cc.fooledit.editor.browser;
import cc.fooledit.*;
import cc.fooledit.core.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String APPLICATION_NAME="browser";
	public static final String CONTENT_TYPE_NAME="fooledit/browser";
	public static void onLoad(){
		Registry.registerApplication("browser","fooledit/browser",BrowserObjectType.INSTANCE,BrowserObject.class,BrowserEditor.INSTANCE);
		Main.INSTANCE.getGlobalCommandRegistry().put("browser",new Command("browser",()->Main.INSTANCE.addAndShow(DataObjectRegistry.create(BrowserObjectType.INSTANCE)),NAME));
	}
	public static void onUnLoad(){
	}
	public static void onInstall(){
		Registry.providesDataObjectType(BrowserObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(BrowserEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(BrowserObject.class.getName(),NAME);
		Registry.providesApplication(APPLICATION_NAME,NAME);
		Registry.providesContentTypeLoader(CONTENT_TYPE_NAME,NAME);
		Registry.providesCommand(APPLICATION_NAME,NAME);
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
