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
		files.setInitialPath(geussDefaultPath());
		files.setAction((paths)->{
			paths.forEach((p)->open(p));
			DataObjectRegistry.removeDataObject(files);
		});
		Main.INSTANCE.addAndShow(files,Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,MessageRegistry.getString("OPEN")));
	}
	public static void open(Path file){
		open(file,geussContentType(file));
	}
	public static void open(Path file,MimeType mime){
		for(DataObjectType type:DataObjectTypeRegistry.getPreferedDataObjectType(mime)){
			if(tryOpen(file,type,mime))
				return;
		}
	}
	public static void save(){
		DataObject data=Main.INSTANCE.getCurrentDataObject();
		String url=DataObjectRegistry.getURL(data);
		if(url==null)
			saveAs();
		else
			try{
				File file=new File(new URI(url));
				data.getDataObjectType().writeTo(data,new FileOutputStream(file));
			}catch(Exception ex){
				Logger.getLogger(FileCommands.class.getName()).log(Level.SEVERE,null,ex);
			}
	}
	public static void saveAs(){
		FileSystemData files=new FileSystemData();
		files.setInitialPath(geussDefaultPath());
		files.setAction((paths)->{
			paths.forEach((p)->saveAs(p));
			DataObjectRegistry.removeDataObject(files);
		});
		Main.INSTANCE.addAndShow(files,Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,MessageRegistry.getString("SAVE_AS")));
	}
	public static void saveAs(Path p){
		DataObject data=Main.INSTANCE.getCurrentDataObject();
		try(OutputStream out=Files.newOutputStream(p)){
			data.getDataObjectType().writeTo(data,out);
			DataObjectRegistry.getProperties(data).put(DataObjectRegistry.URI,p.toUri().toString());
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	private static boolean tryOpen(Path f,DataObjectType type,MimeType mime){
		try(InputStream in=Files.newInputStream(f)){
			Main.INSTANCE.addAndShow(type.readFrom(in),DataObjectRegistry.createProperties(f.getFileName().toString(),f.toUri().toString(),mime.toString(),type.getClass().getName()));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return false;
		}
		return true;
	}
	private static Path geussDefaultPath(){
		try{
			return new File(new URI(DataObjectRegistry.getURL(Main.getCurrentDataObject()))).toPath();
		}catch(Exception ex){
			return null;
		}
	}
	private static MimeType geussContentType(Path file){
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
	public static void create(){
		Main.INSTANCE.addAndShow(TemplateEditor.INSTANCE,Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,MessageRegistry.getString("TEMPLATE")));
	}
}