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
package com.github.chungkwong.jtk.example.audio;
import com.github.chungkwong.jtk.example.text.*;
import com.github.chungkwong.jtk.model.*;
import java.io.*;
import java.util.logging.*;
import javafx.application.*;
import static javafx.application.Application.launch;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class AudioEditor extends Application implements DataEditor<AudioObject>{
	@Override
	public Node edit(AudioObject data){
		MediaView editor=new MediaView();
		data.getProperty().getValue().setAutoPlay(true);
		editor.setMediaPlayer(data.getProperty().getValue());
		data.getProperty().getValue().play();
		//editor.mediaPlayerProperty().bindBidirectional(data.getProperty());
		return editor;
	}

	@Override
	public void start(Stage primaryStage){
		AudioObject data;
		try{
			FileInputStream in=new FileInputStream("/home/kwong/sysu_learning2/研究生英语/视频（role play）/Avengers/Avengers.mp4");
			data=AudioObjectType.INSTANCE.readFrom(in);
			in.close();
		}catch(Exception ex){
			Logger.getLogger(TextEditor.class.getName()).log(Level.SEVERE,null,ex);
			throw new RuntimeException();
		}
		Node edit=new AudioEditor().edit(data);
		Scene scene=new Scene(new BorderPane(edit));
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