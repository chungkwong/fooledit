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
import java.util.stream.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.*;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.*;
import org.eclipse.jgit.transport.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GitRepositoryViewer extends BorderPane{
	public GitRepositoryViewer(Git repository){
		TreeTableView<Object> nav=new TreeTableView<>(new GitTreeItem(repository));
		nav.setShowRoot(true);
		ContextMenu contextMenu=new ContextMenu();
		nav.setContextMenu(contextMenu);
		nav.setOnContextMenuRequested((e)->contextMenu.getItems().setAll(((NavigationTreeItem)nav.getSelectionModel().getSelectedItem()).getContextMenuItems()));
		setCenter(nav);
		setBottom(createColumnsChooser(nav));
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
