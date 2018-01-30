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
import cc.fooledit.util.*;
import cc.fooledit.vcs.git.TreeItemBuilder;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javafx.scene.control.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.treewalk.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TreeItemBuilder{
	public static ThrowableSupplier<Collection<TreeItem<Object>>> getTreeItemsSupplier(ObjectId id,Git git){
		return ()->{
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
		};
	}
	public static ObjectId toTree(Ref ref,Git git){
		try{
			return git.getRepository().parseCommit(ref.getObjectId()).getTree();
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return null;
		}
	}
}
class DirectoryTreeItem extends LazySimpleTreeItem<Object>{
	private final String name;
	public DirectoryTreeItem(ObjectId id,String name,Git git){
		super(id,TreeItemBuilder.getTreeItemsSupplier(id,git));
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
