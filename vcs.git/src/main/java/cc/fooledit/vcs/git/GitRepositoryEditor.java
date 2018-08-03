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
import cc.fooledit.core.*;
import cc.fooledit.editor.filesystem.*;
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javafx.scene.*;
import javafx.scene.control.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.revwalk.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GitRepositoryEditor implements DataEditor<GitRepositoryObject>{
	private final MenuRegistry menuRegistry=Registry.ROOT.registerMenu(Activator.class);
	private final RegistryNode<String,Command> commandRegistry=Registry.ROOT.registerCommand(Activator.NAME);
	private final NavigableRegistryNode<String,String> keymapRegistry=Registry.ROOT.registerKeymap(Activator.class);
	public static GitRepositoryEditor INSTANCE=new GitRepositoryEditor();
	private GitRepositoryEditor(){
		Argument git=new Argument("GIT",GitRepositoryEditor::getGit);
		Argument remote=new Argument("REMOTE",GitRepositoryEditor::getSelectedItem);
		Argument branch=new Argument("BRANCH",GitRepositoryEditor::getSelectedItem);
		Argument msg=new Argument("MESSAGE");
		Argument uri=new Argument("URI");
		Argument name=new Argument("NAME");
		Argument user=new Argument("USER");
		Argument pass=new Argument("PASSWORD");
		Argument tag=new Argument("TAG",GitRepositoryEditor::getSelectedItem);
		Argument dir=new Argument("DIRECTORY",GitRepositoryEditor::getDirectory);
		Argument file=new Argument("FILE",GitRepositoryEditor::getSelectedItem);
		Argument commit=new Argument("COMMIT",GitRepositoryEditor::getSelectedItem);
		Argument id=new Argument("ID",GitRepositoryEditor::getSelectedItem);
		Argument commitOrBranch=new Argument("COMMIT_OR_BRANCH",GitRepositoryEditor::getSelectedItem);
		addCommand("git-push",Arrays.asList(remote,git),(args)->{
			return GitCommands.push(args[0],(String)args[1],(String)args[2],(Git)args[3]);
		});
		addCommand("git-pull",Arrays.asList(remote,git),(args)->{
			return GitCommands.pull(args[0],(Git)args[1]);
		});
		addCommand("git-fetch",Arrays.asList(remote,git),(args)->{
			return GitCommands.fetch(args[0],(Git)args[1]);
		});
		addCommand("git-view",Arrays.asList(id,git),(args)->{
			return GitCommands.view(args[0],(Git)args[1]);
		});
		addCommand("git-checkout",Arrays.asList(commitOrBranch,git),(args)->{
			return GitCommands.checkout(args[0],(Git)args[1]);
		});
		addCommand("git-revert",Arrays.asList(commitOrBranch,git),(args)->{
			return GitCommands.revert(args[0],(Git)args[1]);
		});
		addCommand("git-merge",Arrays.asList(commitOrBranch,git),(args)->{
			return GitCommands.merge(args[0],(Git)args[1]);
		});
		addCommand("git-branch-add",Arrays.asList(name,git),(args)->{
			return GitCommands.addBranch(args[0].toString(),(Git)args[1]);
		});
		addCommand("git-branch-delete",Arrays.asList(branch,git),(args)->{
			return GitCommands.deleteBranch(args[0],(Git)args[1]);
		});
		addCommand("git-branch-rename",Arrays.asList(branch,name,git),(args)->{
			return GitCommands.renameBranch(args[0],args[1].toString(),(Git)args[2]);
		});
		addCommand("git-add",Arrays.asList(file,git),(args)->{
			return GitCommands.add(args[0].toString(),(Git)args[1]);
		});
		addCommand("git-remove",Arrays.asList(file,git),(args)->{
			return GitCommands.remove(args[0].toString(),(Git)args[1]);
		});
		addCommand("git-tag-add",Arrays.asList(name,commit,git),(args)->{
			return GitCommands.addTag(args[0].toString(),(RevObject)args[1],(Git)args[2]);
		});
		addCommand("git-tag-delete",Arrays.asList(tag,git),(args)->{
			return GitCommands.removeTag(args[0],(Git)args[1]);
		});
		addCommand("git-remote-add",Arrays.asList(uri,name,git),(args)->{
			return GitCommands.addRemote(args[0].toString(),args[1].toString(),(Git)args[2]);
		});
		addCommand("git-remote-delete",Arrays.asList(name,git),(args)->{
			return GitCommands.deleteRemote(args[0],(Git)args[1]);
		});
		addCommand("git-remote-set-url",Arrays.asList(remote,uri,git),(args)->{
			return GitCommands.setRemote(args[0],args[1].toString(),(Git)args[2]);
		});
		addCommand("git-commit",Arrays.asList(git),(args)->{
			return GitCommands.commit((Git)args[0]);
		});
		addCommand("git-gc",Arrays.asList(git),(args)->{
			return GitCommands.gc((Git)args[0]);
		});
		addCommand("git-init",Arrays.asList(dir),(args)->{
			return GitCommands.init((File)args[0]);
		});
		addCommand("git-clone",Arrays.asList(uri,dir),(args)->{
			return GitCommands.clone((String)args[0],(File)args[1]);
		});
		addCommand("git-config",Arrays.asList(),(args)->{
			return null;
		});//TODO
		addCommand("git-blame",Arrays.asList(),(args)->{
			return null;
		});//TODO
	}
	private void addCommand(String name,List<Argument> args,ThrowableVarargsFunction<Object,Object> proc){
		commandRegistry.put(name,new Command(name,args,proc,Activator.class));
	}
	@Override
	public Node edit(GitRepositoryObject data,Object remark,RegistryNode<String,Object> meta){
		return new GitRepositoryViewer(data.getRepository());
	}
	@Override
	public RegistryNode<String,Command> getCommandRegistry(){
		return commandRegistry;
	}
	@Override
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
	@Override
	public NavigableRegistryNode<String,String> getKeymapRegistry(){
		return keymapRegistry;
	}
	@Override
	public Object getRemark(Node node){
		return null;
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("GIT_REPOSITORY_VIEWER",Activator.class);
	}
	static Git getGit() throws Exception{
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
				if(dir!=null){
					return Git.open(dir);
				}
			}
		}
		throw new Exception();
	}
	static File getGitDirectory() throws Exception{
		DataObject data=Main.INSTANCE.getCurrentData();
		if(data instanceof FileSystemObject){
			Iterator<Path> iterator=((FileSystemObject)data).getPaths().iterator();
			if(iterator.hasNext()){
				File dir=findGitDirectory(iterator.next().toFile());
				while(dir!=null&&iterator.hasNext()){
					if(!dir.equals(findGitDirectory(iterator.next().toFile()))){
						dir=null;
						break;
					}
				}
				if(dir!=null){
					return dir;
				}
			}
		}
		throw new Exception();
	}
	private static File findGitDirectory(File f){
		while(!new File(f,".git").exists()){
			f=f.getParentFile();
			if(f==null){
				return null;
			}
		}
		return f;
	}
	static File getDirectory() throws Exception{
		DataObject obj=Main.INSTANCE.getCurrentData();
		if(obj instanceof FileSystemObject){
			Iterator<Path> iter=((FileSystemObject)obj).getPaths().iterator();
			if(iter.hasNext()){
				File dir=findDirectory(iter.next().toFile());
				while(iter.hasNext()){
					dir=gcd(dir,findDirectory(iter.next().toFile()));
				}
				if(dir!=null){
					return dir;
				}
			}
		}
		throw new Exception();
	}
	private static File findDirectory(File f){
		while(f!=null&&!f.isDirectory()){
			f=f.getParentFile();
			if(f==null){
				return null;
			}
		}
		return f;
	}
	private static File gcd(File f,File g){
		if(f==null||g==null){
			return null;
		}
		Path p=f.toPath();
		Path q=g.toPath();
		if(p.getNameCount()<q.getNameCount()){
			Path tmp=p;
			p=q;
			q=tmp;
		}
		while(!p.startsWith(q)){
			q=q.getParent();
			if(q==null){
				return null;
			}
		}
		return q.toFile();
	}
	private static Object getSelectedItem() throws Exception{
		Node node=Main.INSTANCE.getCurrentNode();
		if(node instanceof GitRepositoryViewer){
			TreeTableView.TreeTableViewSelectionModel<Object> model=((TreeTableView<Object>)((GitRepositoryViewer)node).getCenter()).getSelectionModel();
			if(!model.isEmpty()){
				return model.getSelectedItem().getValue();
			}
		}
		throw new Exception();
	}
}
