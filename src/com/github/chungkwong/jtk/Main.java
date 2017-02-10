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
	private final TextField input=new TextField();
	private final Scene scene;
	private Stage stage;
	private Node currentNode;
	public Main(){
		Logger.getGlobal().setLevel(Level.INFO);
		Logger.getGlobal().addHandler(notifier);
		MenuBar bar=new MenuBar();
		HBox commander=new HBox(bar,input);
		HBox.setHgrow(bar,Priority.NEVER);
		HBox.setHgrow(input,Priority.ALWAYS);
		TextObject welcome=new TextObject("Welcome");
		dataObjectRegistry.addDataObject(welcome,Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,"Welcome"));
		root=new BorderPane(new WorkSheet(getDefaultEditor(welcome)));
		root.setTop(commander);
		scene=new Scene(root);
		//scene.setUserAgentStylesheet("com/github/chungkwong/jtk/dark.css");
		registerStandardCommand();
		menuRegistry=new MenuRegistry(bar.getMenus(),commandRegistry);
		keymapRegistry=new KeymapRegistry(scene,commandRegistry);
		scene.focusOwnerProperty().addListener((e,o,n)->updateCurrentNode(n));
		input.setOnAction((e)->commandRegistry.getCommand(input.getText()).execute());
		bar.getMenus().add(getBufferMenu());
	}
	private void registerStandardCommand(){
		FileCommands fileCommands=new FileCommands(this);
		commandRegistry.addCommand("open-file",()->fileCommands.open());
		commandRegistry.addCommand("save",()->fileCommands.save());
		commandRegistry.addCommand("full_screen",()->stage.setFullScreen(true));
		commandRegistry.addCommand("maximize_frame",()->stage.setMaximized(true));
		commandRegistry.addCommand("iconify_frame",()->stage.setIconified(true));
		commandRegistry.addCommand("always_on_top_frame",()->stage.setAlwaysOnTop(true));
		commandRegistry.addCommand("split_vertically",()->currentWorkSheet().splitVertically(getDefaultEditor(getCurrentDataObject())));
		commandRegistry.addCommand("split_horizontally",()->currentWorkSheet().splitHorizontally(getDefaultEditor(getCurrentDataObject())));
		commandRegistry.addCommand("keep_only",()->((WorkSheet)root.getCenter()).keepOnly(getCurrentNode()));
		commandRegistry.addCommand("browser",()->addAndShow(new BrowserData(),Helper.hashMap(DataObjectRegistry.DEFAULT_NAME,"Browser")));
		commandRegistry.addCommand("command",()->input.requestFocus());
	}
	private OnDemandMenu getBufferMenu(){
		return new OnDemandMenu(MessageRegistry.getString("BUFFERS"),(l)->{
			for(String name:dataObjectRegistry.getDataObjectNames()){
				MenuItem item=new MenuItem(name);
				item.setOnAction((e)->currentWorkSheet().setCenter(getDefaultEditor(dataObjectRegistry.getDataObject(name))));
				l.add(item);
			}
			l.add(new SeparatorMenuItem());
			DataObject curr=getCurrentDataObject();
			DataObjectTypeRegistry.getDataEditors(curr.getClass()).forEach((editor)->{
				MenuItem item=new MenuItem(editor.getName());
				item.setOnAction((e)->currentWorkSheet().setCenter(getEditor(curr,editor)));
				l.add(item);
			});
		});
	}
	private void updateCurrentNode(Node node){
		while(!(node instanceof WorkSheet)&&node!=null){
			node=node.getParent();
		}
		if(node!=null)
			currentNode=((WorkSheet)node).getCenter();
	}
	public void addAndShow(DataObject data,HashMap<Object,Object> prop){
		getDataObjectRegistry().addDataObject(data,prop);
		Node editor=getDefaultEditor(data);
		currentWorkSheet().keepOnly(editor);
	}
	public Node getDefaultEditor(DataObject data){
		return getEditor(data,DataObjectTypeRegistry.getDataEditors(data.getClass()).get(0));
	}
	private Node getEditor(DataObject data,DataEditor editor){
		Node node=editor.edit(data);
		node.setUserData(data);
		return node;
	}
	public DataObject getCurrentDataObject(){
		return (DataObject)currentNode.getUserData();
	}
	public Node getCurrentNode(){
		return currentNode;
	}
	public WorkSheet currentWorkSheet(){
		return (WorkSheet)currentNode.getParent();
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
