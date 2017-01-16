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
import com.github.chungkwong.jtk.control.*;
import java.util.logging.*;
import javafx.application.*;
import javafx.beans.value.*;
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
	private final Notifier notifier=new Notifier();
	private final BorderPane root;
	private Node currentNode;
	private Stage stage;
	public Main(){
		Logger.getGlobal().setLevel(Level.INFO);
		Logger.getGlobal().addHandler(notifier);
		MenuBar bar=new MenuBar();
		TextField input=new TextField();
		HBox commander=new HBox(bar,input);
		HBox.setHgrow(bar,Priority.NEVER);
		HBox.setHgrow(input,Priority.ALWAYS);
		root=new BorderPane(wrap(new WorkSheet(new TextArea())));
		root.setTop(commander);
		commandRegistry.addCommand("full_screen",()->stage.setFullScreen(true));
		commandRegistry.addCommand("maximize_frame",()->stage.setMaximized(true));
		commandRegistry.addCommand("iconify_frame",()->stage.setIconified(true));
		commandRegistry.addCommand("always_on_top_frame",()->stage.setAlwaysOnTop(true));
		commandRegistry.addCommand("split_vertically",()->currentWorkSheet().splitVertically(wrap(new TextArea())));
		commandRegistry.addCommand("split_horizontally",()->currentWorkSheet().splitHorizontally(wrap(new TextArea())));
		commandRegistry.addCommand("keep_only",()->((WorkSheet)root.getCenter()).keepOnly(currentNode));
		menuRegistry=new MenuRegistry(bar.getMenus(),commandRegistry);
	}
	private Node wrap(Node node){
		node.focusedProperty().addListener(new ChangeListener<Boolean>(){
			@Override
			public void changed(ObservableValue<? extends Boolean> ov,Boolean t,Boolean t1){
				if(t1)
					currentNode=node;
			}
		});
		return node;
	}
	private WorkSheet currentWorkSheet(){
		return currentNode==null?((WorkSheet)root.getCenter()):(WorkSheet)currentNode.getParent();
	}
	public CommandRegistry getCommandRegistry(){
		return commandRegistry;
	}
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
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
