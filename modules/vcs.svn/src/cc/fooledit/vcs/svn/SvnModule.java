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
package cc.fooledit.vcs.svn;
import cc.fooledit.*;
import cc.fooledit.core.*;
import cc.fooledit.editor.filesystem.*;
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import com.github.chungkwong.jschememin.type.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import javafx.collections.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SvnModule{
	public static final String NAME="vcs.svn";
	public static final String APPLICATION_NAME="svn";
	public static final String SETTINGS_REGISTRY_NAME="settings";
	public static final RegistryNode<String,Object,String> SETTINGS_REGISTRY=
			(RegistryNode<String,Object,String>)Registry.ROOT.getOrCreateChild(SvnModule.NAME).getOrCreateChild(SETTINGS_REGISTRY_NAME);
	private static final RegistryNode<String,Command,String> commands=Registry.ROOT.registerCommand(NAME);
	public static void onLoad() throws ClassNotFoundException, MalformedURLException{
		DataObjectTypeRegistry.addDataObjectType(SvnRepositoryObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(SvnRepositoryEditor.INSTANCE,SvnRepositoryObject.class);

		Argument makeParent=createArgument("MAKE_PARENT");
		Argument properties=createArgument("PROPERTIES");
		Argument breakLock=createArgument("BREAK_LOCK");
		Argument stealLock=createArgument("STEAL_LOCK");
		Argument force=createArgument("FORCE");
		Argument dryrun=createArgument("DRY_RUN");
		Argument deleteFile=createArgument("DELETE_FILE");
		Argument url=new Argument("URL");
		Argument name=new Argument("NAME");
		Argument msg=new Argument("MESSAGE");
		Argument files=new Argument("FILES",()->{
			return ((FileSystemViewer)Main.INSTANCE.getCurrentNode()).getSelectedPaths();
		});


		FileSystemEditor.INSTANCE.getCommandRegistry().addChild("svn-init",SvnRepositoryEditor.INSTANCE.getCommandRegistry().getChild("svn-init"));
//		Argument dir=new Argument("DIRECTORY",SvnRepositoryEditor::getSvnDirectory);
//		FileSystemEditor.INSTANCE.getCommandRegistry().addChild("svn-browse",new Command("svn-browse",Arrays.asList(dir),(params)->{
//			Main.INSTANCE.addAndShow(DataObjectRegistry.readFrom(((File)SchemeConverter.toJava(ScmList.first(params))).toURI().toURL()));
//			return null;
//		},NAME));
		CoreModule.DYNAMIC_MENU_REGISTRY.addChild(APPLICATION_NAME,(items)->{
			ObservableList<Path> paths=((FileSystemObject)Main.INSTANCE.getCurrentData()).getPaths();
			items.add(createMenuItem("svn-init","INIT"));
			items.add(createMenuItem("svn-clone","CLONE"));
			items.add(createMenuItem("svn-browse","BROWSE"));
		});
//		CoreModule.PROTOCOL_REGISTRY.addChild("svn",new SvnStreamHandler());
		ContentTypeHelper.getURL_GUESSER().registerPathPattern("^.*[/\\\\]\\.svn$","directory/svn");
	}
	private static MenuItem createMenuItem(String command,String name){
		MenuItem item=new MenuItem(MessageRegistry.getString(name,NAME));
		item.setOnAction((e)->{Main.INSTANCE.getCommandRegistry().get(command).accept(ScmNil.NIL);});
		return item;
	}
	public static void onUnLoad(){

	}
	public static void onInstall(){
		Registry.providesDynamicMenu(APPLICATION_NAME,NAME);
		Registry.provides("svn-auth",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-add",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-blame",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-cat",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-changelist",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-checkout",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-cleanup",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-commit",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-copy",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-delete",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-diff",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-export",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-import",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-info",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-list",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-lock",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-log",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-merge",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-mergeinfo",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-mkdir",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-move",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-patch",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-propdel",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-propedit",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-propget",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-proplist",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-propset",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-relocate",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-resolve",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-resolved",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-revert",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-status",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-switch",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-unlock",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-update",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.provides("svn-upgrade",NAME,CoreModule.COMMAND_REGISTRY_NAME,FileSystemModule.NAME);
		Registry.providesDataObjectType(SvnRepositoryObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(SvnRepositoryEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(SvnRepositoryObject.class.getName(),NAME);
		Registry.providesProtocol("svn",NAME);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.addChild("directory/svn",SvnRepositoryObjectType.class.getName());
	}
	private static Argument createArgument(String name){
		return new Argument(MessageRegistry.getString(name,NAME),()->SETTINGS_REGISTRY.getChild(name));
	}
	private void addCommand(String name,List<Argument> args,ThrowableFunction<ScmPairOrNil,ScmObject> proc){
		commands.addChild(name,new Command(name,args,proc,NAME));
	}

}
