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
import com.github.chungkwong.fooledit.model.*;
import com.github.chungkwong.fooledit.setting.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import javafx.scene.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FileSystemPrompt extends Prompt{
	private final MenuRegistry menuRegistry=new MenuRegistry();
	private final CommandRegistry commandRegistry=new CommandRegistry();
	private final KeymapRegistry keymapRegistry=new KeymapRegistry();
	public FileSystemPrompt(){
		addCommand("delete",(viewer)->viewer.getSelectedPaths().forEach((path)->delete(path)));
		addCommand("expand",(viewer)->viewer.getTree().getSelectionModel().getSelectedItem().setExpanded(true));
		addCommand("mark",(viewer)->viewer.markPaths());
		addCommand("submit",(viewer)->viewer.fireAction());
		addCommand("rename",(viewer)->viewer.getSelectedPaths().forEach((path)->rename(path)));
		addCommand("move",(viewer)->viewer.getMarkedPaths().forEach((from)->viewer.getCurrentDirectories().forEach((dir)->move(from,dir))));
		addCommand("copy",(viewer)->viewer.getMarkedPaths().forEach((from)->viewer.getCurrentDirectories().forEach((dir)->copy(from,dir))));
		addCommand("symbolic-link",(viewer)->viewer.getMarkedPaths().forEach((from)->viewer.getCurrentDirectories().forEach((dir)->symbolicLink(from,dir))));
		addCommand("hard-link",(viewer)->viewer.getMarkedPaths().forEach((from)->viewer.getCurrentDirectories().forEach((dir)->hardLink(from,dir))));
		addCommand("create-directory",(viewer)->viewer.getCurrentDirectories().forEach((path)->createDirectory(path)));
		addCommand("create-file",(viewer)->viewer.getCurrentDirectories().forEach((path)->createFile(path)));
		menuRegistry.setMenus(Main.loadJSON((File)SettingManager.getOrCreate(FileSystemModule.NAME).get("menubar-file",null)));
		keymapRegistry.registerKeys((Map<String,String>)(Object)Main.loadJSON((File)SettingManager.getOrCreate(FileSystemModule.NAME).get("keymap-file",null)));
	}
	private void addCommand(String name,Consumer<FileSystemViewer> action){
		commandRegistry.put(name,()->action.accept((FileSystemViewer)Main.INSTANCE.getCurrentNode()));
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
		},null,"",new Label(MessageRegistry.getString("NAME")),null);
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
		},null,"",new Label(MessageRegistry.getString("NAME")),null);
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
		},null,from.getFileName().toString(),new Label(MessageRegistry.getString("RENAME_TO")),null);
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
		String yes=MessageRegistry.getString("YES");
		String no=MessageRegistry.getString("NO");
		Main.INSTANCE.getMiniBuffer().setMode((ans)->{
			if(ans.equals(yes))
				action.run();
			Main.INSTANCE.getMiniBuffer().restore();
		},AutoCompleteProvider.createSimple(Arrays.asList(AutoCompleteHint.create(yes,yes,""),AutoCompleteHint.create(no,no,"")))
		,"",new Label(MessageRegistry.getString("OVERRIDE_EXIST")),null);
		Main.INSTANCE.getMiniBuffer().restore();
		Main.INSTANCE.getCurrentNode().requestFocus();
	}
	@Override
	public Node edit(Prompt data){
		return new FileSystemViewer();
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("FILE_SYSTEM");
	}
	@Override
	public CommandRegistry getCommandRegistry(){
		return commandRegistry;
	}
	@Override
	public KeymapRegistry getKeymapRegistry(){
		return keymapRegistry;
	}
	@Override
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
}
interface FileToDirectoryAction{
	void apply(Path from,Path to,boolean override)throws IOException;
}