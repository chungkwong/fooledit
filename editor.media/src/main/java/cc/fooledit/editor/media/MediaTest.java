/*
 * Copyright (C) 2018 Chan Chung Kwong <1m02math@126.com>
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
package cc.fooledit.editor.media;
import java.io.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.*;
/**
 *
 * @author kwong
 */
public class MediaTest extends Application{
	@Override
	public void start(Stage stage) throws Exception{
		FxMediaViewer viewer=new FxMediaViewer(new MediaPlayer(new Media(new File("/home/kwong/音乐/自由在我手-群星.mp3").toURI().toString())));
		//VlcMediaViewer viewer=new VlcMediaViewer("/home/kwong/视频/殘梦.mp4");
		//VlcMediaViewer viewer=new VlcMediaViewer("/home/kwong/音乐/变色龙-关正杰.mp3");
		ControlToolBox.Controls controls=new ControlToolBox.Controls(viewer);
		stage.setScene(new Scene(new BorderPane(viewer,null,null,controls,null)));
		stage.setMaximized(true);
		stage.show();
	}
	public static void main(String[] args){
		launch(args);
	}
}
