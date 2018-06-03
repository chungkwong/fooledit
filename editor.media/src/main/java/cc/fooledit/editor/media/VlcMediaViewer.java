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
import com.sun.jna.*;
import java.nio.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.media.*;
import javafx.util.*;
import uk.co.caprica.vlcj.component.*;
import uk.co.caprica.vlcj.player.direct.*;
import uk.co.caprica.vlcj.player.direct.format.*;
/**
 *
 * @author kwong
 */
public class VlcMediaViewer extends Canvas implements MediaViewer{
	private static final double FPS=30.0;
	private final Timeline timeline;
	private PixelWriter pixelWriter;
	private final WritablePixelFormat<ByteBuffer> pixelFormat;
	private final DirectMediaPlayerComponent mediaPlayerComponent;
	private final EventHandler<ActionEvent> nextFrameHandler=(t)->renderFrame();
	private final DoubleProperty rate;
	private final DoubleProperty volume;
	private final ObjectProperty<Duration> currentTime;
	private final ObjectProperty<Duration> totalTime;
	private final ObjectProperty<MediaPlayer.Status> status;
	public VlcMediaViewer(String url){
		pixelWriter=getGraphicsContext2D().getPixelWriter();
		pixelFormat=PixelFormat.getByteBgraInstance();
		mediaPlayerComponent=new VlcMediaPlayerComponent();
		mediaPlayerComponent.getMediaPlayer().playMedia(url);
		rate=new SimpleDoubleProperty(mediaPlayerComponent.getMediaPlayer().getRate());
		rate.addListener((e,o,n)->mediaPlayerComponent.getMediaPlayer().setRate(n.floatValue()));
		volume=new SimpleDoubleProperty(1.0);
		volume.addListener((e,o,n)->mediaPlayerComponent.getMediaPlayer().setVolume((int)(n.doubleValue()*100)));
		currentTime=new SimpleObjectProperty<>(Duration.millis(mediaPlayerComponent.getMediaPlayer().getTime()));
		totalTime=new SimpleObjectProperty<>(Duration.millis(mediaPlayerComponent.getMediaPlayer().getLength()));
		status=new SimpleObjectProperty<>(MediaPlayer.Status.READY);
		timeline=new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		double duration=1000.0/FPS;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration),nextFrameHandler));
		timeline.playFromStart();
	}
	public final void stop() throws Exception{
		timeline.stop();
		mediaPlayerComponent.getMediaPlayer().stop();
		mediaPlayerComponent.getMediaPlayer().release();
	}
	@Override
	public void play(){
		mediaPlayerComponent.getMediaPlayer().play();
	}
	@Override
	public void pause(){
		mediaPlayerComponent.getMediaPlayer().pause();
	}
	@Override
	public void seek(Duration duration){
		mediaPlayerComponent.getMediaPlayer().setTime((long)duration.toMillis());
	}
	@Override
	public DoubleProperty rateProperty(){
		return rate;
	}
	@Override
	public DoubleProperty volumeProperty(){
		return volume;
	}
	@Override
	public ReadOnlyObjectProperty<Duration> currentTimeProperty(){
		return currentTime;
	}
	@Override
	public ReadOnlyObjectProperty<Duration> totalTimeProperty(){
		return totalTime;
	}
	@Override
	public ReadOnlyObjectProperty<MediaPlayer.Status> statusProperty(){
		return status;
	}
	private class VlcMediaPlayerComponent extends DirectMediaPlayerComponent{
		public VlcMediaPlayerComponent(){
			super(new VlcBufferFormatCallback());
		}
		@Override
		public void lengthChanged(uk.co.caprica.vlcj.player.MediaPlayer mediaPlayer,long newLength){
			totalTime.set(Duration.millis(newLength));
		}
		@Override
		public void timeChanged(uk.co.caprica.vlcj.player.MediaPlayer mediaPlayer,long newTime){
			currentTime.set(Duration.millis(newTime));
		}
		@Override
		public void paused(uk.co.caprica.vlcj.player.MediaPlayer mediaPlayer){
			timeline.pause();
			status.setValue(MediaPlayer.Status.PAUSED);
		}
		@Override
		public void playing(uk.co.caprica.vlcj.player.MediaPlayer mediaPlayer){
			timeline.play();
			status.setValue(MediaPlayer.Status.PLAYING);
		}
		@Override
		public void finished(uk.co.caprica.vlcj.player.MediaPlayer mediaPlayer){
			status.setValue(MediaPlayer.Status.STOPPED);
			timeline.pause();
		}
		@Override
		public void stopped(uk.co.caprica.vlcj.player.MediaPlayer mediaPlayer){
			status.setValue(MediaPlayer.Status.STOPPED);
			timeline.pause();
		}
		@Override
		public void opening(uk.co.caprica.vlcj.player.MediaPlayer mediaPlayer){
			status.setValue(MediaPlayer.Status.READY);
		}
		@Override
		public void newMedia(uk.co.caprica.vlcj.player.MediaPlayer mediaPlayer){
			status.setValue(MediaPlayer.Status.READY);
		}
		@Override
		public void error(uk.co.caprica.vlcj.player.MediaPlayer mediaPlayer){
			status.setValue(MediaPlayer.Status.HALTED);
		}
		@Override
		public void mediaFreed(uk.co.caprica.vlcj.player.MediaPlayer mediaPlayer){
			status.setValue(MediaPlayer.Status.DISPOSED);
		}
	}
	private class VlcBufferFormatCallback implements BufferFormatCallback{
		@Override
		public BufferFormat getBufferFormat(int sourceWidth,int sourceHeight){
			Platform.runLater(()->{
				setWidth(sourceWidth);
				setHeight(sourceHeight);
				pixelWriter=getGraphicsContext2D().getPixelWriter();
			});
			return new RV32BufferFormat(sourceWidth,sourceHeight);
		}
	}
	protected final void renderFrame(){
		Memory[] nativeBuffers=mediaPlayerComponent.getMediaPlayer().lock();
		if(nativeBuffers!=null){
			Memory nativeBuffer=nativeBuffers[0];
			if(nativeBuffer!=null){
				ByteBuffer byteBuffer=nativeBuffer.getByteBuffer(0,nativeBuffer.size());
				BufferFormat bufferFormat=((DefaultDirectMediaPlayer)mediaPlayerComponent.getMediaPlayer()).getBufferFormat();
				if(bufferFormat.getWidth()>0&&bufferFormat.getHeight()>0){
					pixelWriter.setPixels(0,0,bufferFormat.getWidth(),bufferFormat.getHeight(),pixelFormat,byteBuffer,bufferFormat.getPitches()[0]);
				}
			}
		}
		mediaPlayerComponent.getMediaPlayer().unlock();
	}
}
