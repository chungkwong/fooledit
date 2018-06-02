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
import javafx.event.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.util.*;
import uk.co.caprica.vlcj.component.*;
import uk.co.caprica.vlcj.player.direct.*;
import uk.co.caprica.vlcj.player.direct.format.*;
/**
 *
 * @author kwong
 */
public class VlcMediaViewer extends Canvas{
	private static final double FPS=30.0;
	private final Timeline timeline;
	private PixelWriter pixelWriter;
	private final WritablePixelFormat<ByteBuffer> pixelFormat;
	private final DirectMediaPlayerComponent mediaPlayerComponent;
	private final EventHandler<ActionEvent> nextFrameHandler=(t)->renderFrame();
	public VlcMediaViewer(String url){
		pixelWriter=getGraphicsContext2D().getPixelWriter();
		pixelFormat=PixelFormat.getByteBgraInstance();
		mediaPlayerComponent=new VlcMediaPlayerComponent();
		mediaPlayerComponent.getMediaPlayer().playMedia(url);
		timeline=new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		double duration=1000.0/FPS;
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration),nextFrameHandler));
		startTimer();
		mediaPlayerComponent.getMediaPlayer();
	}
	public final void stop() throws Exception{
		stopTimer();
		mediaPlayerComponent.getMediaPlayer().stop();
		mediaPlayerComponent.getMediaPlayer().release();
	}
	private class VlcMediaPlayerComponent extends DirectMediaPlayerComponent{
		public VlcMediaPlayerComponent(){
			super(new VlcBufferFormatCallback());
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
	private void startTimer(){
		timeline.playFromStart();
	}
	private void stopTimer(){
		timeline.stop();
	}
}
