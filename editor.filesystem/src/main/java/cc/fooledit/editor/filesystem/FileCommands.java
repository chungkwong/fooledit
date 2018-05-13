/*
 * Copyright (C) 2018 kwong
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
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;
import javafx.scene.control.*;
/**
 *
 * @author kwong
 */
public class FileCommands{
	private FileCommands(){

	}
	public static void open(){
		RegistryNode<String,Object> files=DataObjectRegistry.create(FileSystemObjectType.INSTANCE);
		FileSystemObject data=(FileSystemObject)files.get(DataObject.DATA);
		data.getPaths().setAll(guessDefaultPath());
		data.setAction((paths)->{
			paths.forEach((p)->{
				try{
					Main.INSTANCE.showOnCurrentTab(DataObjectRegistry.readFrom(p.toUri().toURL()));
				}catch(Exception ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
				}
			});
			DataObjectRegistry.removeDataObject(files);
		});
		files.put(DataObject.DEFAULT_NAME,MessageRegistry.getString("OPEN_FILE",CoreModule.NAME));
		DataObjectRegistry.addDataObject(files);
		Main.INSTANCE.showOnNewTab(files);
	}
	public static void openUrl(){
		Main.INSTANCE.getMiniBuffer().setMode((url)->{
			try{
				Main.INSTANCE.showOnNewTab(DataObjectRegistry.readFrom(new URL(url)));
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
			Main.INSTANCE.getMiniBuffer().restore();
		},null,"",new Label("URL:"),null);
	}
	public static void save(){
		RegistryNode<String,Object> data=Main.INSTANCE.getCurrentDataObject();
		String url=(String)data.get(DataObject.URI);
		if(url==null)
			saveAs();
		else
			try{
				DataObjectRegistry.write(data);
			}catch(Exception ex){
				Logger.getLogger(FileCommands.class.getName()).log(Level.SEVERE,null,ex);
			}
	}
	public static void saveAs(){
		RegistryNode<String,Object> files=DataObjectRegistry.create(FileSystemObjectType.INSTANCE);
		FileSystemObject data=(FileSystemObject)files.get(DataObject.DATA);
		data.getPaths().setAll(guessDefaultPath());
		data.setAction((paths)->{
			paths.forEach((p)->saveAs(p));
			DataObjectRegistry.removeDataObject(files);
		});
		files.put(DataObject.DEFAULT_NAME,MessageRegistry.getString("SAVE",CoreModule.NAME));
		DataObjectRegistry.addDataObject(files);
		Main.INSTANCE.showOnNewTab(files);
	}
	public static void saveAs(Path p){
		RegistryNode<String,Object> object=Main.INSTANCE.getCurrentDataObject();
		DataObject data=Main.INSTANCE.getCurrentData();
		try{
			data.getDataObjectType().writeTo(data,p.toUri().toURL().openConnection(),object);
			object.put(DataObject.URI,p.toUri().toString());
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private static Collection<Path> guessDefaultPath(){
		try{
			return Collections.singletonList(new File(new URI((String)Main.INSTANCE.getCurrentDataObject().get(DataObject.URI))).toPath());
		}catch(Exception ex){
			return Collections.emptyList();
		}
	}
	private static String extractFilename(URL url){
		String path=url.getPath();
		int index=path.lastIndexOf('/');
		if(index!=-1)
			path=path.substring(index+1);
		return path;
	}
	public static void create(){
		RegistryNode<String,Object> templateEditor=DataObjectRegistry.create(TemplateEditor.INSTANCE);
		DataObjectRegistry.addDataObject(templateEditor);
		Main.INSTANCE.showOnNewTab(templateEditor);
	}
}
