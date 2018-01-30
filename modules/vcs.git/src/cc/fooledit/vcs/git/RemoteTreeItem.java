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
import java.util.stream.*;
import javafx.scene.control.*;
import org.eclipse.jgit.transport.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class RemoteTreeItem extends TreeItem<Object> implements NavigationTreeItem{
	public RemoteTreeItem(RemoteConfig ref){
		super(ref);
		getChildren().add(new LazySimpleTreeItem<>(MessageRegistry.getString("FETCH",GitModuleReal.NAME),
				()->ref.getFetchRefSpecs().stream().map((spec)->new RemoteSpecTreeItem(spec)).collect(Collectors.toList())));
		getChildren().add(new LazySimpleTreeItem<>(MessageRegistry.getString("PUSH",GitModuleReal.NAME),
				()->ref.getPushRefSpecs().stream().map((spec)->new RemoteSpecTreeItem(spec)).collect(Collectors.toList())));
	}
	@Override
	public String toString(){
		return ((RemoteConfig)getValue()).getName();
	}
	@Override
	public MenuItem[] getContextMenuItems(){
		return new MenuItem[]{
			MenuItemBuilder.build("PUSH",(e)->GitCommands.execute("git-push")),
			MenuItemBuilder.build("PULL",(e)->GitCommands.execute("git-pull")),
			MenuItemBuilder.build("FETCH",(e)->GitCommands.execute("git-fetch")),
			MenuItemBuilder.build("REMOVE REMOTE",(e)->GitCommands.execute("git-branch-delete")),
			MenuItemBuilder.build("RESET URL",(e)->GitCommands.execute("git-remote-set-url"))
		};
	}
}