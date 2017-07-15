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
package com.github.chungkwong.fooledit.example.filesystem;
import com.github.chungkwong.fooledit.*;
import com.github.chungkwong.fooledit.api.*;
import com.github.chungkwong.fooledit.control.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
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
	private WatchService watchService;
	private Consumer<Collection<Path>> action;
	private final Thread updateThread=new Thread(()->refresh());
	private boolean active=true;
	private Collection<Path> marked=Collections.emptySet();
	public FileSystemViewer(){
		try{
			watchService=FileSystems.getDefault().newWatchService();
			updateThread.setDaemon(true);
			updateThread.start();
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			watchService=null;
		}
		Stream<Path> roots=StreamSupport.stream(Spliterators.spliteratorUnknownSize(FileSystems.getDefault().getRootDirectories().iterator(),0),false);
		TreeItem<Path> root=new LazyTreeItem<Path>(()->roots.sorted().map((r)->createTreeItem(r)).collect(Collectors.toList()),null);
		tree.setShowRoot(false);
		tree.setRoot(root);
		tree.setTableMenuButtonVisible(true);
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		setCenter(tree);
		this.<String>createColumnChooser(MessageRegistry.getString("NAME"),(param)->
				new ReadOnlyStringWrapper(getFileName(param.getValue().getValue())),true);
		this.<String>createColumnChooser(MessageRegistry.getString("OWNER"),(param)->
				new ReadOnlyStringWrapper(getOwnerName(param.getValue().getValue())),true);
		this.<Boolean>createColumnChooser(MessageRegistry.getString("READABLE"),(param)->
				new ReadOnlyBooleanWrapper(Files.isReadable(param.getValue().getValue())),true);
		this.<Boolean>createColumnChooser(MessageRegistry.getString("WRITABLE"),(param)->
				new ReadOnlyBooleanWrapper(Files.isWritable(param.getValue().getValue())),true);
		this.<Boolean>createColumnChooser(MessageRegistry.getString("EXECUTABLE"),(param)->
				new ReadOnlyBooleanWrapper(Files.isExecutable(param.getValue().getValue())),true);
		this.<Boolean>createColumnChooser(MessageRegistry.getString("HIDDEN"),(param)->
				new ReadOnlyBooleanWrapper(isHidden(param.getValue().getValue())),true);
		this.<String>createColumnChooser(MessageRegistry.getString("LAST_MODIFIED"),(param)->
				new ReadOnlyStringWrapper(getLastModified(param.getValue().getValue())),true);
		this.<Number>createColumnChooser(MessageRegistry.getString("SIZE"),(param)->
				new ReadOnlyLongWrapper(getSize(param.getValue().getValue())),true);
		this.<String>createColumnChooser(MessageRegistry.getString("SYMBOLIC_LINK"),(param)->
				new ReadOnlyStringWrapper(getLinkTarget(param.getValue().getValue())),false);
		((TreeTableColumn<Path,String>)tree.getColumns().get(0)).setCellFactory((p)->new FileCell());
		tree.setEditable(true);
		tree.getFocusModel().focusedIndexProperty().addListener(((e,o,n)->tree.scrollTo(n.intValue())));
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
	private static String getLastModified(Path path){
		try{
			return Files.getLastModifiedTime(path).toString();
		}catch(IOException ex){
			return MessageRegistry.getString("UNKNOWN");
		}
	}
	private static long getSize(Path path){
		try{
			return Files.size(path);
		}catch(IOException ex){
			return -1;
		}
	}
	private static String getLinkTarget(Path path){
		try{
			return Files.isSymbolicLink(path)?Files.readSymbolicLink(path).toString():"";
		}catch(IOException ex){
			return "";
		}
	}
	private static boolean isHidden(Path path){
		try{
			return Files.isHidden(path);
		}catch(IOException ex){
			return false;
		}
	}
	private void refresh(){
		while(active){
			try{
				WatchKey key=watchService.take();
				Path path=(Path)key.watchable();
				TreeItem<Path> item=getTreeItem(path);
				if(item==null||!item.isExpanded())
					continue;
				for(WatchEvent event:key.pollEvents()){
					if(event.kind()==StandardWatchEventKinds.OVERFLOW){
						((LazyTreeItem<Path>)item).refresh();
					}else{
						Path file=(Path)event.context();
						TreeItem<Path> sub=getTreeItem(path.resolve(file),item);
						if(event.kind()==StandardWatchEventKinds.ENTRY_CREATE){
							if(sub==null)
								item.getChildren().add(createTreeItem(path.resolve(file)));
							else
								item.getChildren().set(item.getChildren().indexOf(sub),sub);
						}else if(event.kind()==StandardWatchEventKinds.ENTRY_DELETE){
							item.getChildren().remove(sub);
						}else if(event.kind()==StandardWatchEventKinds.ENTRY_MODIFY){
							item.getChildren().set(item.getChildren().indexOf(sub),sub);
						}
					}
				}
				key.reset();
			}catch(Exception ex){

			}
		}
		try{
			watchService.close();
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private TreeItem<Path> getTreeItem(Path path){
		Optional<TreeItem<Path>> cand=tree.getRoot().getChildren().stream().filter((p)->path.startsWith(p.getValue())).findAny();
		return cand.isPresent()?getTreeItem(path,cand.get()):null;
	}
	private TreeItem<Path> getTreeItem(Path path,TreeItem<Path> start){
		while(true){
			Path curr=start.getValue();
			if(path.equals(curr))
				return start;
			Path next=path.getName(curr.getNameCount());
			Optional<TreeItem<Path>> cand=start.getChildren().stream().filter((p)->p.getValue().endsWith(next)).findAny();
			if(cand.isPresent()){
				start=cand.get();
			}else{
				return null;
			}
		}
	}
	public void close(){
		active=false;
		updateThread.interrupt();
	}
	private TreeItem<Path> createTreeItem(Path path){
		if(Files.isDirectory(path)){
			if(watchService!=null)
				try{
					path.register(watchService,StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_DELETE,
							StandardWatchEventKinds.ENTRY_MODIFY,StandardWatchEventKinds.OVERFLOW);
				}catch(IOException ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
				}
			return new LazyTreeItem<Path>(()->getChildren(path),path);
		}else{
			return new TreeItem<>(path);
		}
	}
	private Collection<TreeItem<Path>> getChildren(Path item){
		try{
			return Files.list(item).sorted().map((path)->createTreeItem(path)).collect(Collectors.toList());
		}catch(IOException ex){
			return Collections.emptyList();
		}
	}
	private <T> void createColumnChooser(String name,Callback<TreeTableColumn.CellDataFeatures<Path,T>,ObservableValue<T>> callback,boolean visible){
		TreeTableColumn<Path,T> column=new TreeTableColumn<>(name);
		column.setCellValueFactory(callback);
		column.setEditable(true);
		tree.getColumns().add(column);
		column.setVisible(visible);
	}
	public void setAction(Consumer<Collection<Path>> action){
		this.action=action;
	}
	public Consumer<Collection<Path>> getAction(){
		return action;
	}
	public void fireAction(){
		if(action!=null)
			action.accept(getSelectedPaths());
	}
	public void markPaths(){
		marked=getSelectedPaths();
	}
	public Collection<Path> getMarkedPaths(){
		return marked;
	}
	public Collection<Path> getCurrentDirectories(){
		return tree.getSelectionModel().getSelectedItems().stream().map((item)->item.getValue()).
				map((path)->Files.isDirectory(path)?path:path.getParent()).collect(Collectors.toSet());
	}
	@Override
	public void requestFocus(){
		tree.requestFocus();
	}
	public Collection<Path> getSelectedPaths(){
		return tree.getSelectionModel().getSelectedItems().stream().map((item)->item.getValue()).collect(Collectors.toSet());
	}
	TreeTableView<Path> getTree(){
		return tree;
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
	public static void main(String[] args) throws IOException, InterruptedException{
		/*WatchService service=FileSystems.getDefault().newWatchService();
		new File("/home/kwong/NetBeansProjects/jtk/").toPath().register(service,
				StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_DELETE,StandardWatchEventKinds.ENTRY_MODIFY);
		for(int i=0;i<100;i++){
			WatchKey key=service.poll(20,TimeUnit.SECONDS);
			if(key==null)
				return;
			System.out.println("");
			key.pollEvents().forEach((WatchEvent e)->System.out.println(key.watchable()+""+e.context()));
			key.reset();
		}*/
		System.out.println(new File("/").toPath().getNameCount());
	}
}
