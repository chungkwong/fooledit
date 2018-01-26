/*
 * Copyright (C) 2016,2018 Chan Chung Kwong <1m02math@126.com>
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
import cc.fooledit.vcs.git.MenuItemBuilder;
import java.util.*;
import java.util.logging.*;
import javafx.scene.control.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.errors.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.treewalk.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CommitTreeItem extends LazySimpleTreeItem<Object>{
	private final Git git;
	public CommitTreeItem(RevCommit rev,Git git){
		super(()->{
			LinkedList<TreeItem<Object>> list=new LinkedList<>();
			TreeWalk walk=new TreeWalk(git.getRepository());
			walk.addTree(rev.getTree());
			while(walk.next()){
				if(walk.isSubtree()){
					list.add(new DirectoryTreeItem(walk.getObjectId(0),walk.getNameString(),git));
				}else{
					list.add(new FileTreeItem(walk.getObjectId(0),walk.getNameString()));
				}
			}
			return list;
		},rev);
		this.git=git;
	}
	@Override
	public String toString(){
		try{
			ObjectId id=((RevCommit)getValue()).getId();
			return ((Git)getParent().getParent().getValue()).nameRev().add(id).call().get(id);
		}catch(MissingObjectException|JGitInternalException|GitAPIException ex){
			Logger.getLogger(CommitTreeItem.class.getName()).log(Level.SEVERE,null,ex);
		}
		return ((RevCommit)getValue()).getName();
	}
	@Override
	public MenuItem[] getContextMenuItems(){
		return new MenuItem[]{
			MenuItemBuilder.build("CHECKOUT",(e)->GitCommands.execute("git-checkout")),
			MenuItemBuilder.build("REVERT",(e)->GitCommands.execute("git-revert")),
			MenuItemBuilder.build("TAG",(e)->GitCommands.execute("git-tag"))
		};
	}
	private void gitCheckout(){
		try{
			((Git)getParent().getParent().getValue()).checkout().setName(((RevCommit)getValue()).getName()).call();
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private void gitRevert(){
		try{
			RevCommit rev=((Git)getParent().getParent().getValue()).revert().include((RevCommit)getValue()).call();
			getParent().getChildren().add(new CommitTreeItem(rev,git));
		}catch(Exception ex){
			Logger.getLogger(BranchTreeItem.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	private void gitTag(){
		TextInputDialog dialog=new TextInputDialog();
		dialog.setTitle(MessageRegistry.getString("CHOOSE NAME FOR THE TAG",GitModuleReal.NAME));
		dialog.setHeaderText(MessageRegistry.getString("ENTER THE NAME OF THE TAG:",GitModuleReal.NAME));
		Optional<String> name=dialog.showAndWait();
		if(name.isPresent())
			try{
				Ref tag=((Git)getParent().getParent().getValue()).tag().setName(name.get()).setObjectId((RevCommit)getValue()).call();
//				getParent().getParent().getChildren().filtered(item->item instanceof TagListTreeItem).
//					forEach((item)->item.getChildren().add(new TagTreeItem(tag)));
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
	}
}
class DirectoryTreeItem extends LazySimpleTreeItem<Object>{
	private final String name;
	public DirectoryTreeItem(ObjectId id,String name,Git git){
		super(()->{
			LinkedList<TreeItem<Object>> list=new LinkedList<>();
			TreeWalk walk=new TreeWalk(git.getRepository());
			walk.addTree(id);
			while(walk.next()){
				if(walk.isSubtree()){
					list.add(new DirectoryTreeItem(walk.getObjectId(0),walk.getNameString(),git));
				}else{
					list.add(new FileTreeItem(walk.getObjectId(0),walk.getNameString()));
				}
			}
			return list;
		},id);
		this.name=name;
	}
	@Override
	public String toString(){
		return name;
	}
}
class FileTreeItem extends SimpleTreeItem<Object>{
	private final String name;
	public FileTreeItem(ObjectId id,String name){
		super(id);
		this.name=name;
	}
	@Override
	public String toString(){
		return name;
	}
}