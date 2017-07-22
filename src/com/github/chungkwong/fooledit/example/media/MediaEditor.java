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
package com.github.chungkwong.fooledit.example.media;
import com.github.chungkwong.fooledit.api.*;
import com.github.chungkwong.fooledit.example.text.*;
import com.github.chungkwong.fooledit.model.*;
import java.io.*;
import java.util.logging.*;
import static javafx.application.Application.launch;
import javafx.application.*;
import javafx.beans.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.*;
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MediaEditor extends Application implements DataEditor<MediaObject>{
	@Override
	public Node edit(MediaObject data){
		MediaView editor=new MediaView();
		MediaPlayer player=data.getProperty().getValue();
		editor.setMediaPlayer(player);
		player.play();
		return new BorderPane(new ScrollPane(editor),new Label(player.getMedia().getMetadata().toString()),null,new MediaControl(player),null);
	}
	@Override
	public void start(Stage primaryStage){
		MediaObject data;
		try{
			//FileInputStream in=new FileInputStream("/home/kwong/音乐/半斤八两-许冠杰.mp3");
			//data=AudioObjectType.INSTANCE.readFrom(in);
			//data=new AudioObject(new MediaPlayer(new Media(new File("/home/kwong/视频/860b4ce2a01af63795899c89bc9a8c7b.mp4").toURI().toString())));
			//data=new AudioObject(new MediaPlayer(new Media(new File("/home/kwong/音乐/半斤八两-许冠杰.mp3").toURI().toString())));
			data=new MediaObject(new MediaPlayer(new Media(new File("/home/kwong/视频/860b4ce2a01af63795899c89bc9a8c7b.mp4").toURI().toString())));
			//in.close();
		}catch(Exception ex){
			Logger.getLogger(PlainTextEditor.class.getName()).log(Level.SEVERE,null,ex);
			throw new RuntimeException();
		}
		Node edit=new MediaEditor().edit(data);
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
	@Override
	public String getName(){
		return MessageRegistry.getString("MEDIA_PLAYER");
	}
}
class MediaControl extends HBox{
	private MediaPlayer mp;
	private MediaView mediaView;
	private boolean repeat=false;
	private boolean stopRequested=false;
	private boolean atEndOfMedia=false;
	private Duration duration;
	private Slider timeSlider;
	private Label playTime;
	private Slider volumeSlider;
	public MediaControl(MediaPlayer mp){
		this.mp=mp;
		Button playButton=new Button(">");
		playButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				MediaPlayer.Status status=mp.getStatus();
				if(status==MediaPlayer.Status.UNKNOWN||status==MediaPlayer.Status.HALTED){
					return;
				}
				if(status==MediaPlayer.Status.PAUSED||status==MediaPlayer.Status.READY||status==MediaPlayer.Status.STOPPED){
					if(atEndOfMedia){
						mp.seek(mp.getStartTime());
						atEndOfMedia=false;
					}
					mp.play();
				}else{
					mp.pause();
				}
			}
		});
		mp.currentTimeProperty().addListener(new InvalidationListener(){
			public void invalidated(Observable ov){
				updateValues();
			}
		});
		mp.setOnPlaying(new Runnable(){
			public void run(){
				if(stopRequested){
					mp.pause();
					stopRequested=false;
				}else{
					playButton.setText("||");
				}
			}
		});
		mp.setOnPaused(new Runnable(){
			public void run(){
				playButton.setText(">");
			}
		});
		mp.setOnReady(new Runnable(){
			public void run(){
				duration=mp.getMedia().getDuration();
				updateValues();
			}
		});
		mp.setOnEndOfMedia(new Runnable(){
			public void run(){
				if(!repeat){
					playButton.setText(">");
					stopRequested=true;
					atEndOfMedia=true;
				}
			}
		});
		getChildren().add(playButton);
		timeSlider=new Slider();
		HBox.setHgrow(timeSlider,Priority.ALWAYS);
		timeSlider.setMaxWidth(Double.MAX_VALUE);
		timeSlider.valueProperty().addListener(new InvalidationListener(){
			public void invalidated(Observable ov){
				if(timeSlider.isValueChanging()){
					mp.seek(duration.multiply(timeSlider.getValue()/100.0));
				}
			}
		});
		getChildren().add(timeSlider);
		playTime=new Label();
		getChildren().add(playTime);
		ToggleButton loop=new ToggleButton("↺");
		loop.setSelected(mp.getCycleCount()!=1);
		loop.setOnAction((e)->{
			repeat=loop.isSelected();
			mp.setCycleCount(repeat?MediaPlayer.INDEFINITE:1);
		});
		getChildren().add(loop);
		Label volumeLabel=new Label("Vol: ");
		getChildren().add(volumeLabel);
		volumeSlider=new Slider(0,2.0,1.0);
		volumeSlider.valueProperty().addListener(new InvalidationListener(){
			public void invalidated(Observable ov){
				mp.setVolume(volumeSlider.getValue());
			}
		});
		getChildren().add(volumeSlider);
	}
	protected void updateValues(){
		if(playTime!=null&&timeSlider!=null&&volumeSlider!=null){
			Platform.runLater(new Runnable(){
				public void run(){
					Duration currentTime=mp.getCurrentTime();
					playTime.setText(formatTime(currentTime,duration));
					timeSlider.setDisable(duration.isUnknown());
					if(!timeSlider.isDisabled()&&duration.greaterThan(Duration.ZERO)&&!timeSlider.isValueChanging()){
						timeSlider.setValue(currentTime.divide(duration).toMillis()*100.0);
					}
				}
			});
		}
	}
	private static String formatTime(Duration elapsed,Duration duration){
		int intElapsed=(int)Math.floor(elapsed.toSeconds());
		int elapsedHours=intElapsed/(60*60);
		if(elapsedHours>0){
			intElapsed-=elapsedHours*60*60;
		}
		int elapsedMinutes=intElapsed/60;
		int elapsedSeconds=intElapsed-elapsedHours*60*60-elapsedMinutes*60;
		if(duration.isIndefinite()||duration.isUnknown()){
			if(elapsedHours>0){
				return String.format("%d:%02d:%02d",elapsedHours,elapsedMinutes,elapsedSeconds);
			}else{
				return String.format("%02d:%02d",elapsedMinutes,elapsedSeconds);
			}
		}else{
			int intDuration=(int)Math.floor(duration.toSeconds());
			int durationHours=intDuration/(60*60);
			if(durationHours>0){
				intDuration-=durationHours*60*60;
			}
			int durationMinutes=intDuration/60;
			int durationSeconds=intDuration-durationHours*60*60-durationMinutes*60;
			if(durationHours>0){
				return String.format("%d:%02d:%02d/%d:%02d:%02d",elapsedHours,elapsedMinutes,elapsedSeconds,
						durationHours,durationMinutes,durationSeconds);
			}else{
				return String.format("%02d:%02d/%02d:%02d",elapsedMinutes,elapsedSeconds,durationMinutes,durationSeconds);
			}
		}
	}
}
