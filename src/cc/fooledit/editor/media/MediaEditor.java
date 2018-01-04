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
package cc.fooledit.editor.media;
import cc.fooledit.core.Registry;
import cc.fooledit.core.MenuRegistry;
import cc.fooledit.core.MessageRegistry;
import cc.fooledit.core.Command;
import cc.fooledit.core.DataEditor;
import cc.fooledit.editor.text.PlainTextEditor;
import cc.fooledit.*;
import cc.fooledit.spi.*;
import java.io.*;
import java.util.function.*;
import java.util.logging.*;
import static javafx.application.Application.launch;
import javafx.application.*;
import javafx.collections.*;
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
	public static final MediaEditor INSTANCE=new MediaEditor();
	private final MenuRegistry menuRegistry=new MenuRegistry(MediaEditorModule.NAME);
	private final RegistryNode<String,Command,String> commandRegistry=Registry.ROOT.registerCommand(MediaEditorModule.NAME);
	private final NavigableRegistryNode<String,String,String> keymapRegistry=Registry.ROOT.registerKeymap(MediaEditorModule.NAME);
	private MediaEditor(){
		addCommand("play",(player)->player.play());
		addCommand("pause",(player)->player.pause());
		menuRegistry.registerDynamicMenu("editor.media.Markers",(items)->{
			MediaPlayer player=((MediaObject)Main.INSTANCE.getCurrentData()).getProperty().getValue();
			ObservableMap<String,Duration> entries=player.getMedia().getMarkers();
			items.clear();
			entries.forEach((mark,time)->{
				MenuItem item=new MenuItem(mark);
				item.setOnAction((e)->{
					player.seek(time);
				});
				items.add(item);
			});
		});
	}
	@Override
	public Node edit(MediaObject data,Object remark,RegistryNode<String,Object,String> meta){
		MediaView editor=new MediaView();
		MediaPlayer player=data.getProperty().getValue();
		player.statusProperty().addListener((e,o,n)->Main.INSTANCE.getNotifier().notify(MessageRegistry.getString(n.toString(),MediaEditorModule.NAME)));
		player.setOnMarker((e)->Main.INSTANCE.getNotifier().notify(e.getMarker().getKey()));
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
		Node edit=new MediaEditor().edit(data,null,null);
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
		return MessageRegistry.getString("MEDIA_PLAYER",MediaEditorModule.NAME);
	}
	private void addCommand(String name,Consumer<MediaPlayer> action){
		commandRegistry.addChild(name,new Command(name,()->action.accept(((MediaObject)Main.INSTANCE.getCurrentData()).getProperty().getValue()),MediaEditorModule.NAME));
	}
	@Override
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
	@Override
	public RegistryNode<String,Command,String> getCommandRegistry(){
		return commandRegistry;
	}
	@Override
	public NavigableRegistryNode<String,String,String> getKeymapRegistry(){
		return keymapRegistry;
	}
}
class MediaControl extends HBox{
	private MediaPlayer mp;
	private MediaView mediaView;
	private Duration duration;
	private Slider timeSlider;
	private Label playTime;
	private Slider volumeSlider;
	private Spinner<Double> rateSpinner;
	public MediaControl(MediaPlayer mp){
		this.mp=mp;
		Button playButton=new Button(">");
		playButton.setOnAction((e)->{
			MediaPlayer.Status status=mp.getStatus();
			if(status==MediaPlayer.Status.UNKNOWN||status==MediaPlayer.Status.HALTED){
				return;
			}
			if(status==MediaPlayer.Status.PAUSED||status==MediaPlayer.Status.READY||status==MediaPlayer.Status.STOPPED){
				mp.play();
			}else{
				mp.pause();
			}
		});
		mp.currentTimeProperty().addListener((ov)->updateValues());
		mp.setOnPlaying(()->playButton.setText("||"));
		mp.setOnPaused(()->playButton.setText(">"));
		mp.setOnReady(()->{
			duration=mp.getMedia().getDuration();
			updateValues();
		});
		mp.setOnEndOfMedia(()->mp.seek(mp.getStartTime()));
		getChildren().add(playButton);
		timeSlider=new Slider();
		HBox.setHgrow(timeSlider,Priority.ALWAYS);
		timeSlider.setMaxWidth(Double.MAX_VALUE);
		timeSlider.valueProperty().addListener((ov)->{
			if(timeSlider.isValueChanging()){
				mp.seek(duration.multiply(timeSlider.getValue()/100.0));
			}
		});
		getChildren().add(timeSlider);
		playTime=new Label();
		getChildren().add(playTime);
		ToggleButton loop=new ToggleButton("↺");
		loop.setSelected(mp.getCycleCount()!=1);
		loop.setOnAction((e)->{
			mp.setCycleCount(loop.isSelected()?MediaPlayer.INDEFINITE:1);
		});
		getChildren().add(loop);
		rateSpinner=new Spinner<>(0.0,8.0,1.0,0.5);
		mp.rateProperty().bind(rateSpinner.valueProperty());
		getChildren().add(rateSpinner);
		Label volumeLabel=new Label("X Vol: ");
		getChildren().add(volumeLabel);
		volumeSlider=new Slider(0,2.0,1.0);
		volumeSlider.valueProperty().addListener((ov)->mp.setVolume(volumeSlider.getValue()));
		getChildren().add(volumeSlider);
	}
	protected void updateValues(){
		if(playTime!=null&&timeSlider!=null&&volumeSlider!=null){
			Platform.runLater(()->{
				Duration currentTime=mp.getCurrentTime();
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
