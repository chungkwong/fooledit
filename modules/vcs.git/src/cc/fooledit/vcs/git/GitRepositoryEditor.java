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
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import com.github.chungkwong.jschememin.type.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javafx.scene.*;
import org.eclipse.jgit.api.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GitRepositoryEditor implements DataEditor<GitRepositoryObject>{
	private final MenuRegistry menuRegistry=Registry.ROOT.registerMenu(GitModuleReal.NAME);
	private final RegistryNode<String,Command,String> commandRegistry=Registry.ROOT.registerCommand(GitModuleReal.NAME);
	private final NavigableRegistryNode<String,String,String> keymapRegistry=Registry.ROOT.registerKeymap(GitModuleReal.NAME);
	public static GitRepositoryEditor INSTANCE=new GitRepositoryEditor();
	private GitRepositoryEditor(){
		addCommand("git-push",Arrays.asList(),(args)->{return null;});
		addCommand("git-pull",Arrays.asList(),(args)->{return null;});
		addCommand("git-fetch",Arrays.asList(),(args)->{return null;});
		addCommand("git-checkout",Arrays.asList(),(args)->{return null;});
		addCommand("git-revert",Arrays.asList(),(args)->{return null;});
		addCommand("git-merge",Arrays.asList(),(args)->{return null;});
		addCommand("git-add-branch",Arrays.asList(),(args)->{return null;});
		addCommand("git-branch-delete",Arrays.asList(),(args)->{return null;});
		addCommand("git-branch-rename",Arrays.asList(),(args)->{return null;});
		addCommand("git-add",Arrays.asList(),(args)->{return null;});
		addCommand("git-remove",Arrays.asList(),(args)->{return null;});
		addCommand("git-add-tag",Arrays.asList(),(args)->{return null;});
		addCommand("git-remove-tag",Arrays.asList(),(args)->{return null;});
		addCommand("git-add-remote",Arrays.asList(),(args)->{return null;});
		addCommand("git-remote-delete",Arrays.asList(),(args)->{return null;});
		addCommand("git-remote-rename",Arrays.asList(),(args)->{return null;});
		addCommand("git-remote-set-url",Arrays.asList(),(args)->{return null;});
		addCommand("git-config-remote",Arrays.asList(),(args)->{return null;});
		addCommand("git-commit",Arrays.asList(),(args)->{return null;});
		addCommand("git-init",Arrays.asList(),(args)->{return null;});
		addCommand("git-clone",Arrays.asList(),(args)->{return null;});
		addCommand("git-config",Arrays.asList(),(args)->{return null;});
		addCommand("git-blame",Arrays.asList(),(args)->{return null;});
	}
	private void addCommand(String name,List<String> args,ThrowableFunction<ScmPairOrNil,ScmObject> proc){
		commandRegistry.addChild(name,new Command(name,proc,GitModuleReal.NAME));
	}
	@Override
	public Node edit(GitRepositoryObject data,Object remark,RegistryNode<String,Object,String> meta){
		return new GitRepositoryViewer(data.getRepository());
	}
	@Override
	public RegistryNode<String,Command,String> getCommandRegistry(){
		return commandRegistry;
	}
	@Override
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
	@Override
	public NavigableRegistryNode<String,String,String> getKeymapRegistry(){
		return keymapRegistry;
	}
	@Override
	public Object getRemark(Node node){
		return null;
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("GIT_REPOSITORY_VIEWER",GitModuleReal.NAME);
	}
	private static Git getGit() throws IOException{
		DataObject data=Main.INSTANCE.getCurrentData();
		if(data instanceof GitRepositoryObject){
			return ((GitRepositoryObject)data).getRepository();
		}else if(data instanceof FileSystemObject){
			Iterator<Path> iterator=((FileSystemObject)data).getPaths().iterator();
			if(iterator.hasNext()){
				File dir=findGitDirectory(iterator.next().toFile());
				while(iterator.hasNext()){
					if(!findGitDirectory(iterator.next().toFile()).equals(dir)){
						dir=null;
						break;
					}
				}
				return Git.open(dir);
			}
		}
		throw new RuntimeException();
	}
	private static File findGitDirectory(File f){
		while(!new File(f,".git").exists()){
			f=f.getParentFile();
			if(f==null)
				return null;
		}
		return f;
	}
}
