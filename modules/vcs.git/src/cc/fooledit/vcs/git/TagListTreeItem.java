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
import javafx.scene.control.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TagListTreeItem extends TreeItem<Object> implements NavigationTreeItem{
	public TagListTreeItem(Git git) throws GitAPIException{
		super(MessageRegistry.getString("TAG",GitModuleReal.NAME));
		for(Ref ref:git.tagList().call())
			getChildren().add(new TagTreeItem(ref));
	}
	@Override
	public String toString(){
		return MessageRegistry.getString("TAG",GitModuleReal.NAME);
	}
	@Override
	public MenuItem[] getContextMenuItems(){
		return new MenuItem[0];
	}
}