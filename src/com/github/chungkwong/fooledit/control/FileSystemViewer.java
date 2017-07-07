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
import com.github.chungkwong.fooledit.*;
import com.github.chungkwong.fooledit.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
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
		this.<String>createColumnChooser(MessageRegistry.getString("NAME"),(param)->
				new ReadOnlyStringWrapper(getFileName(param.getValue().getValue())),true);
		attrs.getChildren().add(this.<String>createColumnChooser(MessageRegistry.getString("OWNER"),(param)->
				new ReadOnlyStringWrapper(getOwnerName(param.getValue().getValue())),true));
		attrs.getChildren().add(this.<Boolean>createColumnChooser(MessageRegistry.getString("READABLE"),(param)->
				new ReadOnlyBooleanWrapper(Files.isReadable(param.getValue().getValue())),true));
		attrs.getChildren().add(this.<Boolean>createColumnChooser(MessageRegistry.getString("WRITABLE"),(param)->
				new ReadOnlyBooleanWrapper(Files.isWritable(param.getValue().getValue())),true));
		attrs.getChildren().add(this.<Boolean>createColumnChooser(MessageRegistry.getString("EXECUTABLE"),(param)->
				new ReadOnlyBooleanWrapper(Files.isExecutable(param.getValue().getValue())),true));
		((TreeTableColumn<Path,String>)tree.getColumns().get(0)).setCellFactory((p)->new FileCell());
		setBottom(attrs);
	}
	private static String getFileName(Path path){
		Path name=path.getFileName();
		return name==null?"":name.toString();
	}
	private static String getOwnerName(Path path){
		try{
			return Files.getOwner(path,LinkOption.NOFOLLOW_LINKS).getName();
		}catch(IOException ex){
			return MessageRegistry.getString("UNKNOWN");
		}
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
	private <T> CheckBox createColumnChooser(String name,Callback<TreeTableColumn.CellDataFeatures<Path,T>,ObservableValue<T>> callback,boolean visible){
		TreeTableColumn<Path,T> column=new TreeTableColumn<>(name);
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
	static class FileCell extends TreeTableCell<Path,String>{
		private TreeItem<File> src;
		public FileCell(){
			/*setOnDragDetected((e)->{
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
			});*/
		}
		@Override
		protected void updateItem(String item,boolean empty){
			super.updateItem(item,empty);
			Path path=getTreeTableRow().getItem();
			if(empty||item==null||path==null){
				setText(null);
				setGraphic(null);
			}else{
				setText(item);
				if(Files.isDirectory(path))
					setGraphic(new ImageView(FOLDER));
				else if(Files.isRegularFile(path))
					setGraphic(new ImageView(REGULAR));
				else
					setGraphic(null);
			}
		}

	}
	private static Image FOLDER,REGULAR;
	static{
		try{
			FOLDER=new Image(new FileInputStream(Main.getFile("icons/folder.png","core")));
			REGULAR=new Image(new FileInputStream(Main.getFile("icons/regular.png","core")));
		}catch(FileNotFoundException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
}
