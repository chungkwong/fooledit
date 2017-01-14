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

import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Main extends Application{
	@Override
	public void start(Stage primaryStage){
		Button btn=new Button();
		btn.setText("Say 'Hello World'");
		btn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				primaryStage.setFullScreen(!primaryStage.isFullScreen());
			}
		});
		MenuBar bar=new MenuBar(new Menu("HELP"));
		TextField input=new TextField();
		HBox commander=new HBox(bar,input);
		HBox.setHgrow(bar,Priority.NEVER);
		HBox.setHgrow(input,Priority.ALWAYS);
		BorderPane root=new BorderPane(btn);
		root.setTop(commander);
		Scene scene=new Scene(root);
		//setUserAgentStylesheet(Main.class.getResource("/com/github/chungkwong/jtk/kwong.css").toString());
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
