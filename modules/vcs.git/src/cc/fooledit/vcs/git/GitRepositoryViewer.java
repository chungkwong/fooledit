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
import java.text.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.*;
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
	public GitRepositoryViewer(Git git){
		this.git=git;
		TreeTableView<Object> nav=new TreeTableView<>(new GitTreeItem(git));
		nav.setShowRoot(true);
		ContextMenu contextMenu=new ContextMenu();
		nav.setContextMenu(contextMenu);
		nav.setOnContextMenuRequested((e)->contextMenu.getItems().setAll(((NavigationTreeItem)nav.getSelectionModel().getSelectedItem()).getContextMenuItems()));
		setCenter(nav);
		setBottom(createColumnsChooser(nav));
	}
	private TreeItem<Object> createGitTreeItem(){
		return new SimpleTreeItem<>(git,
				new TreeItem[]{createWorkingTreeItem(),
					createStageTreeItem(),
					createLogTreeItem(),
					createTagListTreeItem(),
					createLocalTreeItem()
				},new MenuItem[]{});
	}
	private TreeItem<Object> createWorkingTreeItem(){
		return new SimpleTreeItem<>(MessageRegistry.getString("WORKING DIRECTORY",GitModuleReal.NAME),
				new TreeItem[]{new LazySimpleTreeItem(()->git.status().call().getIgnoredNotInIndex().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList()),
					MessageRegistry.getString("IGNORED",GitModuleReal.NAME)),
					new LazySimpleTreeItem(()->git.status().call().getUntracked().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList()),
					MessageRegistry.getString("UNTRACKED",GitModuleReal.NAME)),
					new LazySimpleTreeItem(()->git.status().call().getConflicting().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList()),
					MessageRegistry.getString("CONFLICTING",GitModuleReal.NAME)),
					new LazySimpleTreeItem(()->git.status().call().getMissing().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList()),
					MessageRegistry.getString("MISSING",GitModuleReal.NAME)),
					new LazySimpleTreeItem(()->git.status().call().getModified().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList()),
					MessageRegistry.getString("MODIFIED",GitModuleReal.NAME))
				});
	}
	private TreeItem<Object> createStageTreeItem(){
		return new SimpleTreeItem<>(MessageRegistry.getString("STAGING AREA",GitModuleReal.NAME),
				new TreeItem[]{new LazySimpleTreeItem(()->git.status().call().getAdded().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList()),
				MessageRegistry.getString("ADDED",GitModuleReal.NAME)),
					new LazySimpleTreeItem(()->git.status().call().getRemoved().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList()),
					MessageRegistry.getString("REMOVED",GitModuleReal.NAME)),
					new LazySimpleTreeItem(()->git.status().call().getChanged().stream().map((file)->new SimpleTreeItem<String>(file)).collect(Collectors.toList()),
					MessageRegistry.getString("CHANGED",GitModuleReal.NAME)),
					new LazySimpleTreeItem(()->{
						LinkedList<TreeItem<String>> files=new LinkedList<>();
						DirCache cache=git.getRepository().readDirCache();
						for(int i=0;i<cache.getEntryCount();i++)
							files.add(new SimpleTreeItem<>(cache.getEntry(i).getPathString(),new MenuItem[]{getRemoveMenuItem(),getBlameMenuItem()}));
						return files;
					},MessageRegistry.getString("ALL",GitModuleReal.NAME))
				});
	}

	private MenuItem getCommitMenuItem(){
		MenuItem commitItem=new MenuItem(MessageRegistry.getString("COMMIT",GitModuleReal.NAME));
		commitItem.setOnAction((e)->{
			try{
				git.commit().call();//TODO: message
			}catch(GitAPIException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		});
		return commitItem;
	}
	private MenuItem getRemoveMenuItem(){
		MenuItem item=new MenuItem(MessageRegistry.getString("REMOVE",GitModuleReal.NAME));
		/*item.setOnAction((e)->{
			RmCommand command=((Git)getValue()).rm().setCached(true);
			list.getSelectionModel().getSelectedItems().stream().forEach((path)->{
				command.addFilepattern(path);
			});
			list.getItems().removeAll(list.getSelectionModel().getSelectedItems());
			try{
				command.call();
			}catch(Exception ex){
				Logger.getLogger(StageTreeItem.class.getName()).log(Level.SEVERE,null,ex);
			}
		});*/
		return item;
	}
	private MenuItem getBlameMenuItem(){
		MenuItem item=new MenuItem(MessageRegistry.getString("BLAME",GitModuleReal.NAME));
		/*item.setOnAction((e)->{
			(e)->{
			Stage dialog=new Stage();
			dialog.setTitle(MessageRegistry.getString("BLAME",GitModuleReal.NAME));
			StringBuilder buf=new StringBuilder();
			list.getSelectionModel().getSelectedItems().stream().forEach((path)->{
				try{
					BlameResult command=git.blame().setFilePath(path).call();
					RawText contents=command.getResultContents();
					for(int i=0;i<contents.size();i++){
						buf.append(command.getSourcePath(i)).append(':');
						buf.append(command.getSourceLine(i)).append(':');
						buf.append(command.getSourceCommit(i)).append(':');
						buf.append(command.getSourceAuthor(i)).append(':');
						buf.append(contents.getString(i)).append('\n');
					}
				}catch(Exception ex){
					Logger.getLogger(StageTreeItem.class.getName()).log(Level.SEVERE,null,ex);
				}
			});
			dialog.setScene(new Scene(new TextArea(buf.toString())));
			dialog.setMaximized(true);
			dialog.show();
		}
		});*/
		return item;
	}
	private TreeItem<Object> createTagListTreeItem(){
		return new LazySimpleTreeItem<>(()->git.tagList().call().stream().map((ref)->new TagTreeItem(ref)).collect(Collectors.toList()),
				MessageRegistry.getString("TAG",GitModuleReal.NAME));
	}
	private TreeItem<Object> createLocalTreeItem(){
		return new LazySimpleTreeItem<>(()->git.branchList().call().stream().map((ref)->new BranchTreeItem(ref)).collect(Collectors.toList()),
				MessageRegistry.getString("LOCAL BRANCH",GitModuleReal.NAME),
			new MenuItem[]{getBranchMenuItem()});
	}
	private MenuItem getBranchMenuItem(){
		MenuItem item=new MenuItem(MessageRegistry.getString("NEW BRANCH",GitModuleReal.NAME));
		item.setOnAction((e)->{
			TextInputDialog branchDialog=new TextInputDialog();
			//branchDialog.setTitle(MessageRegistry.getString("CHOOSE A NAME FOR THE NEW BRANCH",GitModuleReal.NAME));
			//branchDialog.setHeaderText(MessageRegistry.getString("ENTER THE NAME OF THE NEW BRANCH:",GitModuleReal.NAME));
			Optional<String> name=branchDialog.showAndWait();
			if(name.isPresent())
				try{
					//getChildren().add(new BranchTreeItem(git.branchCreate().setName(name.get()).call()));
				}catch(Exception ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
				}
		});
		return item;
	}
	private FlowPane createColumnsChooser(TreeTableView<Object> nav){
		FlowPane chooser=new FlowPane();
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("NAME",GitModuleReal.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				return new ReadOnlyObjectWrapper<>(p.getValue().toString());
			}
		},true,nav));
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("MESSAGE",GitModuleReal.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue() instanceof CommitTreeItem)
					return new ReadOnlyObjectWrapper<>(((RevCommit)p.getValue().getValue()).getShortMessage());
				else
					return new ReadOnlyObjectWrapper<>("");
			}
		},false,nav));
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("AUTHOR",GitModuleReal.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue() instanceof CommitTreeItem)
					return new ReadOnlyObjectWrapper<>(((RevCommit)p.getValue().getValue()).getAuthorIdent().toExternalString());
				else
					return new ReadOnlyObjectWrapper<>("");
			}
		},false,nav));
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("COMMITTER",GitModuleReal.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue() instanceof CommitTreeItem)
					return new ReadOnlyObjectWrapper<>(((RevCommit)p.getValue().getValue()).getCommitterIdent().toExternalString());
				else
					return new ReadOnlyObjectWrapper<>("");
			}
		},false,nav));
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("TIME",GitModuleReal.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue() instanceof CommitTreeItem)
					return new ReadOnlyObjectWrapper<>(timeToString(((RevCommit)p.getValue().getValue()).getCommitTime()));
				else
					return new ReadOnlyObjectWrapper<>("");
			}
		},false,nav));
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("REFERNECE",GitModuleReal.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Object,String> p){
				if(p.getValue() instanceof CommitTreeItem){
					ObjectId id=((RevCommit)p.getValue().getValue()).getId();
					return new ReadOnlyObjectWrapper<>(id==null?"":id.getName());
				}else if(p.getValue() instanceof BranchTreeItem){
					ObjectId id=((Ref)p.getValue().getValue()).getLeaf().getObjectId();
					return new ReadOnlyObjectWrapper<>(id==null?"":id.getName());
				}else if(p.getValue() instanceof TagTreeItem){
					ObjectId id=((Ref)p.getValue().getValue()).getTarget().getLeaf().getObjectId();
					return new ReadOnlyObjectWrapper<>(id==null?"":id.getName());
				}else
					return new ReadOnlyObjectWrapper<>("");
			}
		},false,nav));
		chooser.getChildren().add(createColumnChooser(MessageRegistry.getString("URI",GitModuleReal.NAME),new Callback<TreeTableColumn.CellDataFeatures<Object, String>,ObservableValue<String>>() {
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
}
