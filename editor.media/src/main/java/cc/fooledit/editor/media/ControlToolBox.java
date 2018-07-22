/*
 * Copyright (C) 2018 Chan Chung Kwong
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
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class ControlToolBox implements ToolBox{
	@Override
	public String getName(){
		return "CONTROLS";
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("CONTROLS",Activator.NAME);
	}
	@Override
	public Node createInstance(Node viewer,Object remark,RegistryNode<String,Object> meta){
		return new Controls((MediaViewer)viewer);
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public Side[] getPerferedSides(){
		return new Side[]{Side.BOTTOM};
	}
	static class Controls extends HBox{
		private final MediaViewer viewer;
		private Duration duration;
		private Slider timeSlider;
		private Label playTime;
		private Slider volumeSlider;
		private Spinner<Double> rateSpinner;
		public Controls(MediaViewer viewer){
			this.viewer=viewer;
			Button playButton=new Button(viewer.statusProperty().getValue()==MediaPlayer.Status.PLAYING?"||":">");
			playButton.setOnAction((e)->{
				MediaPlayer.Status status=viewer.statusProperty().get();
				if(status==MediaPlayer.Status.UNKNOWN||status==MediaPlayer.Status.HALTED){
					return;
				}
				if(status==MediaPlayer.Status.PAUSED||status==MediaPlayer.Status.READY||status==MediaPlayer.Status.STOPPED){
					viewer.play();
				}else{
					viewer.pause();
				}
			});
			viewer.currentTimeProperty().addListener((ov)->updateValues());
			duration=viewer.totalTimeProperty().get();
			viewer.statusProperty().addListener((e,o,n)->{
				if(n==MediaPlayer.Status.PLAYING)
					Platform.runLater(()->playButton.setText("||"));
				else if(n==MediaPlayer.Status.PAUSED||n==MediaPlayer.Status.STOPPED)
					Platform.runLater(()->playButton.setText(">"));
				else if(n==MediaPlayer.Status.READY){
					duration=viewer.totalTimeProperty().get();
					Platform.runLater(()->updateValues());
				}	
			});
			getChildren().add(playButton);
			timeSlider=new Slider();
			HBox.setHgrow(timeSlider,Priority.ALWAYS);
			timeSlider.setMaxWidth(Double.MAX_VALUE);
			timeSlider.valueProperty().addListener((ov)->{
				if(timeSlider.isValueChanging()){
					viewer.seek(duration.multiply(timeSlider.getValue()/100.0));
				}
			});
			getChildren().add(timeSlider);
			playTime=new Label();
			getChildren().add(playTime);
			Label rateLabel=new Label(MessageRegistry.getString("RATE",Activator.NAME));
			getChildren().add(rateLabel);
			rateSpinner=new Spinner<>(0.0,8.0,1.0,0.5);
			viewer.rateProperty().bind(rateSpinner.valueProperty());
			getChildren().add(rateSpinner);
			Label volumeLabel=new Label(MessageRegistry.getString("VOLUME",Activator.NAME));
			getChildren().add(volumeLabel);
			volumeSlider=new Slider(0,2.0,1.0);
			volumeSlider.valueProperty().bindBidirectional(viewer.volumeProperty());
			getChildren().add(volumeSlider);
		}
		protected void updateValues(){
			if(playTime!=null&&timeSlider!=null&&volumeSlider!=null){
				Platform.runLater(()->{
					Duration currentTime=viewer.currentTimeProperty().get();
					playTime.setText(formatTime(currentTime,duration));
					timeSlider.setDisable(duration.isUnknown());
					if(!timeSlider.isDisabled()&&duration.greaterThan(Duration.ZERO)&&!timeSlider.isValueChanging()){
						timeSlider.setValue(currentTime.divide(duration).toMillis()*100.0);
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
}