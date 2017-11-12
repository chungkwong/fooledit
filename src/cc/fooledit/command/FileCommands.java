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
package cc.fooledit.command;
import cc.fooledit.*;
import cc.fooledit.api.*;
import cc.fooledit.control.*;
import cc.fooledit.example.filesystem.*;
import cc.fooledit.model.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.logging.*;
import javafx.scene.control.*;
import javax.activation.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FileCommands{
	private FileCommands(){

	}
	public static void open(){
		FileSystemData files=new FileSystemData();
		files.setInitialPath(guessDefaultPath());
		files.setAction((paths)->{
			paths.forEach((p)->{
				try{
					Main.show(DataObjectRegistry.readFrom(p.toUri().toURL()));
				}catch(Exception ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
				}
			});
			DataObjectRegistry.removeDataObject(files);
		});
		DataObjectRegistry.addDataObject(files,Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,MessageRegistry.getString("OPEN")));
		Main.INSTANCE.show(files);
	}
	public static void openUrl(){
		Main.INSTANCE.getMiniBuffer().setMode((url)->{
			try{
				Main.show(DataObjectRegistry.readFrom(new URL(url)));
			}catch(Exception ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		},null,"",new Label("URL:"),null);
	}
	public static void save(){
		DataObject data=Main.INSTANCE.getCurrentDataObject();
		String url=DataObjectRegistry.getURL(data);
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
		FileSystemData files=new FileSystemData();
		files.setInitialPath(guessDefaultPath());
		files.setAction((paths)->{
			paths.forEach((p)->saveAs(p));
			DataObjectRegistry.removeDataObject(files);
		});
		DataObjectRegistry.addDataObject(files,Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,MessageRegistry.getString("SAVE_AS")));
		Main.INSTANCE.show(files);
	}
	public static void saveAs(Path p){
		DataObject data=Main.INSTANCE.getCurrentDataObject();
		try{
			data.getDataObjectType().writeTo(data,p.toUri().toURL().openConnection());
			DataObjectRegistry.getProperties(data).put(DataObjectRegistry.URI,p.toUri().toString());
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private static Path guessDefaultPath(){
		try{
			return new File(new URI(DataObjectRegistry.getURL(Main.getCurrentDataObject()))).toPath();
		}catch(Exception ex){
			return null;
		}
	}
	private static MimeType guessContentType(Path file){
		try{
			return new MimeType(Files.probeContentType(file));
		}catch(IOException|MimeTypeParseException ex){
			Logger.getGlobal().log(Level.INFO,null,ex);
			try{
				return new MimeType("application/octet-stream");
			}catch(MimeTypeParseException ex1){
				Logger.getGlobal().log(Level.SEVERE,null,ex1);
				return null;
			}
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
		TemplateEditor templateEditor=new TemplateEditor();
		DataObjectRegistry.addDataObject(templateEditor,Helper.hashMap());
		Main.INSTANCE.show(templateEditor);
	}
}