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
package cc.fooledit.vcs.git;
import cc.fooledit.*;
import cc.fooledit.core.*;
import cc.fooledit.editor.filesystem.*;
import cc.fooledit.vcs.git.Activator;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javafx.collections.*;
import javafx.scene.control.*;
import org.osgi.framework.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String APPLICATION_NAME="git";
	public static final String NAME=Activator.class.getPackage().getName();
	@Override
	public void start(BundleContext bc) throws Exception{
		Registry.providesDynamicMenu(APPLICATION_NAME,NAME);
		Registry.provides("git-init",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemViewer.class.getPackage().getName());
		Registry.provides("git-clone",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemViewer.class.getPackage().getName());
		Registry.provides("git-browse",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemViewer.class.getPackage().getName());
		Registry.providesDataObjectType(GitRepositoryObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(GitRepositoryEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(GitRepositoryObject.class.getName(),NAME);
		Registry.providesProtocol("git",NAME);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("directory/git",GitRepositoryObjectType.class.getName());
		DataObjectTypeRegistry.addDataObjectType(GitRepositoryObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(GitRepositoryEditor.INSTANCE,GitRepositoryObject.class);
		FileSystemEditor.INSTANCE.getCommandRegistry().put("git-init",GitRepositoryEditor.INSTANCE.getCommandRegistry().get("git-init"));
		FileSystemEditor.INSTANCE.getCommandRegistry().put("git-clone",GitRepositoryEditor.INSTANCE.getCommandRegistry().get("git-clone"));
		Argument dir=new Argument("DIRECTORY",GitRepositoryEditor::getGitDirectory);
		FileSystemEditor.INSTANCE.getCommandRegistry().put("git-browse",new Command("git-browse",Arrays.asList(dir),(params)->{
			Main.INSTANCE.addAndShow(DataObjectRegistry.readFrom(((File)params[0]).toURI().toURL()));
			return null;
		},NAME));
		CoreModule.DYNAMIC_MENU_REGISTRY.put(APPLICATION_NAME,(items)->{
			ObservableList<Path> paths=((FileSystemObject)Main.INSTANCE.getCurrentData()).getPaths();
			items.add(createMenuItem("git-init","INIT"));
			items.add(createMenuItem("git-clone","CLONE"));
			items.add(createMenuItem("git-browse","BROWSE"));
		});
		CoreModule.PROTOCOL_REGISTRY.put("git",new GitStreamHandler());
		ContentTypeHelper.getURL_GUESSER().registerPathPattern("^.*[/\\\\]\\.git$","directory/git");
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
	private static MenuItem createMenuItem(String command,String name){
		MenuItem item=new MenuItem(MessageRegistry.getString(name,NAME));
		item.setOnAction((e)->TaskManager.executeCommand(Main.INSTANCE.getCommand(command)));
		return item;
	}
}
