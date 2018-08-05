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
package cc.fooledit.editor.filesystem;
import cc.fooledit.core.*;
import cc.fooledit.editor.filesystem.Activator;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String APPLICATION_NAME="filesystem";
	public static final String CONTENT_TYPE_NAME="fooledit/filesystem";
	public static void onLoad(){
		Registry.registerApplication(APPLICATION_NAME,CONTENT_TYPE_NAME,FileSystemObjectType.INSTANCE,FileSystemObject.class,FileSystemEditor.INSTANCE);
		addCommand("new",()->FileCommands.create());
		addCommand("open-file",()->FileCommands.open());
		addCommand("open-url",()->FileCommands.openUrl());
		addCommand("save",()->FileCommands.save());
		addCommand("save-as",()->FileCommands.saveAs());
	}
	private static void addCommand(String name,Runnable action){
		CoreModule.COMMAND_REGISTRY.put(name,new Command(name,action,Activator.class));
	}
	public static void onUnLoad(){
	}
	public static void onInstall(){
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
