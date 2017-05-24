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
package com.github.chungkwong.jtk.command;
import com.github.chungkwong.jtk.*;
import com.github.chungkwong.jtk.api.*;
import com.github.chungkwong.jtk.model.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.logging.*;
import javafx.scene.control.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FileCommands{
	private final Main main;
	public FileCommands(Main main){
		this.main=main;
	}
	public void open(){
		FileChooser fileChooser=new FileChooser();
		File file=fileChooser.showOpenDialog(null);
		if(file==null)
			return;
		open(file);
	}
	public void open(File file){
		String mime;
		try{
			mime=Files.probeContentType(file.toPath());
		}catch(IOException ex){
			mime=geussContentType(file);
		}
		open(file,mime);
	}
	public void open(File file,String mime){
		for(DataObjectType type:DataObjectTypeRegistry.getPreferedDataObjectType(mime)){
			if(tryOpen(file,type,mime))
				return;
		}
	}
	public void save(){
		DataObject data=(DataObject)main.getCurrentWorkSheet().getChildren().get(0).getUserData();
		try{
			File file=new File(new URI(DataObjectRegistry.getURL(data)));
			data.getDataObjectType().writeTo(data,new FileOutputStream(file));
		}catch(Exception ex){
			Logger.getLogger(FileCommands.class.getName()).log(Level.SEVERE,null,ex);
		}
	}
	private boolean tryOpen(File f,DataObjectType type,String mime){
		try(FileInputStream in=new FileInputStream(f)){
			main.addAndShow(type.readFrom(in),DataObjectRegistry.createProperties(f.getName(),f.toURI().toString(),mime,type.getClass().getName()));
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return false;
		}
		return true;
	}
	private String geussContentType(File file){
		return "";
	}
	public void create(){
		ListView<DataObjectType> types=new ListView<>();
		types.getItems().setAll(DataObjectTypeRegistry.getDataObjectTypes());
		Dialog dia=new Dialog();
		dia.getDialogPane().setContent(types);
		dia.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dia.showAndWait();
		DataObjectType type=types.getSelectionModel().getSelectedItem();
		main.addAndShow(type.create(),Helper.hashMap(DataObjectRegistry.TYPE,type));
	}
}