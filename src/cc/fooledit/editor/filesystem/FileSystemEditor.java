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
import cc.fooledit.*;
import cc.fooledit.control.*;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import com.sun.javafx.scene.control.skin.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.collections.*;
import javafx.scene.Node;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FileSystemEditor implements DataEditor<FileSystemObject>{
	public static final FileSystemEditor INSTANCE=new FileSystemEditor();
	private final MenuRegistry menuRegistry=new MenuRegistry(FileSystemModule.NAME);
	private final RegistryNode<String,Command,String> commandRegistry=Registry.ROOT.registerCommand(FileSystemModule.NAME);
	private final NavigableRegistryNode<String,String,String> keymapRegistry=Registry.ROOT.registerKeymap(FileSystemModule.NAME);
	private FileSystemEditor(){
		addCommand("focus-previous",(viewer)->viewer.getTree().getFocusModel().focusPrevious());
		addCommand("focus-next",(viewer)->viewer.getTree().getFocusModel().focusNext());
		addCommand("focus-first",(viewer)->viewer.getTree().getFocusModel().focus(0));
		addCommand("focus-last",(viewer)->viewer.getTree().getFocusModel().focus(viewer.getTree().getExpandedItemCount()-1));
		addCommand("focus-first-in-directory",(viewer)->focusBeginOfDirectory(viewer.getTree()));
		addCommand("focus-last-in-directory",(viewer)->focusEndOfDirectory(viewer.getTree()));
		addCommand("focus-up",(viewer)->focusUp(viewer.getTree()));
		addCommand("focus-down",(viewer)->focusDown(viewer.getTree()));
		addCommand("move-to-previous-page",(viewer)->moveToPreviousPage(viewer.getTree()));
		addCommand("move-to-next-page",(viewer)->moveToNextPage(viewer.getTree()));
		addCommand("select",(viewer)->viewer.getTree().getSelectionModel().select(viewer.getTree().getFocusModel().getFocusedIndex()));
		addCommand("select-to",(viewer)->selectTo(viewer.getTree()));
		addCommand("deselect",(viewer)->viewer.getTree().getSelectionModel().clearSelection(viewer.getTree().getFocusModel().getFocusedIndex()));
		addCommand("clear-selection",(viewer)->viewer.getTree().getSelectionModel().clearSelection());
		addCommand("toggle-selection",(viewer)->toggleSelection(viewer.getTree()));
		addCommand("delete",(viewer)->viewer.getSelectedPaths().forEach((path)->delete(path)));
		addCommand("expand",(viewer)->viewer.getTree().getTreeItem(viewer.getTree().getSelectionModel().getFocusedIndex()).setExpanded(true));
		addCommand("fold",(viewer)->viewer.getTree().getTreeItem(viewer.getTree().getSelectionModel().getFocusedIndex()).setExpanded(false));
		addCommand("mark",(viewer)->viewer.markPaths());
		addCommand("submit",(viewer)->viewer.fireAction());
		addCommand("rename",(viewer)->viewer.getSelectedPaths().forEach((path)->rename(path)));
		addCommand("move",(viewer)->viewer.getMarkedPaths().forEach((from)->viewer.getCurrentDirectories().forEach((dir)->move(from,dir))));
		addCommand("copy",(viewer)->viewer.getMarkedPaths().forEach((from)->viewer.getCurrentDirectories().forEach((dir)->copy(from,dir))));
		addCommand("symbolic-link",(viewer)->viewer.getMarkedPaths().forEach((from)->viewer.getCurrentDirectories().forEach((dir)->symbolicLink(from,dir))));
		addCommand("hard-link",(viewer)->viewer.getMarkedPaths().forEach((from)->viewer.getCurrentDirectories().forEach((dir)->hardLink(from,dir))));
		addCommand("create-directory",(viewer)->viewer.getCurrentDirectories().forEach((path)->createDirectory(path)));
		addCommand("create-file",(viewer)->viewer.getCurrentDirectories().forEach((path)->createFile(path)));
	}
	private void addCommand(String name,Consumer<FileSystemViewer> action){
		commandRegistry.addChild(name,new Command(name,()->action.accept((FileSystemViewer)Main.INSTANCE.getCurrentNode()),FileSystemModule.NAME));
	}
	private void moveToPreviousPage(TreeTableView<Path> tree){
		int newIndex=((TreeTableViewSkin)tree.getSkin()).onScrollPageUp(true);
		tree.getFocusModel().focus(newIndex);
	}
	private void moveToNextPage(TreeTableView<Path> tree){
		int newIndex=((TreeTableViewSkin)tree.getSkin()).onScrollPageDown(true);
		tree.getFocusModel().focus(newIndex);
	}
	private void focusUp(TreeTableView<Path> tree){
		TreeItem<Path> curr=tree.getFocusModel().getFocusedItem();
		if(curr.isExpanded()){
			curr.setExpanded(false);
		}else{
			tree.getFocusModel().focus(tree.getRow(curr.getParent()));
		}
	}
	private void focusDown(TreeTableView<Path> tree){
		TreeItem<Path> curr=tree.getFocusModel().getFocusedItem();
		if(curr.isExpanded()){
			if(!curr.getChildren().isEmpty()){
				tree.getFocusModel().focus(tree.getRow(curr.getChildren().get(0)));
			}
		}else if(!curr.isLeaf()){
			curr.setExpanded(true);
			tree.scrollTo(tree.getFocusModel().getFocusedIndex());
		}
	}
	private static void focusBeginOfDirectory(TreeTableView<Path> tree){
		TreeItem<Path> curr=tree.getFocusModel().getFocusedItem();
		tree.getFocusModel().focus(tree.getRow(curr.getParent().getChildren().get(0)));
	}
	private static void focusEndOfDirectory(TreeTableView<Path> tree){
		TreeItem<Path> curr=tree.getFocusModel().getFocusedItem();
		ObservableList<TreeItem<Path>> sibling=curr.getParent().getChildren();
		tree.getFocusModel().focus(tree.getRow(sibling.get(sibling.size()-1)));
	}
	private static void toggleSelection(TreeTableView<Path> tree){
		int curr=tree.getFocusModel().getFocusedIndex();
		if(tree.getSelectionModel().isSelected(curr))
			tree.getSelectionModel().clearSelection(curr);
		else
			tree.getSelectionModel().select(curr);
	}
	private static void selectTo(TreeTableView<Path> tree){
		int last=tree.getSelectionModel().getSelectedIndex();
		int curr=tree.getFocusModel().getFocusedIndex();
		tree.getSelectionModel().selectRange(Math.min(last,curr),Math.max(last,curr)+1);
	}
	private static final void delete(Path path){
		try{
			if(Files.isDirectory(path)){
				Files.newDirectoryStream(path).forEach((p)->delete(p));
			}
			Files.delete(path);
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private static void createDirectory(Path from){
		Main.INSTANCE.getMiniBuffer().setMode((name)->{
			try{
				Path to=from.resolve(name);
				Files.createDirectory(to);
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
			Main.INSTANCE.getMiniBuffer().restore();
			Main.INSTANCE.getCurrentNode().requestFocus();
		},null,"",new Label(MessageRegistry.getString("NAME",FileSystemModule.NAME)),null);
	}
	private static void createFile(Path from){
		Main.INSTANCE.getMiniBuffer().setMode((name)->{
			try{
				Path to=from.resolve(name);
				Files.createFile(to);
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
			Main.INSTANCE.getMiniBuffer().restore();
			Main.INSTANCE.getCurrentNode().requestFocus();
		},null,"",new Label(MessageRegistry.getString("NAME",FileSystemModule.NAME)),null);
	}
	private static void rename(Path from){
		Main.INSTANCE.getMiniBuffer().setMode((name)->{
			try{
				Path to=from.getParent().resolve(name);
				if(Files.exists(to))
					onOverride(()->{
						try{
							Files.move(from,to,StandardCopyOption.REPLACE_EXISTING);
						}catch(IOException ex){
							Logger.getGlobal().log(Level.SEVERE,null,ex);
						}
					});
				else{
					Files.move(from,to);
					Main.INSTANCE.getMiniBuffer().restore();
					Main.INSTANCE.getCurrentNode().requestFocus();
				}
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		},null,from.getFileName().toString(),new Label(MessageRegistry.getString("RENAME_TO",FileSystemModule.NAME)),null);
	}
	private static void move(Path from,Path dir){
		fileToDirectory(from,dir,(f,t,o)->{
			if(o)
				Files.move(f,t,StandardCopyOption.REPLACE_EXISTING);
			else
				Files.move(f,t);
		});
	}
	private static void symbolicLink(Path from,Path dir){
		fileToDirectory(from,dir,(f,t,o)->{
			Files.createSymbolicLink(t,f);
		});
	}
	private static void hardLink(Path from,Path dir){
		fileToDirectory(from,dir,(f,t,o)->{
			Files.createLink(t,f);
		});
	}
	private static void copy(Path from,Path dir){
		fileToDirectory(from,dir,(f,t,o)->{
			if(o)
				Files.copy(f,t,StandardCopyOption.REPLACE_EXISTING);
			else
				Files.copy(f,t);
		});
	}
	private static void fileToDirectory(Path from,Path dir,FileToDirectoryAction action){
		try{
			Path to=dir.resolve(from.getFileName());
			if(Files.exists(to))
				onOverride(()->{
					try{
						action.apply(from,to,true);
					}catch(IOException ex){
						Logger.getGlobal().log(Level.SEVERE,null,ex);
					}
				});
			else{
				action.apply(from,to,false);
				Main.INSTANCE.getMiniBuffer().restore();
				Main.INSTANCE.getCurrentNode().requestFocus();
			}
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private static void onOverride(Runnable action){
		String yes=MessageRegistry.getString("YES",FileSystemModule.NAME);
		String no=MessageRegistry.getString("NO",FileSystemModule.NAME);
		Main.INSTANCE.getMiniBuffer().setMode((ans)->{
			if(ans.equals(yes))
				action.run();
			Main.INSTANCE.getMiniBuffer().restore();
		},AutoCompleteProvider.createSimple(Arrays.asList(AutoCompleteHint.create(yes,yes,""),AutoCompleteHint.create(no,no,"")))
		,"",new Label(MessageRegistry.getString("OVERRIDE_EXIST",FileSystemModule.NAME)),null);
		Main.INSTANCE.getMiniBuffer().restore();
		Main.INSTANCE.getCurrentNode().requestFocus();
	}
	@Override
	public Node edit(FileSystemObject data,Object remark,RegistryNode<String,Object,String> meta){
		if(remark!=null&&remark instanceof List){
			((List<String>)remark).forEach((path)->{
				if(path!=null)
					data.getPaths().add(new File(path).toPath());
			});
		}
		FileSystemViewer viewer=new FileSystemViewer(data);
		return viewer;
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("FILE_SYSTEM_VIEWER",FileSystemModule.NAME);
	}
	@Override
	public Object getRemark(Node node){
		return ((FileSystemViewer)node).getSelectedPaths().stream().map((path)->path.toAbsolutePath().toString()).collect(Collectors.toList());
	}
	@Override
	public RegistryNode<String,Command,String> getCommandRegistry(){
		return commandRegistry;
	}
	@Override
	public NavigableRegistryNode<String,String,String> getKeymapRegistry(){
		return keymapRegistry;
	}
	@Override
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
	public static interface FileToDirectoryAction{
		void apply(Path from,Path to,boolean override)throws IOException;
	}
}
