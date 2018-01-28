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
import org.eclipse.jgit.revwalk.*;
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
		Argument git=new Argument("GIT",GitRepositoryEditor::getGit);
		Argument remote=new Argument("REMOTE",GitRepositoryEditor::getRemote);
		Argument branch=new Argument("REMOTE",GitRepositoryEditor::getBranch);
		Argument msg=new Argument("MESSAGE");
		Argument uri=new Argument("URI");
		Argument name=new Argument("NAME");
		Argument tag=new Argument("TAG",GitRepositoryEditor::getTag);
		Argument dir=new Argument("DIRECTORY",GitRepositoryEditor::getDirectory);
		Argument file=new Argument("DIRECTORY",GitRepositoryEditor::getFile);
		Argument commit=new Argument("COMMIT",GitRepositoryEditor::getCommit);
		addCommand("git-push",Arrays.asList(),(args)->{
			return SchemeConverter.toScheme(GitCommands.push(SchemeConverter.toJava(ScmList.first(args)).toString(),(Git)SchemeConverter.toJava(ScmList.second(args))));
		});
		addCommand("git-pull",Arrays.asList(),(args)->{
			return SchemeConverter.toScheme(GitCommands.pull(SchemeConverter.toJava(ScmList.first(args)).toString(),(Git)SchemeConverter.toJava(ScmList.second(args))));
		});
		addCommand("git-fetch",Arrays.asList(),(args)->{
			return SchemeConverter.toScheme(GitCommands.fetch(SchemeConverter.toJava(ScmList.first(args)).toString(),(Git)SchemeConverter.toJava(ScmList.second(args))));
		});
		addCommand("git-checkout",Arrays.asList(),(args)->{return null;});
		addCommand("git-revert",Arrays.asList(),(args)->{return null;});
		addCommand("git-merge",Arrays.asList(),(args)->{return null;});
		addCommand("git-branch-add",Arrays.asList(name,git),(args)->{
			return SchemeConverter.toScheme(GitCommands.addBranch(SchemeConverter.toJava(ScmList.first(args)).toString(),(Git)SchemeConverter.toJava(ScmList.second(args))));
		});
		addCommand("git-branch-delete",Arrays.asList(branch,git),(args)->{
			return SchemeConverter.toScheme(GitCommands.deleteBranch(SchemeConverter.toJava(ScmList.first(args)).toString(),(Git)SchemeConverter.toJava(ScmList.second(args))));
		});
		addCommand("git-branch-rename",Arrays.asList(branch,name,git),(args)->{
			return SchemeConverter.toScheme(GitCommands.renameBranch(SchemeConverter.toJava(ScmList.first(args)).toString(),SchemeConverter.toJava(ScmList.second(args)).toString(),(Git)SchemeConverter.toJava(ScmList.third(args))));
		});
		addCommand("git-add",Arrays.asList(file,git),(args)->{
			return SchemeConverter.toScheme(GitCommands.add(SchemeConverter.toJava(ScmList.first(args)).toString(),(Git)SchemeConverter.toJava(ScmList.second(args))));
		});
		addCommand("git-remove",Arrays.asList(file,git),(args)->{
			return SchemeConverter.toScheme(GitCommands.remove(SchemeConverter.toJava(ScmList.first(args)).toString(),(Git)SchemeConverter.toJava(ScmList.second(args))));
		});
		addCommand("git-tag-add",Arrays.asList(name,commit,git),(args)->{
			return SchemeConverter.toScheme(GitCommands.addTag(SchemeConverter.toJava(ScmList.first(args)).toString(),(RevObject)SchemeConverter.toJava(ScmList.second(args)),(Git)SchemeConverter.toJava(ScmList.third(args))));
		});
		addCommand("git-tag-remove",Arrays.asList(tag,git),(args)->{
			return SchemeConverter.toScheme(GitCommands.removeTag(SchemeConverter.toJava(ScmList.first(args)).toString(),(Git)SchemeConverter.toJava(ScmList.second(args))));
		});
		addCommand("git-remote-add",Arrays.asList(uri,name,git),(args)->{
			return SchemeConverter.toScheme(GitCommands.addRemote(SchemeConverter.toJava(ScmList.first(args)).toString(),SchemeConverter.toJava(ScmList.second(args)).toString(),(Git)SchemeConverter.toJava(ScmList.third(args))));
		});
		addCommand("git-remote-delete",Arrays.asList(name,git),(args)->{
			return SchemeConverter.toScheme(GitCommands.deleteRemote(SchemeConverter.toJava(ScmList.first(args)).toString(),(Git)SchemeConverter.toJava(ScmList.second(args))));
		});
		addCommand("git-remote-set-url",Arrays.asList(remote,uri,git),(args)->{
			return SchemeConverter.toScheme(GitCommands.setRemote(SchemeConverter.toJava(ScmList.first(args)).toString(),SchemeConverter.toJava(ScmList.second(args)).toString(),(Git)SchemeConverter.toJava(ScmList.third(args))));
		});
		addCommand("git-commit",Arrays.asList(git),(args)->{
			return SchemeConverter.toScheme(GitCommands.commit((Git)SchemeConverter.toJava(ScmList.first(args))));
		});
		addCommand("git-gc",Arrays.asList(git),(args)->{
			return SchemeConverter.toScheme(GitCommands.gc((Git)SchemeConverter.toJava(ScmList.first(args))));
		});
		addCommand("git-init",Arrays.asList(dir),(args)->{
			return SchemeConverter.toScheme(GitCommands.init((File)SchemeConverter.toJava(ScmList.first(args))));
		});
		addCommand("git-clone",Arrays.asList(uri,dir),(args)->{
			return SchemeConverter.toScheme(GitCommands.clone(SchemeConverter.toJava(ScmList.first(args)).toString(),(File)SchemeConverter.toJava(ScmList.second(args))));
		});
		addCommand("git-config",Arrays.asList(),(args)->{return null;});
		addCommand("git-blame",Arrays.asList(),(args)->{return null;});
	}
	private void addCommand(String name,List<Argument> args,ThrowableFunction<ScmPairOrNil,ScmObject> proc){
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
	private static Git getGit() throws Exception{
		DataObject data=Main.INSTANCE.getCurrentData();
		if(data instanceof GitRepositoryObject){
			return ((GitRepositoryObject)data).getRepository();
		}else if(data instanceof FileSystemObject){
			Iterator<Path> iterator=((FileSystemObject)data).getPaths().iterator();
			if(iterator.hasNext()){
				File dir=findGitDirectory(iterator.next().toFile());
				while(dir!=null&&iterator.hasNext()){
					if(!dir.equals(findGitDirectory(iterator.next().toFile()))){
						dir=null;
						break;
					}
				}
				if(dir!=null)
					return Git.open(dir);
			}
		}
		throw new Exception();
	}
	private static File findGitDirectory(File f){
		while(!new File(f,".git").exists()){
			f=f.getParentFile();
			if(f==null)
				return null;
		}
		return f;
	}
	private static String getRemote() throws Exception{
		Node node=Main.INSTANCE.getCurrentNode();
		if(node instanceof GitRepositoryViewer){

		}
		throw new Exception();
	}
	private static String getBranch() throws Exception{
		Node node=Main.INSTANCE.getCurrentNode();
		if(node instanceof GitRepositoryViewer){

		}
		throw new Exception();
	}
	private static RevObject getCommit() throws Exception{
		Node node=Main.INSTANCE.getCurrentNode();
		if(node instanceof GitRepositoryViewer){

		}
		throw new Exception();
	}
	private static File getDirectory() throws Exception{
		Node node=Main.INSTANCE.getCurrentNode();
		if(node instanceof GitRepositoryViewer){

		}
		throw new Exception();
	}
	private static String getFile() throws Exception{
		Node node=Main.INSTANCE.getCurrentNode();
		if(node instanceof GitRepositoryViewer){

		}
		throw new Exception();
	}
	private static String getTag() throws Exception{
		Node node=Main.INSTANCE.getCurrentNode();
		if(node instanceof GitRepositoryViewer){

		}
		throw new Exception();
	}
}