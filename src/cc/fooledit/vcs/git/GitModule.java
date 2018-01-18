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
package cc.fooledit.vcs.git;
import cc.fooledit.*;
import cc.fooledit.core.*;
import cc.fooledit.editor.filesystem.*;
import java.net.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GitModule{
	public static final String NAME="vcs.git";
	public static final String APPLICATION_NAME="git";
	public static void onLoad() throws ReflectiveOperationException, MalformedURLException{
		URLClassLoader loader=new URLClassLoader(new URL[]{Main.INSTANCE.getFile("dist/vcs.git.jar",NAME).toURI().toURL()});
		loader.loadClass("cc.fooledit.vcs.git.GitModuleReal").getMethod("onLoad").invoke(null);
	}
	public static void onUnLoad(){

	}
	public static void onInstall(){
		Registry.providesDynamicMenu(APPLICATION_NAME,NAME);
		Registry.provides("init-git",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
	}
}
