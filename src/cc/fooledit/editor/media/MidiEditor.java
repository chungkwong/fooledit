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
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.util.logging.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javax.sound.midi.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MidiEditor implements DataEditor<MidiObject>{
	public static final MidiEditor INSTANCE=new MidiEditor();
	private MidiEditor(){
	}
	@Override
	public Node edit(MidiObject data,Object remark,RegistryNode<String,Object,String> meta){
		try{
			return new MidiViewer(data.getSequence());
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return new Label(ex.getLocalizedMessage());
		}
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("MIDI_EDITOR",MediaEditorModule.NAME);
	}
}
class MidiViewer extends HBox{
	private final Sequence sequence;
	private final Sequencer sequencer;
	public MidiViewer(Sequence sequence)throws MidiUnavailableException, InvalidMidiDataException{
		this.sequence=sequence;
		this.sequencer=MidiSystem.getSequencer(true);
		sequencer.open();
		sequencer.setSequence(sequence);
		Slider timeSlider=new Slider(0,sequence.getTickLength(),0);
		HBox.setHgrow(timeSlider,Priority.ALWAYS);
		timeSlider.setMaxWidth(Double.MAX_VALUE);
		Button playButton=new Button(">");
		playButton.setOnAction((e)->{
			sequencer.start();
		});
		getChildren().add(playButton);
		Button stopButton=new Button("||");
		stopButton.setOnAction((e)->{
			sequencer.stop();
			timeSlider.setValue(sequencer.getTickPosition());
		});
		getChildren().add(stopButton);
		timeSlider.valueProperty().addListener((ov)->{
			sequencer.setTickPosition((long)timeSlider.getValue());
		});
		getChildren().add(timeSlider);
	}
}
