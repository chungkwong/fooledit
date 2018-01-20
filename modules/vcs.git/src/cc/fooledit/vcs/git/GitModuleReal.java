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
import com.github.chungkwong.jschememin.type.*;
import java.net.*;
import java.nio.file.*;
import java.util.logging.*;
import javafx.collections.*;
import javafx.scene.control.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GitModuleReal{
	public static final String NAME="vcs.git";
	public static final String APPLICATION_NAME="git";
	public static void onLoad() throws ClassNotFoundException, MalformedURLException{
		org.apache.log4j.BasicConfigurator.configure();
		FileSystemEditor.INSTANCE.getCommandRegistry().addChild("init-git",new Command("init-gui",()->{
			ObservableList<Path> paths=((FileSystemObject)Main.INSTANCE.getCurrentData()).getPaths();
			for(Path path:paths){
				try{
					Git.init().setDirectory(path.toFile()).call();
				}catch(GitAPIException ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
				}
			}
		},NAME));
		CoreModule.DYNAMIC_MENU_REGISTRY.addChild(APPLICATION_NAME,(items)->{
			ObservableList<Path> paths=((FileSystemObject)Main.INSTANCE.getCurrentData()).getPaths();
			items.add(createMenuItem("init-git","INIT"));
		});
	}
	private static MenuItem createMenuItem(String command,String name){
		MenuItem item=new MenuItem(MessageRegistry.getString(name,NAME));
		item.setOnAction((e)->{Main.INSTANCE.getCommandRegistry().get(command).accept(ScmNil.NIL);});
		return item;
	}
}
