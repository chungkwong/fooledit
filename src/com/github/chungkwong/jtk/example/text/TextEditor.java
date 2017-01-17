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
package com.github.chungkwong.jtk.example.text;
import com.github.chungkwong.jtk.model.*;
import java.io.*;
import java.util.logging.*;
import javafx.application.*;
import static javafx.application.Application.launch;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TextEditor extends Application implements DataEditor<TextObject>{
	public Node edit(TextObject obj){
		TextArea textArea=new TextArea();
		textArea.textProperty().bindBidirectional(obj.getText());
		return textArea;
	}
	@Override
	public void start(Stage primaryStage){
		TextObject data;
		try{
			FileInputStream in=new FileInputStream("/home/kwong/下载/train");
			data=new TextObjectType().readFrom(in);
			in.close();
		}catch(Exception ex){
			Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE,null,ex);
			throw new RuntimeException();
		}
		Node edit=new TextEditor().edit(data);
		Button save=new Button("Save");
		save.setOnAction((e)->{
			try{
				FileOutputStream out=new FileOutputStream("/home/kwong/下载/train");
				new TextObjectType().writeTo(data,out);
				out.close();
			}catch(Exception ex){
				Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE,null,ex);
			}
		});
		Scene scene=new Scene(new BorderPane(edit,save,null,null,null));
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
