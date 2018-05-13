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
import cc.fooledit.control.*;
import cc.fooledit.core.*;
import cc.fooledit.vcs.git.MenuItemBuilder;
import java.text.*;
import java.util.*;
import java.util.stream.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.dircache.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.transport.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GitRepositoryViewer extends BorderPane{
	private final Git git;
	private final TreeTableView<Object> nav;
	public GitRepositoryViewer(Git git){
		this.git=git;
		nav=new TreeTableWrapper<>(createGitTreeItem());
		nav.setShowRoot(false);
		ContextMenu contextMenu=new ContextMenu();
		nav.setOnContextMenuRequested((e)->contextMenu.getItems().setAll(((NavigationTreeItem)nav.getSelectionModel().getSelectedItem()).getContextMenuItems()));
		nav.setContextMenu(contextMenu);
		nav.getFocusModel().focusedIndexProperty().addListener(
				(e,o,n)->nav.scrollTo(n.intValue()));
		setCenter(nav);
		setBottom(createColumnsChooser(nav));
	}
	private TreeItem<Object> createGitTreeItem(){
		return new SimpleTreeItem<>(git.getRepository().getDirectory().toString(),
				new TreeItem[]{createWorkingTreeItem(),
					createStageTreeItem(),
					createLogTreeItem(),
					createTagListTreeItem(),
					createLocalTreeItem(),
					createRemoteTreeItem()
				},new MenuItem[]{
					MenuItemBuilder.build("COLLECT GARGAGE",(e)->GitCommands.execute("git-gc")),
					MenuItemBuilder.build("CONFIGURE",(e)->GitCommands.execute("git-config"))
				});
	}
	private TreeItem<Object> createWorkingTreeItem(){
		MenuItem[] add=new MenuItem[]{
			MenuItemBuilder.build("ADD",(e)->GitCommands.execute("git-add"))
		};
		return new SimpleTreeItem<>(MessageRegistry.getString("WORKING DIRECTORY",GitModule.NAME),
				new TreeItem[]{
					new LazySimpleTreeItem(MessageRegistry.getString("IGNORED",GitModule.NAME),
							()->git.status().call().getIgnoredNotInIndex().stream().map((file)->new SimpleTreeItem<>(file)).collect(Collectors.toList())),
					new LazySimpleTreeItem(MessageRegistry.getString("UNTRACKED",GitModule.NAME),
							()->git.status().call().getUntracked().stream().map((file)->new SimpleTreeItem<>(file,add)).collect(Collectors.toList())),
					new LazySimpleTreeItem(MessageRegistry.getString("CONFLICTING",GitModule.NAME),
							()->git.status().call().getConflicting().stream().map((file)->new SimpleTreeItem<>(file)).collect(Collectors.toList())),
					new LazySimpleTreeItem(MessageRegistry.getString("MISSING",GitModule.NAME),
							()->git.status().call().getMissing().stream().map((file)->new SimpleTreeItem<>(file)).collect(Collectors.toList())),
					new LazySimpleTreeItem(MessageRegistry.getString("MODIFIED",GitModule.NAME),
							()->git.status().call().getModified().stream().map((file)->new SimpleTreeItem<>(file,add)).collect(Collectors.toList()))
				});
	}
	private TreeItem<Object> createStageTreeItem(){
		MenuItem[] rm=new MenuItem[]{
			MenuItemBuilder.build("REMOVE",(e)->GitCommands.execute("git-remove"))
		};
		MenuItem[] all=new MenuItem[]{
			MenuItemBuilder.build("REMOVE",(e)->GitCommands.execute("git-remove")),
			MenuItemBuilder.build("BLAME",(e)->GitCommands.execute("git-blame"))
		};
		return new SimpleTreeItem<>(MessageRegistry.getString("STAGING AREA",GitModule.NAME),
				new TreeItem[]{
					new LazySimpleTreeItem(MessageRegistry.getString("ADDED",GitModule.NAME),
							()->git.status().call().getAdded().stream().map((file)->new SimpleTreeItem<>(file,rm)).collect(Collectors.toList())),
					new LazySimpleTreeItem(MessageRegistry.getString("REMOVED",GitModule.NAME),
							()->git.status().call().getRemoved().stream().map((file)->new SimpleTreeItem<>(file)).collect(Collectors.toList())),
					new LazySimpleTreeItem(MessageRegistry.getString("CHANGED",GitModule.NAME),
							()->git.status().call().getChanged().stream().map((file)->new SimpleTreeItem<>(file,rm)).collect(Collectors.toList())),
					new LazySimpleTreeItem(MessageRegistry.getString("ALL",GitModule.NAME),()->{
						LinkedList<TreeItem<String>> files=new LinkedList<>();
						DirCache cache=git.getRepository().readDirCache();
						for(int i=0;i<cache.getEntryCount();i++)
							files.add(new SimpleTreeItem<>(cache.getEntry(i).getPathString(),all));
						return files;
					})
				},new MenuItem[]{
					MenuItemBuilder.build("COMMIT",(e)->GitCommands.execute("git-commit"))
				});
	}
	private TreeItem<Object> createTagListTreeItem(){
		return new LazySimpleTreeItem<>(MessageRegistry.getString("TAG",GitModule.NAME),
				()->git.tagList().call().stream().map((ref)->new TagTreeItem(ref,git)).collect(Collectors.toList()));
	}
	private TreeItem<Object> createLogTreeItem(){
		return new LazySimpleTreeItem<>(MessageRegistry.getString("COMMIT",GitModule.NAME),
				()->StreamSupport.stream(git.log().call().spliterator(),false).map((rev)->new CommitTreeItem(rev,git)).collect(Collectors.toList()),
				new MenuItem[0]);
	}
	private TreeItem<Object> createLocalTreeItem(){
		return new LazySimpleTreeItem<>(MessageRegistry.getString("LOCAL BRANCH",GitModule.NAME),
				()->git.branchList().call().stream().map((ref)->new BranchTreeItem(ref,git)).collect(Collectors.toList()),
			new MenuItem[]{
				MenuItemBuilder.build("BRANCH",(e)->GitCommands.execute("git-branch-add"))
			});
	}
	private TreeItem<Object> createRemoteTreeItem(){
		return new LazySimpleTreeItem<>(MessageRegistry.getString("REMOTE BRANCH",GitModule.NAME),
				()->git.remoteList().call().stream().map((ref)->new RemoteTreeItem(ref)).collect(Collectors.toList()),
			new MenuItem[]{
				MenuItemBuilder.build("REMOTE ADD",(e)->GitCommands.execute("git-remote-add"))
			});
	}
	private FlowPane createColumnsChooser(TreeTableView<Object> nav){
		FlowPane chooser=new FlowPane();
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("NAME",GitModule.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				return new ReadOnlyObjectWrapper<>(p.getValue().toString());
			}
		},true,nav));
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("MESSAGE",GitModule.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue() instanceof CommitTreeItem)
					return new ReadOnlyObjectWrapper<>(((RevCommit)p.getValue().getValue()).getShortMessage());
				else
					return new ReadOnlyObjectWrapper<>("");
			}
		},false,nav));
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("AUTHOR",GitModule.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue() instanceof CommitTreeItem)
					return new ReadOnlyObjectWrapper<>(((RevCommit)p.getValue().getValue()).getAuthorIdent().toExternalString());
				else
					return new ReadOnlyObjectWrapper<>("");
			}
		},false,nav));
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("COMMITTER",GitModule.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue() instanceof CommitTreeItem)
					return new ReadOnlyObjectWrapper<>(((RevCommit)p.getValue().getValue()).getCommitterIdent().toExternalString());
				else
					return new ReadOnlyObjectWrapper<>("");
			}
		},false,nav));
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("TIME",GitModule.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue() instanceof CommitTreeItem)
					return new ReadOnlyObjectWrapper<>(timeToString(((RevCommit)p.getValue().getValue()).getCommitTime()));
				else
					return new ReadOnlyObjectWrapper<>("");
			}
		},false,nav));
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("REFERNECE",GitModule.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue().getValue() instanceof ObjectId){
					ObjectId id=(ObjectId)p.getValue().getValue();
					return new ReadOnlyObjectWrapper<>(id.getName());
				}else if(p.getValue().getValue() instanceof Ref){
					ObjectId id=((Ref)p.getValue().getValue()).getObjectId();
					return new ReadOnlyObjectWrapper<>(id.getName());
				}else
					return new ReadOnlyObjectWrapper<>("");
			}
		},false,nav));
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("URI",GitModule.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue() instanceof RemoteTreeItem){
					String uris=((RemoteConfig)p.getValue().getValue()).getURIs().stream().map((url)->url.toString()).collect(Collectors.joining(" "));
					return new ReadOnlyObjectWrapper<>(uris);
				}else
					return new ReadOnlyObjectWrapper<>("");
			}
		},false,nav));

		return chooser;
	}
	private static String timeToString(int time){
		return DateFormat.getDateTimeInstance().format(new Date(time*1000l));
	}
	private CheckBox createColumnChooser(String name,Callback callback,boolean visible,TreeTableView<Object> nav){
		TreeTableColumn<Object,String> column=new TreeTableColumn<>(name);
		column.setCellValueFactory(callback);
		CheckBox chooser=new CheckBox(name);
		chooser.setSelected(visible);
		if(visible)
			nav.getColumns().add(column);
		chooser.selectedProperty().addListener((v)->{
			if(chooser.isSelected())
				nav.getColumns().add(column);
			else
				nav.getColumns().remove(column);
		});
		return chooser;
	}
	public TreeTableView<Object> getTree(){
		return nav;
	}
}
