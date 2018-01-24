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
import java.util.logging.*;
import java.util.stream.*;
import javafx.application.*;
import javafx.collections.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.transport.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GitTreeItem extends TreeItem<Object> implements NavigationTreeItem{
	public GitTreeItem(Git git){
		super(git);
		try{
			getChildren().add(new WorkingTreeItem(git));
			getChildren().add(new StageTreeItem(git));
			getChildren().add(new LogTreeItem(git));
			getChildren().add(new NoteListTreeItem(git));
			getChildren().add(new TagListTreeItem(git));
			getChildren().add(new LocalTreeItem(git));
			for(RemoteConfig remote:git.remoteList().call())
				getChildren().add(new RemoteTreeItem(remote));
		}catch(Exception ex){
			Logger.getLogger(GitTreeItem.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	@Override
	public String toString(){
		return ((Git)getValue()).getRepository().getDirectory().getParentFile().getName();
	}
	@Override
	public MenuItem[] getContextMenuItems(){
		MenuItem remote=new MenuItem(MessageRegistry.getString("NEW REMOTE",GitModuleReal.NAME));
		remote.setOnAction((e)->gitRemoteNew());
		MenuItem gc=new MenuItem(MessageRegistry.getString("COLLECT GARGAGE",GitModuleReal.NAME));
		gc.setOnAction((e)->gitGC());
		MenuItem conf=new MenuItem(MessageRegistry.getString("CONFIGURE",GitModuleReal.NAME));
		conf.setOnAction((e)->gitConfig());
		return new MenuItem[]{remote,gc,conf};
	}
	private void gitRemoteNew(){
		TextInputDialog dialog=new TextInputDialog();
		dialog.setTitle(MessageRegistry.getString("CHOOSE A NAME FOR THE NEW REMOTE CONFIGURE",GitModuleReal.NAME));
		dialog.setHeaderText(MessageRegistry.getString("ENTER THE NAME OF THE NEW REMOTE CONFIGURE:",GitModuleReal.NAME));
		Optional<String> name=dialog.showAndWait();
		dialog.setTitle(MessageRegistry.getString("CHOOSE A URI FOR THE NEW REMOTE CONFIGURE",GitModuleReal.NAME));
		dialog.setHeaderText(MessageRegistry.getString("ENTER THE URI OF THE NEW REMOTE CONFIGURE:",GitModuleReal.NAME));
		Optional<String> uri=dialog.showAndWait();
		if(name.isPresent())
			try{
				RemoteAddCommand command=((Git)getValue()).remoteAdd();
				command.setName(name.get());
				command.setUri(new URIish(uri.get()));
				getChildren().add(new RemoteTreeItem(command.call()));
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
	}
	private void gitConfig(){
		try{
			StoredConfig config=((Git)getValue()).getRepository().getConfig();
			Dialog dialog=new Dialog();
			dialog.setTitle(MessageRegistry.getString("CONFIGURE",GitModuleReal.NAME));
			TextArea area=new TextArea(config.toText());
			dialog.getDialogPane().setContent(area);
			dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL,ButtonType.APPLY);
			dialog.showAndWait();
			if(dialog.getResult().equals(ButtonType.APPLY)){
				config.fromText(area.getText());
				config.save();
			}
		}catch(Exception ex){
			Logger.getLogger(GitTreeItem.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	private void gitGC(){
		ProgressDialog progressDialog=new ProgressDialog(MessageRegistry.getString("GC",GitModuleReal.NAME));
		GarbageCollectCommand command=((Git)getValue()).gc().setProgressMonitor(progressDialog);
		new Thread(()->{
			try{
				Properties stat=command.call();
				System.out.println(stat);
			}catch(Exception ex){
				Logger.getLogger(GitTreeItem.class.getName()).log(Level.SEVERE,null,ex);
				Platform.runLater(()->{
					progressDialog.hide();
				});
			}
		}).start();
	}
	@Override
	public Node getContentPage(){
		GridPane node=new GridPane();
		try{
			Status status=((Git)getValue()).status().call();
			Set<String> untrackedSet=new HashSet<>(status.getUntrackedFolders());
			untrackedSet.addAll(status.getUntracked());
			untrackedSet.removeAll(status.getIgnoredNotInIndex());
			TitledPane untracked=createList(MessageRegistry.getString("UNTRACKED FILE",GitModuleReal.NAME),untrackedSet);
			TitledPane missing=createList(MessageRegistry.getString("MISSING",GitModuleReal.NAME),status.getMissing());
			TitledPane modified=createList(MessageRegistry.getString("MODIFIED",GitModuleReal.NAME),status.getModified());
			TitledPane added=createList(MessageRegistry.getString("ADDED",GitModuleReal.NAME),status.getAdded());
			TitledPane removed=createList(MessageRegistry.getString("REMOVED",GitModuleReal.NAME),status.getRemoved());
			TitledPane changed=createList(MessageRegistry.getString("CHANGED",GitModuleReal.NAME),status.getChanged());
			Button add=new Button(MessageRegistry.getString("ADD",GitModuleReal.NAME));
			add.setOnAction((e)->gitAdd(untracked,modified,added,changed));
			Button commit=new Button(MessageRegistry.getString("COMMIT",GitModuleReal.NAME));
			commit.setOnAction((e)->gitCommit(added,removed,changed));
			Button clean=new Button(MessageRegistry.getString("CLEAN",GitModuleReal.NAME));
			clean.setOnAction((e)->gitClean(untracked));
			node.addColumn(0,untracked,missing,modified,add);
			node.addColumn(1,added,removed,changed,commit,clean);
		}catch(Exception ex){
			Logger.getLogger(GitTreeItem.class.getName()).log(Level.SEVERE,null,ex);
		}
		return node;
	}
	private void gitCommit(TitledPane addedView,TitledPane removedView,TitledPane changedView){
		TextInputDialog dialog=new TextInputDialog();
		dialog.setTitle(MessageRegistry.getString("CHOOSE A MESSAGE FOR THE COMMIT",GitModuleReal.NAME));
		dialog.setHeaderText(MessageRegistry.getString("ENTER THE MESSAGE:",GitModuleReal.NAME));
		Optional<String> msg=dialog.showAndWait();
		if(msg.isPresent())
			try{
				RevCommit commit=((Git)getValue()).commit().setMessage(msg.get()).call();
				((ListView<String>)addedView.getContent()).getItems().clear();
				((ListView<String>)removedView.getContent()).getItems().clear();
				((ListView<String>)changedView.getContent()).getItems().clear();
				getChildren().stream().filter((item)->item instanceof LogTreeItem).forEach(
						(item)->((LogTreeItem)item).getChildren().add(new CommitTreeItem(commit)));
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
	}
	private void gitClean(TitledPane untrackedView){
		try{
			((Git)getValue()).clean().setIgnore(true).call();
			((ListView<String>)untrackedView.getContent()).getItems().clear();
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private TitledPane createList(String title,Set<String> data){
		ListView<String> list=new ListView<>(FXCollections.observableList(data.stream().collect(Collectors.toList())));
		list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		TitledPane titledPane=new TitledPane(title,list);
		GridPane.setHgrow(titledPane,Priority.ALWAYS);
		GridPane.setVgrow(titledPane,Priority.ALWAYS);
		return titledPane;
	}
	private void gitAdd(TitledPane untrackedView,TitledPane modifiedView,TitledPane addedView,TitledPane changedView){
		Git git=(Git)getValue();
		ListView<String> untracked=((ListView<String>)untrackedView.getContent());
		ListView<String> modified=((ListView<String>)modifiedView.getContent());
		ListView<String> added=((ListView<String>)addedView.getContent());
		ListView<String> changed=((ListView<String>)changedView.getContent());
		try{
			for(String item:untracked.getSelectionModel().getSelectedItems()){
				git.add().addFilepattern(item).call();
				untracked.getItems().remove(item);
				added.getItems().add(item);
			}
			for(String item:modified.getSelectionModel().getSelectedItems()){
				git.add().addFilepattern(item).call();
				modified.getItems().remove(item);
				changed.getItems().add(item);
			}
		}catch(Exception ex){
			Logger.getLogger(GitTreeItem.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
}