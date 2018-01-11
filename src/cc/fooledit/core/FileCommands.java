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
package cc.fooledit.core;
import cc.fooledit.editor.filesystem.FileSystemObjectType;
import cc.fooledit.editor.filesystem.FileSystemObject;
import cc.fooledit.*;
import cc.fooledit.control.*;
import cc.fooledit.spi.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.logging.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FileCommands{
	private FileCommands(){

	}
	public static void open(){
		RegistryNode<String,Object,String> files=DataObjectRegistry.create(FileSystemObjectType.INSTANCE);
		FileSystemObject data=(FileSystemObject)files.getChild(DataObject.DATA);
		data.setInitialPath(guessDefaultPath());
		data.setAction((paths)->{
			paths.forEach((p)->{
				try{
					Main.INSTANCE.show(DataObjectRegistry.readFrom(p.toUri().toURL()));
				}catch(Exception ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
				}
			});
			DataObjectRegistry.removeDataObject(files);
		});
		files.addChild(DataObject.DEFAULT_NAME,MessageRegistry.getString("OPEN_FILE",CoreModule.NAME));
		DataObjectRegistry.addDataObject(files);
		Main.INSTANCE.show(files);
	}
	public static void openUrl(){
		Main.INSTANCE.getMiniBuffer().setMode((url)->{
			try{
				Main.INSTANCE.show(DataObjectRegistry.readFrom(new URL(url)));
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		},null,"",new Label("URL:"),null);
	}
	public static void save(){
		RegistryNode<String,Object,String> data=Main.INSTANCE.getCurrentDataObject();
		String url=(String)data.getChild(DataObject.URI);
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
		RegistryNode<String,Object,String> files=DataObjectRegistry.create(FileSystemObjectType.INSTANCE);
		FileSystemObject data=(FileSystemObject)files.getChild(DataObject.DATA);
		data.setInitialPath(guessDefaultPath());
		data.setAction((paths)->{
			paths.forEach((p)->saveAs(p));
			DataObjectRegistry.removeDataObject(files);
		});
		files.addChild(DataObject.DEFAULT_NAME,MessageRegistry.getString("SAVE",CoreModule.NAME));
		DataObjectRegistry.addDataObject(files);
		Main.INSTANCE.show(files);
	}
	public static void saveAs(Path p){
		RegistryNode<String,Object,String> object=Main.INSTANCE.getCurrentDataObject();
		DataObject data=Main.INSTANCE.getCurrentData();
		try{
			data.getDataObjectType().writeTo(data,p.toUri().toURL().openConnection(),object);
			object.addChild(DataObject.URI,p.toUri().toString());
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private static Path guessDefaultPath(){
		try{
			return new File(new URI((String)Main.INSTANCE.getCurrentDataObject().getChild(DataObject.URI))).toPath();
		}catch(Exception ex){
			return null;
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
		RegistryNode<String,Object,String> templateEditor=DataObjectRegistry.create(TemplateEditor.INSTANCE);
		DataObjectRegistry.addDataObject(templateEditor);
		Main.INSTANCE.show(templateEditor);
	}
}