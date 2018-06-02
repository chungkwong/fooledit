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
import javafx.beans.property.*;
import javafx.scene.media.*;
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class FxMediaViewer extends MediaView implements MediaViewer{
	private final MediaPlayer player;
	public FxMediaViewer(MediaPlayer player){
		super(player);
		this.player=player;
	}
	@Override
	public void play(){
		player.play();
	}
	@Override
	public void pause(){
		player.pause();
	}
	@Override
	public void seek(Duration duration){
		player.seek(duration);
	}
	@Override
	public DoubleProperty rateProperty(){
		return player.rateProperty();
	}
	@Override
	public DoubleProperty volumeProperty(){
		return player.volumeProperty();
	}
	@Override
	public ReadOnlyObjectProperty<Duration> currentTimeProperty(){
		return player.currentTimeProperty();
	}
	@Override
	public ReadOnlyObjectProperty<Duration> totalTimeProperty(){
		return player.totalDurationProperty();
	}
	@Override
	public ReadOnlyObjectProperty<MediaPlayer.Status> statusProperty(){
		return player.statusProperty();
	}
}
