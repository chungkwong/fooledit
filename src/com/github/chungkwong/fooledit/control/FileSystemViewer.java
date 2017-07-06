/*
 * Copyright (C) 2017 Chan Chung Kwong <1m02math@126.com>
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
package com.github.chungkwong.fooledit.control;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FileSystemViewer extends BorderPane{
	private final TreeTableView<Path> tree=new TreeTableView<>();
	public FileSystemViewer(){
		FileSystems.getDefault().getRootDirectories();
		Stream<Path> roots=StreamSupport.stream(Spliterators.spliteratorUnknownSize(FileSystems.getDefault().getRootDirectories().iterator(),0),false);
		TreeItem<Path> root=new LazyTreeItem<Path>(()->roots.sorted().map((r)->new LazyTreeItem<>(()->getChildren(r),r)).collect(Collectors.toList()),null);
		tree.setShowRoot(false);
		tree.setRoot(root);
		setCenter(tree);
		HBox attrs=new HBox();
		attrs.getChildren().add(createColumnChooser("name",new Callback<TreeTableColumn.CellDataFeatures<Path,String>,ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Path,String> param){
				return new ReadOnlyStringWrapper(Objects.toString(param.getValue().getValue().getFileName()));
			}
		},true));
		setBottom(attrs);
	}
	private static Collection<TreeItem<Path>> getChildren(Path item){
		try{
			return Files.list(item).sorted()
					.map((f)->Files.isDirectory(f)?new LazyTreeItem<Path>(()->getChildren(f),f):new TreeItem<>(f))
					.collect(Collectors.toList());
		}catch(IOException ex){
			return Collections.emptyList();
		}
	}

	private CheckBox createColumnChooser(String name,Callback<TreeTableColumn.CellDataFeatures<Path,String>,ObservableValue<String>> callback,boolean visible){
		TreeTableColumn<Path,String> column=new TreeTableColumn<>(name);
		column.setCellValueFactory(callback);
		CheckBox chooser=new CheckBox(name);
		chooser.setSelected(visible);
		if(visible)
			tree.getColumns().add(column);
		chooser.selectedProperty().addListener((v)->{
			if(chooser.isSelected())
				tree.getColumns().add(column);
			else
				tree.getColumns().remove(column);
		});
		return chooser;
	}
	static class FileCell extends TreeCell<File>{
		private TreeItem<File> src;
		public FileCell(){
			setOnDragDetected((e)->{
				Dragboard board=startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content=new ClipboardContent();
				content.putFiles(Collections.singletonList(getItem()));
				src=getTreeItem();
				board.setContent(content);
				e.consume();
			});
			setOnDragOver((e)->{
				if(e.getDragboard().hasFiles()&&getItem().isDirectory())
					e.acceptTransferModes(TransferMode.MOVE,TransferMode.COPY);
				e.consume();
			});
			setOnDragDropped((e)->{
				Dragboard board=e.getDragboard();
				if(board.hasFiles()){
					try{
						for(File f:board.getFiles()){
							Path to=getItem().toPath().resolve(f.getName());
							Files.move(f.toPath(),to);
							getTreeItem().getChildren().add(new TreeItem<>(to.toFile()));
						}
						e.setDropCompleted(true);
					}catch(IOException ex){
						Logger.getGlobal().log(Level.SEVERE,null,ex);
						e.setDropCompleted(false);
					}
				}else{
					e.setDropCompleted(false);
				}
				e.consume();
			});
			setOnDragDone((e)->{
				src.getParent().getChildren().remove(src);
			});
		}
		@Override
		protected void updateItem(File item,boolean empty){
			super.updateItem(item,empty);
			if(empty||item==null){
				setText(null);
				setGraphic(null);
			}else{
				setText(item.getName());
			}
		}
	}
}
