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
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BranchTreeItem extends TreeItem<Object> implements NavigationTreeItem{
	public BranchTreeItem(Ref ref){
		super(ref);
	}
	@Override
	public String toString(){
		return ((Ref)getValue()).getName();
	}
	@Override
	public MenuItem[] getContextMenuItems(){
		return new MenuItem[]{
			MenuItemBuilder.build("CHECKOUT",(e)->GitCommands.execute("git-checkout")),
			MenuItemBuilder.build("REVERT",(e)->GitCommands.execute("git-revert")),
			MenuItemBuilder.build("MERGE",(e)->GitCommands.execute("git-merge")),
			MenuItemBuilder.build("REMOVE BRANCH",(e)->GitCommands.execute("git-branch-delete")),
			MenuItemBuilder.build("RENAME BRANCH",(e)->GitCommands.execute("git-branch-rename"))
		};
	}
	private void gitMerge(){
		try{
			MergeResult result=((Git)getParent().getParent().getValue()).merge().include((Ref)getValue()).call();
			if(result.getMergeStatus().equals(MergeResult.MergeStatus.MERGED)){
				RevCommit commit=((Git)getParent().getParent().getValue()).log().addRange(result.getNewHead(),result.getNewHead()).call().iterator().next();
//				getParent().getParent().getChildren().filtered(item->item instanceof LocalTreeItem).
//					forEach((item)->item.getChildren().add(new CommitTreeItem(commit)));
			}else{
				new Alert(Alert.AlertType.INFORMATION,result.getMergeStatus().toString(),ButtonType.CLOSE).show();
			}
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private void gitCheckout(){
		try{
			((Git)getParent().getParent().getValue()).checkout().setName(((Ref)getValue()).getName()).call();
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private void gitBranchRename(){
		TextInputDialog branchDialog=new TextInputDialog();
		branchDialog.setTitle(MessageRegistry.getString("CHOOSE A NEW NAME FOR THE BRANCH",GitModuleReal.NAME));
		branchDialog.setHeaderText(MessageRegistry.getString("ENTER THE NEW NAME OF THE BRANCH:",GitModuleReal.NAME));
		Optional<String> name=branchDialog.showAndWait();
		if(name.isPresent())
			try{
				setValue(((Git)getParent().getParent().getValue()).branchRename().setOldName(((Ref)getValue()).getName()).setNewName(name.get()).call());
			}catch(GitAPIException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
	}
	private void gitBranchRemove(){
		try{
			((Git)getParent().getParent().getValue()).branchDelete().setBranchNames(((Ref)getValue()).getName()).call();
			getParent().getChildren().remove(this);
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private void gitRevert(){
		try{
			RevCommit rev=((Git)getParent().getParent().getValue()).revert().include((Ref)getValue()).call();
//			getParent().getParent().getChildren().filtered(item->item instanceof LocalTreeItem).
//					forEach((item)->item.getChildren().add(new CommitTreeItem(rev)));
		}catch(Exception ex){
			Logger.getLogger(BranchTreeItem.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
}
