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
import javax.sound.sampled.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Recorder{
	private final File wavFile;
	private final AudioFileFormat.Type fileType=AudioFileFormat.Type.WAVE;
	private TargetDataLine line;
	public Recorder(File wavFile){
		this.wavFile=wavFile;
	}
	public void start(){
		try{
			AudioFormat format=getAudioFormat();
			DataLine.Info info=new DataLine.Info(TargetDataLine.class,format);
			line=(TargetDataLine)AudioSystem.getLine(info);
			line.open(format);
			line.start();
			AudioInputStream ais=new AudioInputStream(line);
			AudioSystem.write(ais,fileType,wavFile);
		}catch(LineUnavailableException|IOException ex){
			ex.printStackTrace();
		}
	}
	private AudioFormat getAudioFormat(){
		float sampleRate=16000;
		int sampleSizeInBits=8;
		int channels=2;
		boolean signed=true;
		boolean bigEndian=true;
		return new AudioFormat(sampleRate,sampleSizeInBits,channels,signed,bigEndian);
	}
	public void finish(){
		line.stop();
		line.close();
	}
	public static void main(String[] args){
		final Recorder recorder=new Recorder(new File("/home/kwong/下载/useless.wav"));
		Thread stopper=new Thread(()->{
				try{
					Thread.sleep(10000);
				}catch(InterruptedException ex){
					ex.printStackTrace();
				}
				recorder.finish();
		});
		stopper.start();
		recorder.start();
	}
}
