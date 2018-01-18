/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
		System.out.println("World");
		FileSystemEditor.INSTANCE.getCommandRegistry().addChild("init-git",new Command("init-gui",()->{
			ObservableList<Path> paths=((FileSystemObject)Main.INSTANCE.getCurrentData()).getPaths();
			for(Path path:paths){
				try{
					Git.init().setGitDir(path.toFile()).call();
				}catch(GitAPIException ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
				}
			}
		},NAME));
		CoreModule.DYNAMIC_MENU_REGISTRY.addChild(APPLICATION_NAME,(items)->{
			ObservableList<Path> paths=((FileSystemObject)Main.INSTANCE.getCurrentData()).getPaths();
			items.add(createMenuItem("init-git","INIT"));
		});
		System.err.println("Hello");
	}
	private static MenuItem createMenuItem(String command,String name){
		MenuItem item=new MenuItem(MessageRegistry.getString(name,NAME));
		item.setOnAction((e)->{Main.INSTANCE.getCommandRegistry().get(command).accept(ScmNil.NIL);});
		return item;
	}
}
