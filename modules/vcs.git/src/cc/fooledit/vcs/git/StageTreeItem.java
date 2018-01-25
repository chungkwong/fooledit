/*
 * Copyright (C) 2016 Chan Chung Kwong <1m02math@126.com>
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
import java.util.*;
import java.util.stream.*;
import javafx.scene.control.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.dircache.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class StageTreeItem extends TreeItem<Object> implements NavigationTreeItem{
	public StageTreeItem(Git git){
		super(git);
		getChildren().add(new LazySimpleTreeItem(()->git.status().call().getAdded().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList()),
				MessageRegistry.getString("ADDED",GitModuleReal.NAME)));
		getChildren().add(new LazySimpleTreeItem(()->git.status().call().getRemoved().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList()),
				MessageRegistry.getString("REMOVED",GitModuleReal.NAME)));
		getChildren().add(new LazySimpleTreeItem(()->git.status().call().getChanged().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList()),
				MessageRegistry.getString("CHANGED",GitModuleReal.NAME)));
		getChildren().add(new LazySimpleTreeItem(()->{
			LinkedList<TreeItem<String>> files=new LinkedList<>();
			DirCache cache=git.getRepository().readDirCache();
			for(int i=0;i<cache.getEntryCount();i++)
				files.add(new SimpleTreeItem<>(cache.getEntry(i).getPathString(),new MenuItem[]{getRemoveMenuItem(),getBlameMenuItem()}));
			return files;
		},MessageRegistry.getString("ALL",GitModuleReal.NAME)));
	}
	@Override
	public String toString(){
		return MessageRegistry.getString("STAGING AREA",GitModuleReal.NAME);
	}
	@Override
	public MenuItem[] getContextMenuItems(){
		return new MenuItem[]{getCommitMenuItem()};
	}
}