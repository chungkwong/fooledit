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
import cc.fooledit.spi.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.scene.Node;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FileSystemEditor implements DataEditor<FileSystemObject>{
	public static final FileSystemEditor INSTANCE=new FileSystemEditor();
	private final MenuRegistry menuRegistry=Registry.ROOT.registerMenu(Activator.class);
	private final RegistryNode<String,Command> commandRegistry=Registry.ROOT.registerCommand(Activator.NAME);
	private final NavigableRegistryNode<String,String> keymapRegistry=Registry.ROOT.registerKeymap(Activator.class);
	private FileSystemEditor(){
		addCommand("delete",(viewer)->viewer.getSelectedPaths().forEach((path)->delete(path)));
		addCommand("mark",(viewer)->viewer.markPaths());
		addCommand("submit",(viewer)->viewer.fireAction());
		addCommand("rename",(viewer)->viewer.getSelectedPaths().forEach((path)->rename(path)));
		addCommand("move",(viewer)->viewer.getMarkedPaths().forEach((from)->viewer.getCurrentDirectories().forEach((dir)->move(from,dir))));
		addCommand("copy",(viewer)->viewer.getMarkedPaths().forEach((from)->viewer.getCurrentDirectories().forEach((dir)->copy(from,dir))));
		addCommand("symbolic-link",(viewer)->viewer.getMarkedPaths().forEach((from)->viewer.getCurrentDirectories().forEach((dir)->symbolicLink(from,dir))));
		addCommand("hard-link",(viewer)->viewer.getMarkedPaths().forEach((from)->viewer.getCurrentDirectories().forEach((dir)->hardLink(from,dir))));
		addCommand("create-directory",(viewer)->viewer.getCurrentDirectories().forEach((path)->createDirectory(path)));
		addCommand("create-file",(viewer)->viewer.getCurrentDirectories().forEach((path)->createFile(path)));
		addCommand("open",(viewer)->viewer.getSelectedPaths().forEach((p)->{
			try{
				Main.INSTANCE.showOnNewTab(DataObjectRegistry.readFrom(p.toUri().toURL()));
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		}));
	}
	private void addCommand(String name,Consumer<FileSystemViewer> action){
		commandRegistry.put(name,new Command(name,()->action.accept((FileSystemViewer)Main.INSTANCE.getCurrentNode()),Activator.class));
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
		},null,"",new Label(MessageRegistry.getString("NAME",Activator.class)),null);
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
		},null,"",new Label(MessageRegistry.getString("NAME",Activator.class)),null);
	}
	private static void rename(Path from){
		Main.INSTANCE.getMiniBuffer().setMode((name)->{
			try{
				Path to=from.getParent().resolve(name);
				if(Files.exists(to)){
					onOverride(()->{
						try{
							Files.move(from,to,StandardCopyOption.REPLACE_EXISTING);
						}catch(IOException ex){
							Logger.getGlobal().log(Level.SEVERE,null,ex);
						}
					});
				}else{
					Files.move(from,to);
					Main.INSTANCE.getMiniBuffer().restore();
					Main.INSTANCE.getCurrentNode().requestFocus();
				}
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		},null,from.getFileName().toString(),new Label(MessageRegistry.getString("RENAME_TO",Activator.class)),null);
	}
	private static void move(Path from,Path dir){
		fileToDirectory(from,dir,(f,t,o)->{
			if(o){
				Files.move(f,t,StandardCopyOption.REPLACE_EXISTING);
			}else{
				Files.move(f,t);
			}
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
			if(o){
				Files.copy(f,t,StandardCopyOption.REPLACE_EXISTING);
			}else{
				Files.copy(f,t);
			}
		});
	}
	private static void fileToDirectory(Path from,Path dir,FileToDirectoryAction action){
		try{
			Path to=dir.resolve(from.getFileName());
			if(Files.exists(to)){
				onOverride(()->{
					try{
						action.apply(from,to,true);
					}catch(IOException ex){
						Logger.getGlobal().log(Level.SEVERE,null,ex);
					}
				});
			}else{
				action.apply(from,to,false);
				Main.INSTANCE.getMiniBuffer().restore();
				Main.INSTANCE.getCurrentNode().requestFocus();
			}
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private static void onOverride(Runnable action){
		String yes=MessageRegistry.getString("YES",Activator.class);
		String no=MessageRegistry.getString("NO",Activator.class);
		Main.INSTANCE.getMiniBuffer().setMode((ans)->{
			if(ans.equals(yes)){
				action.run();
			}
			Main.INSTANCE.getMiniBuffer().restore();
		},AutoCompleteProvider.createSimple(Arrays.asList(AutoCompleteHint.create(yes,yes,""),AutoCompleteHint.create(no,no,""))),
				"",new Label(MessageRegistry.getString("OVERRIDE_EXIST",Activator.class)),null);
		Main.INSTANCE.getMiniBuffer().restore();
		Main.INSTANCE.getCurrentNode().requestFocus();
	}
	@Override
	public Node edit(FileSystemObject data,Object remark,RegistryNode<String,Object> meta){
		if(remark!=null&&remark instanceof ListRegistryNode){
			((ListRegistryNode<String>)remark).getChildren().forEach((path)->{
				if(path!=null){
					File file=new File(path);
					if(file.exists()){
						data.getPaths().add(file.toPath());
					}
				}
			});
		}
		FileSystemViewer viewer=new FileSystemViewer(data);
		return viewer;
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("FILE_SYSTEM_VIEWER",Activator.class);
	}
	@Override
	public Object getRemark(Node node){
		List<String> selected=((FileSystemViewer)node).getSelectedPaths().stream().map((path)->path.toAbsolutePath().toString()).collect(Collectors.toList());
		return new ListRegistryNode<>(selected);
	}
	@Override
	public RegistryNode<String,Command> getCommandRegistry(){
		return commandRegistry;
	}
	@Override
	public NavigableRegistryNode<String,String> getKeymapRegistry(){
		return keymapRegistry;
	}
	@Override
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
	public static interface FileToDirectoryAction{
		void apply(Path from,Path to,boolean override) throws IOException;
	}
}
