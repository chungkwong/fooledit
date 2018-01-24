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
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.scene.control.*;
import org.eclipse.jgit.api.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class WorkingTreeItem extends TreeItem<Object> implements NavigationTreeItem{
	public WorkingTreeItem(Git git){
		super(git);
		getChildren().add(new LazySimpleTreeItem(()->{
			try{
				return git.status().call().getIgnoredNotInIndex().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList());
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				return Collections.emptyList();
			}
		},MessageRegistry.getString("IGNORED",GitModuleReal.NAME)));
		getChildren().add(new LazySimpleTreeItem(()->{
			try{
				return git.status().call().getUntracked().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList());
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				return Collections.emptyList();
			}
		},MessageRegistry.getString("UNTRACKED",GitModuleReal.NAME)));
		getChildren().add(new LazySimpleTreeItem(()->{
			try{
				return git.status().call().getConflicting().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList());
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				return Collections.emptyList();
			}
		},MessageRegistry.getString("CONFLICTING",GitModuleReal.NAME)));
		getChildren().add(new LazySimpleTreeItem(()->{
			try{
				return git.status().call().getMissing().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList());
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				return Collections.emptyList();
			}
		},MessageRegistry.getString("MISSING",GitModuleReal.NAME)));
		getChildren().add(new LazySimpleTreeItem(()->{
			try{
				return git.status().call().getModified().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList());
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				return Collections.emptyList();
			}
		},MessageRegistry.getString("MODIFIED",GitModuleReal.NAME)));
	}
	@Override
	public String toString(){
		return MessageRegistry.getString("STAGING AREA",GitModuleReal.NAME);
	}
	@Override
	public MenuItem[] getContextMenuItems(){
		return new MenuItem[0];
	}
}