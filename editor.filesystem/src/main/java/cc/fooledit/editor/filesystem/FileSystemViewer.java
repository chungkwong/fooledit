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
package cc.fooledit.editor.filesystem;
import cc.fooledit.control.*;
import cc.fooledit.core.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FileSystemViewer extends BorderPane{
	private final TreeTableView<Path> tree=new TreeTableWrapper<>();
	private WatchService watchService;
	private final Thread updateThread=new Thread(()->refresh());
	private boolean active=true;
	private Collection<Path> marked=Collections.emptySet();
	private FileSystemObject data;
	public FileSystemViewer(FileSystemObject data){
		this.data=data;
		try{
			watchService=FileSystems.getDefault().newWatchService();
			updateThread.setDaemon(true);
			updateThread.start();
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			watchService=null;
		}
		Stream<Path> roots=StreamSupport.stream(Spliterators.spliteratorUnknownSize(FileSystems.getDefault().getRootDirectories().iterator(),0),false);
		TreeItem<Path> root=new LazyTreeItem<Path>(null,()->roots.sorted().map((r)->createTreeItem(r)).collect(Collectors.toList()));
		tree.setShowRoot(false);
		tree.setRoot(root);
		tree.setTableMenuButtonVisible(true);
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		setCenter(tree);
		this.<String>createColumnChooser(MessageRegistry.getString("NAME",Activator.class),(param)
				->new ReadOnlyStringWrapper(getFileName(param.getValue().getValue())),true);
		this.<String>createColumnChooser(MessageRegistry.getString("OWNER",Activator.class),(param)
				->new ReadOnlyStringWrapper(getOwnerName(param.getValue().getValue())),true);
		this.<Boolean>createColumnChooser(MessageRegistry.getString("READABLE",Activator.class),(param)
				->new ReadOnlyBooleanWrapper(Files.isReadable(param.getValue().getValue())),true);
		this.<Boolean>createColumnChooser(MessageRegistry.getString("WRITABLE",Activator.class),(param)
				->new ReadOnlyBooleanWrapper(Files.isWritable(param.getValue().getValue())),true);
		this.<Boolean>createColumnChooser(MessageRegistry.getString("EXECUTABLE",Activator.class),(param)
				->new ReadOnlyBooleanWrapper(Files.isExecutable(param.getValue().getValue())),true);
		this.<Boolean>createColumnChooser(MessageRegistry.getString("HIDDEN",Activator.class),(param)
				->new ReadOnlyBooleanWrapper(isHidden(param.getValue().getValue())),true);
		this.<String>createColumnChooser(MessageRegistry.getString("LAST_MODIFIED",Activator.class),(param)
				->new ReadOnlyStringWrapper(getLastModified(param.getValue().getValue())),true);
		this.<Number>createColumnChooser(MessageRegistry.getString("SIZE",Activator.class),(param)
				->new ReadOnlyLongWrapper(getSize(param.getValue().getValue())),true);
		this.<String>createColumnChooser(MessageRegistry.getString("SYMBOLIC_LINK",Activator.class),(param)
				->new ReadOnlyStringWrapper(getLinkTarget(param.getValue().getValue())),false);
		((TreeTableColumn<Path,String>)tree.getColumns().get(0)).setCellFactory((p)->new FileCell());
		((TreeTableColumn<Path,String>)tree.getColumns().get(0)).prefWidthProperty().bind(tree.widthProperty().multiply(0.4));
		tree.setEditable(true);
		tree.getFocusModel().focusedIndexProperty().addListener(
				(e,o,n)->tree.scrollTo(n.intValue()));
		data.getPaths().forEach((path)->selectPath(path));
		tree.getSelectionModel().getSelectedItems().addListener(
				(ListChangeListener.Change<? extends TreeItem<Path>> c)->{
					data.getPaths().setAll(c.getList().stream().map((item)->item.getValue()).collect(Collectors.toSet()));
				}
		);
	}
	private static String getFileName(Path path){
		Path name=path.getFileName();
		return name==null?"":name.toString();
	}
	private static String getOwnerName(Path path){
		try{
			return Files.getOwner(path,LinkOption.NOFOLLOW_LINKS).getName();
		}catch(IOException ex){
			return MessageRegistry.getString("UNKNOWN",Activator.class);
		}
	}
	private static String getLastModified(Path path){
		try{
			return Files.getLastModifiedTime(path).toString();
		}catch(IOException ex){
			return MessageRegistry.getString("UNKNOWN",Activator.class);
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
				TreeItem<Path> item=getTreeItem(path,false);
				if(item==null||!item.isExpanded()){
					continue;
				}
				for(WatchEvent event:key.pollEvents()){
					if(event.kind()==StandardWatchEventKinds.OVERFLOW){
						((LazyTreeItem<Path>)item).refresh();
					}else{
						Path file=(Path)event.context();
						TreeItem<Path> sub=getTreeItem(path.resolve(file),item,false);
						if(event.kind()==StandardWatchEventKinds.ENTRY_CREATE){
							if(sub==null){
								item.getChildren().add(createTreeItem(path.resolve(file)));
							}else{
								item.getChildren().set(item.getChildren().indexOf(sub),sub);
							}
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
	public void focusPath(Path path){
		TreeItem<Path> treeItem=getTreeItem(path,true);
		if(treeItem!=null){
			tree.getFocusModel().focus(tree.getRow(treeItem));
		}
	}
	public void selectPath(Path path){
		TreeItem<Path> treeItem=getTreeItem(path,true);
		if(treeItem!=null){
			tree.getSelectionModel().select(tree.getRow(treeItem));
		}
	}
	private TreeItem<Path> getTreeItem(Path path,boolean expand){
		Optional<TreeItem<Path>> cand=tree.getRoot().getChildren().stream().filter((p)->path.startsWith(p.getValue())).findAny();
		return cand.isPresent()?getTreeItem(path,cand.get(),expand):null;
	}
	private TreeItem<Path> getTreeItem(Path path,TreeItem<Path> start,boolean expand){
		while(true){
			Path curr=start.getValue();
			if(path.equals(curr)){
				return start;
			}
			Path next=path.getName(curr.getNameCount());
			if(expand){
				start.setExpanded(true);
			}
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
			if(watchService!=null){
				try{
					path.register(watchService,StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_DELETE,
							StandardWatchEventKinds.ENTRY_MODIFY,StandardWatchEventKinds.OVERFLOW);
				}catch(IOException ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
				}
			}
			return new LazyTreeItem<Path>(path,()->getChildren(path));
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
	public void fireAction(){
		Consumer<Collection<Path>> action=data.getAction();
		if(action!=null){
			action.accept(getSelectedPaths());
		}
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
	public final Collection<Path> getSelectedPaths(){
		return data.getPaths();
	}
	TreeTableView<Path> getTree(){
		return tree;
	}
	class FileCell extends TreeTableCell<Path,String>{
		public FileCell(){
			setOnDragDetected((e)->{
				Dragboard board=tree.startDragAndDrop(TransferMode.COPY,TransferMode.LINK,TransferMode.MOVE);
				ClipboardContent content=new ClipboardContent();
				content.putFiles(getSelectedPaths().stream().map((p)->p.toFile()).collect(Collectors.toList()));
				board.setContent(content);
				e.consume();
			});
			setOnDragOver((e)->{
				if(e.getDragboard().hasFiles()&&Files.isDirectory(getTreeTableRow().getTreeItem().getValue())){
					e.acceptTransferModes(TransferMode.MOVE,TransferMode.COPY,TransferMode.LINK);
				}else{
					e.acceptTransferModes();
				}
				e.consume();
			});
			setOnDragDropped((e)->{
				Dragboard board=e.getDragboard();
				if(board.hasFiles()){
					try{
						for(File f:board.getFiles()){
							Path to=getTreeTableRow().getItem().resolve(f.getName());
							if(e.getTransferMode().equals(TransferMode.MOVE)){
								Files.move(f.toPath(),to);
							}else if(e.getTransferMode().equals(TransferMode.COPY)){
								Files.copy(f.toPath(),to);
							}else if(e.getTransferMode().equals(TransferMode.LINK)){
								Files.createLink(to,f.toPath());
							}
						}
						e.setDropCompleted(true);
					}catch(IOException ex){
						Logger.getGlobal().log(Level.SEVERE,null,ex);
						e.setDropCompleted(false);
					}
				}else{
					e.setDropCompleted(false);
				}
			});
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
				if(Files.isDirectory(path)){
					setGraphic(new ImageView(FOLDER));
				}else if(Files.isRegularFile(path)){
					setGraphic(new ImageView(REGULAR));
				}else{
					setGraphic(null);
				}
			}
		}
	}
	private static Image FOLDER, REGULAR;
	static{
		FOLDER=new Image(FileSystemViewer.class.getResourceAsStream("/folder.png"));
		REGULAR=new Image(FileSystemViewer.class.getResourceAsStream("/regular.png"));
	}
	public static void main(String[] args) throws IOException,InterruptedException{
		/*WatchService service=FileSystems.getDefault().newWatchService();
		new File("/home/kwong/NetBeansProjects/fooledit/").toPath().register(service,
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
