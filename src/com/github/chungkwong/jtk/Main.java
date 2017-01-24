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
package com.github.chungkwong.jtk;

import com.github.chungkwong.jtk.api.*;
import com.github.chungkwong.jtk.command.*;
import com.github.chungkwong.jtk.control.*;
import com.github.chungkwong.jtk.example.text.*;
import com.github.chungkwong.jtk.example.tool.*;
import com.github.chungkwong.jtk.model.*;
import java.util.*;
import java.util.logging.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Main extends Application{
	private final CommandRegistry commandRegistry=new CommandRegistry();
	private final MenuRegistry menuRegistry;
	private final KeymapRegistry keymapRegistry;
	private final Notifier notifier=new Notifier();
	private final DataObjectRegistry dataObjectRegistry=new DataObjectRegistry();
	private final BorderPane root;
	private Stage stage;
	public Main(){
		Logger.getGlobal().setLevel(Level.INFO);
		Logger.getGlobal().addHandler(notifier);
		MenuBar bar=new MenuBar();
		TextField input=new TextField();
		HBox commander=new HBox(bar,input);
		HBox.setHgrow(bar,Priority.NEVER);
		HBox.setHgrow(input,Priority.ALWAYS);
		root=new BorderPane(new WorkSheet(getEditor(new TextObject("Welcome"))));
		root.setTop(commander);
		FileCommands fileCommands=new FileCommands(this);
		commandRegistry.addCommand("open-file",()->fileCommands.open());
		commandRegistry.addCommand("save",()->fileCommands.save());
		commandRegistry.addCommand("full_screen",()->stage.setFullScreen(true));
		commandRegistry.addCommand("maximize_frame",()->stage.setMaximized(true));
		commandRegistry.addCommand("iconify_frame",()->stage.setIconified(true));
		commandRegistry.addCommand("always_on_top_frame",()->stage.setAlwaysOnTop(true));
		commandRegistry.addCommand("split_vertically",()->currentWorkSheet().splitVertically(getEditor(getCurrentDataObject())));
		commandRegistry.addCommand("split_horizontally",()->currentWorkSheet().splitHorizontally(getEditor(getCurrentDataObject())));
		commandRegistry.addCommand("keep_only",()->((WorkSheet)root.getCenter()).keepOnly(getCurrentNode()));
		commandRegistry.addCommand("browser",()->addAndShow(new BrowserData(),Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,"Browser")));
		menuRegistry=new MenuRegistry(bar.getMenus(),commandRegistry);
		keymapRegistry=new KeymapRegistry(root,commandRegistry);
		//bar.getMenus().get(0).getItems().add(item);
	}
	public void addAndShow(DataObject data,HashMap<Object,Object> prop){
		getDataObjectRegistry().addDataObject(data,prop);
		Node editor=getEditor(data);
		currentWorkSheet().keepOnly(editor);
		editor.requestFocus();
	}
	public Node getEditor(DataObject data){
		Node editor=DataObjectTypeRegistry.getDataEditors(data.getClass()).get(0).edit(data);
		editor.setUserData(data);
		return editor;
	}
	public DataObject getCurrentDataObject(){
		return (DataObject)getCurrentNode().getUserData();
	}
	public Node getCurrentNode(){
		return currentWorkSheet().getCenter();
	}
	public WorkSheet currentWorkSheet(){
		Node focusOwner=root.getScene().getFocusOwner();
		while(!(focusOwner instanceof WorkSheet)){
			focusOwner=focusOwner.getParent();
		}
		return (WorkSheet)focusOwner;
	}
	public CommandRegistry getCommandRegistry(){
		return commandRegistry;
	}
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
	public DataObjectRegistry getDataObjectRegistry(){
		return dataObjectRegistry;
	}
	public Notifier getNotifier(){
		return notifier;
	}
	@Override
	public void start(Stage primaryStage){
		stage=primaryStage;
		Scene scene=new Scene(root);
		primaryStage.setTitle("IDEM");
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.show();
	}
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args){
		launch(args);
	}
}
