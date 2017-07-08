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
import com.github.chungkwong.fooledit.model.*;
import com.github.chungkwong.fooledit.setting.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import javafx.scene.*;
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
	@Override
	public Node edit(Prompt data){
		return new FileSystemViewer();
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("FILE_SYSTEM");
	}
}
