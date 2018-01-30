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
import cc.fooledit.vcs.git.MenuItemBuilder;
import cc.fooledit.vcs.git.TreeItemBuilder;
import javafx.scene.control.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.lib.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BranchTreeItem extends LazySimpleTreeItem<Object>{
	public BranchTreeItem(Ref ref,Git git){
		super(ref,TreeItemBuilder.getTreeItemsSupplier(TreeItemBuilder.toTree(ref,git),git));
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
}
