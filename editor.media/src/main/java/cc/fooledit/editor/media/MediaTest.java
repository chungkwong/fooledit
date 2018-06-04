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
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;
/**
 *
 * @author kwong
 */
public class MediaTest extends Application{
	private final VlcMediaViewer viewer;
	public MediaTest(){
		this.viewer=new VlcMediaViewer("/home/kwong/视频/殘梦.mp4");
	}
	@Override
	public void start(Stage stage) throws Exception{
		//FxMediaViewer viewer=new FxMediaViewer(new MediaPlayer(new Media(new File("/usr/share/orage/sounds/Boiling.wav").toURI().toString())));
		//VlcMediaViewer viewer=new VlcMediaViewer("v4l2:///dev/video0");
		//VlcMediaViewer viewer=new VlcMediaViewer("/home/kwong/音乐/变色龙-关正杰.mp3");
		ControlToolBox.Controls controls=new ControlToolBox.Controls(viewer);
		stage.setScene(new Scene(new BorderPane(viewer,null,null,controls,null)));
		stage.setMaximized(true);
		stage.show();
	}
	@Override
	public void stop() throws Exception{
		viewer.dispose();
		System.err.println("Stopped");
	}
	public static void main(String[] args){
		launch(args);
	}
}
