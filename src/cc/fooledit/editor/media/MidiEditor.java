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
import cc.fooledit.control.*;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javax.sound.midi.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MidiEditor implements DataEditor<MidiObject>{
	public static final MidiEditor INSTANCE=new MidiEditor();
	public MidiEditor(){
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
class MidiViewer extends BorderPane{
	private final Sequence sequence;
	private final Sequencer sequencer;
	public MidiViewer(Sequence sequence)throws MidiUnavailableException, InvalidMidiDataException{
		this.sequence=sequence;
		this.sequencer=MidiSystem.getSequencer(true);
		sequencer.open();
		sequencer.setSequence(sequence);
		setBottom(getToolBar());
		VBox tracks=new VBox();
		TreeTableView<Object> events=new TreeTableView<>(getSequenceTreeItem());
		events.setShowRoot(false);
		events.getColumns().setAll(getTimeColumn(),getChannelColumn(),getTypeColumn(),getData1Column(),getData2Column());
		setCenter(events);
		setTop(getInfo());
	}
	private Label getInfo(){
		StringBuilder buf=new StringBuilder();
		buf.append(MessageRegistry.getString("LENGTH",MediaEditorModule.NAME)).append(':');
		buf.append(sequence.getTickLength());
		buf.append(MessageRegistry.getString("TICKS",MediaEditorModule.NAME)).append('=');
		buf.append(sequence.getMicrosecondLength()/1000000.0);
		buf.append(MessageRegistry.getString("SECONDS",MediaEditorModule.NAME)).append(' ');
		buf.append(MessageRegistry.getString("RESOLUTION",MediaEditorModule.NAME)).append(':');
		buf.append(sequence.getResolution()).append('/');
		buf.append(MessageRegistry.getString(getDivisionTypeName(sequence.getDivisionType()),MediaEditorModule.NAME));
		return new Label(buf.toString());
	}
	private String getDivisionTypeName(float type){
		if(type==Sequence.PPQ)
			return "PPQ";
		else if(type==Sequence.SMPTE_24)
			return "SMPTE24";
		else if(type==Sequence.SMPTE_25)
			return "SMPTE25";
		else if(type==Sequence.SMPTE_30)
			return "SMPTE30";
		else if(type==Sequence.SMPTE_30DROP)
			return "SMPTE30DROP";
		return Float.toString(type);
	}
	private LazyTreeItem<Object> getSequenceTreeItem(){
		return new LazyTreeItem<>(sequence,()->
				Arrays.stream(sequence.getTracks()).map(this::getTrackTreeItem).collect(Collectors.toList()));
	}
	private LazyTreeItem<Object> getTrackTreeItem(Track track){
		return new LazyTreeItem<>(track,()->{
			int size=track.size();
			List<TreeItem<Object>> events=new ArrayList<>(size);
			for(int i=0;i<size;i++)
				events.add(new TreeItem<>(track.get(i)));
			return events;
		});
	}
	private TreeTableColumn<Object,Number> getTimeColumn(){
		TreeTableColumn<Object,Number> column=new TreeTableColumn<>(MessageRegistry.getString("TIME",MediaEditorModule.NAME));
		column.setCellValueFactory((param)->{
			Object value=param.getValue().getValue();
			if(value instanceof MidiEvent)
				//return new ReadOnlyLongWrapper(((MidiEvent)value).getTick());
				return new SimpleLongProperty(value,"tick",((MidiEvent)value).getTick());
			else
				return new ReadOnlyLongWrapper(-1);
		});
		return column;
	}
	private TreeTableColumn<Object,Number> getChannelColumn(){
		TreeTableColumn<Object,Number> column=new TreeTableColumn<>(MessageRegistry.getString("CHANNEL",MediaEditorModule.NAME));
		column.setCellValueFactory((param)->{
			Object val=param.getValue().getValue();
			if(val instanceof MidiEvent){
				MidiMessage value=((MidiEvent)val).getMessage();
				if(value instanceof ShortMessage)
					return new ReadOnlyLongWrapper(((ShortMessage)value).getChannel());
					//return new SimpleLongProperty(value,"tick",((MidiEvent)value).getTick());
				else if(value instanceof MetaMessage)
					return new ReadOnlyLongWrapper(-2);
				else if(value instanceof SysexMessage)
					return new ReadOnlyLongWrapper(-3);
				else
					return new ReadOnlyLongWrapper(-4);
			}else
				return new ReadOnlyLongWrapper(-1);
		});
		return column;
	}
	private TreeTableColumn<Object,String> getTypeColumn(){
		TreeTableColumn<Object,String> column=new TreeTableColumn<>(MessageRegistry.getString("TYPE",MediaEditorModule.NAME));
		column.setCellValueFactory((param)->{
			Object val=param.getValue().getValue();
			if(val instanceof MidiEvent){
				MidiMessage value=((MidiEvent)val).getMessage();
				if(value instanceof ShortMessage)
					return new ReadOnlyStringWrapper(getCommandName(((ShortMessage)value).getCommand()));
					//return new SimpleLongProperty(value,"tick",((MidiEvent)value).getTick());
				else if(value instanceof MetaMessage)
					return new ReadOnlyStringWrapper(getTypeName(((MetaMessage)value).getType()));
				else
					return new ReadOnlyStringWrapper(MessageRegistry.getString("UNKNOWN",MediaEditorModule.NAME));
			}else
				return new ReadOnlyStringWrapper("");
		});
		return column;
	}
	private TreeTableColumn<Object,String> getData1Column(){
		TreeTableColumn<Object,String> column=new TreeTableColumn<>(MessageRegistry.getString("DATA1",MediaEditorModule.NAME));
		column.setCellValueFactory((param)->{
			Object val=param.getValue().getValue();
			if(val instanceof MidiEvent){
				MidiMessage value=((MidiEvent)val).getMessage();
				if(value instanceof ShortMessage)
					return new ReadOnlyStringWrapper(getData1((ShortMessage)value));
					//return new SimpleLongProperty(value,"tick",((MidiEvent)value).getTick());
				else
					return new ReadOnlyStringWrapper(Integer.toString(value.getLength()));
			}else
				return new ReadOnlyStringWrapper("");
		});
		return column;
	}
	private TreeTableColumn<Object,Object> getData2Column(){
		TreeTableColumn<Object,Object> column=new TreeTableColumn<>(MessageRegistry.getString("DATA2",MediaEditorModule.NAME));
		column.setCellValueFactory((param)->{
			Object val=param.getValue().getValue();
			if(val instanceof MidiEvent){
				MidiMessage value=((MidiEvent)val).getMessage();
				if(value instanceof ShortMessage)
					return new ReadOnlyObjectWrapper<>(getData2((ShortMessage)value));
					//return new SimpleLongProperty(value,"tick",((MidiEvent)value).getTick());
				else
					return new ReadOnlyObjectWrapper<>(value.getMessage());
			}else
				return new ReadOnlyObjectWrapper<>("");
		});
		return column;
	}
	private Node getToolBar(){
		Slider timeSlider=new Slider(0,sequence.getTickLength(),0);
		HBox.setHgrow(timeSlider,Priority.ALWAYS);
		timeSlider.setMaxWidth(Double.MAX_VALUE);
		Button playButton=new Button(">");
		playButton.setOnAction((e)->{
			sequencer.start();
		});
		Button stopButton=new Button("||");
		stopButton.setOnAction((e)->{
			sequencer.stop();
			timeSlider.setValue(sequencer.getTickPosition());
		});
		timeSlider.valueProperty().addListener((ov)->{
			sequencer.setTickPosition((long)timeSlider.getValue());
		});
		return new HBox(playButton,stopButton,timeSlider);
	}
	private String getCommandName(int command){
		switch(command){
			case ShortMessage.ACTIVE_SENSING:return "ACTIVE_SENSING";
			case ShortMessage.CHANNEL_PRESSURE:return "CHANNEL_PRESSURE";
			case ShortMessage.CONTINUE:return "CONTINUE";
			case ShortMessage.CONTROL_CHANGE:return "CONTROL_CHANGE";
			case ShortMessage.END_OF_EXCLUSIVE:return "END_OF_EXCLUSIVE";
			case ShortMessage.MIDI_TIME_CODE:return "MIDI_TIME_CODE";
			case ShortMessage.NOTE_OFF:return "NOTE_OFF";
			case ShortMessage.NOTE_ON:return "NOTE_ON";
			case ShortMessage.PITCH_BEND:return "PITCH_BEND";
			case ShortMessage.POLY_PRESSURE:return "POLY_PRESSURE";
			case ShortMessage.PROGRAM_CHANGE:return "PROGRAM_CHANGE";
			case ShortMessage.SONG_POSITION_POINTER:return "SONG_POSITION_POINTER";
			case ShortMessage.SONG_SELECT:return "SONG_SELECT";
			case ShortMessage.START:return "START";
			case ShortMessage.STOP:return "STOP";
			case ShortMessage.SYSTEM_RESET:return "SYSTEM_RESET";
			case ShortMessage.TIMING_CLOCK:return "TIMING_CLOCK";
			case ShortMessage.TUNE_REQUEST:return "TUNE_REQUEST";
			default:return Integer.toString(command);
		}
	}
	private String getTypeName(int type){
		switch(type){
			case 0x00:return "SEQUENCE_NUMBER";
			case 0x01:return "TEXT_EVENT";
			case 0x02:return "COPYRIGHT_NOTICE";
			case 0x03:return "NAME";
			case 0x04:return "INSTRUMENT";
			case 0x05:return "LYRIC";
			case 0x06:return "MARKER";
			case 0x07:return "CUE_POINT";
			case 0x20:return "MIDI_CHANNEL_PREFIX";
			case 0x2F:return "END_OF_TRACK";
			case 0x51:return "SET_TEMPO";
			case 0x54:return "SMPTE_OFFSET";
			case 0x58:return "TIME_SIGNATURE";
			case 0x59:return "KEY_SIGNATURE";
			case 0x7F:return "SEQUENCER_SPECIFIC";
			default:return Integer.toString(type);
		}
	}
	private String getData1(ShortMessage msg){
		int data1=msg.getData1();
		switch(msg.getCommand()){
			case ShortMessage.ACTIVE_SENSING:return "";
			case ShortMessage.CHANNEL_PRESSURE:return Integer.toString(data1);
			case ShortMessage.CONTINUE:return "";
			case ShortMessage.CONTROL_CHANGE:return getControlName(data1);
			case ShortMessage.END_OF_EXCLUSIVE:return "";
			case ShortMessage.MIDI_TIME_CODE:return getControlName(data1);
			case ShortMessage.NOTE_OFF:return getNodeName(data1);
			case ShortMessage.NOTE_ON:return getNodeName(data1);
			case ShortMessage.PITCH_BEND:return Integer.toString(data1<<8|msg.getData2());
			case ShortMessage.POLY_PRESSURE:return getNodeName(data1);
			case ShortMessage.PROGRAM_CHANGE:return getProgramName(data1);
			case ShortMessage.SONG_POSITION_POINTER:return Integer.toString(data1<<8|msg.getData2());
			case ShortMessage.SONG_SELECT:return Integer.toString(data1);
			case ShortMessage.START:return "";
			case ShortMessage.STOP:return "";
			case ShortMessage.SYSTEM_RESET:return "";
			case ShortMessage.TIMING_CLOCK:return "";
			case ShortMessage.TUNE_REQUEST:return "";
			default:return Integer.toString(data1);
		}
	}
	private String getData2(ShortMessage msg){
		int data1=msg.getData1();
		switch(msg.getCommand()){
			case ShortMessage.ACTIVE_SENSING:return "";
			case ShortMessage.CHANNEL_PRESSURE:return "";
			case ShortMessage.CONTINUE:return "";
			case ShortMessage.CONTROL_CHANGE:return Integer.toString(data1);
			case ShortMessage.END_OF_EXCLUSIVE:return "";
			case ShortMessage.MIDI_TIME_CODE:return Integer.toString(data1);
			case ShortMessage.NOTE_OFF:return Integer.toString(data1);
			case ShortMessage.NOTE_ON:return Integer.toString(data1);
			case ShortMessage.PITCH_BEND:return "";
			case ShortMessage.POLY_PRESSURE:return Integer.toString(data1);
			case ShortMessage.PROGRAM_CHANGE:return "";
			case ShortMessage.SONG_POSITION_POINTER:return "";
			case ShortMessage.SONG_SELECT:return "";
			case ShortMessage.START:return "";
			case ShortMessage.STOP:return "";
			case ShortMessage.SYSTEM_RESET:return "";
			case ShortMessage.TIMING_CLOCK:return "";
			case ShortMessage.TUNE_REQUEST:return "";
			default:return Integer.toString(data1);
		}
	}
	private static final String[] NOTES=new String[]{"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
	private String getNodeName(int note){
		return NOTES[note%12]+(note/12-1);
	}
	private String getProgramName(int program){
		Instrument[] instruments;
		try{
			instruments=MidiSystem.getSynthesizer().getAvailableInstruments();
			if(program<instruments.length)
				return instruments[program].getName();
		}catch(MidiUnavailableException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
		return Integer.toString(program);
	}
	private String getControlName(int control){
		switch(control){
			case 0: return "Bank Select";
			case 1: return "Modulation wheel";
			case 2: return "Breath control";
			case 4: return "Foot controller";
			case 5: return "Portamento time";
			case 6: return "Data Entry";
			case 7: return "Channel Volume (formerly Main Volume)";
			case 8: return "Balance";
			case 10: return "Pan";
			case 11: return "Expression Controller";
			case 12: return "Effect control 1";
			case 13: return "Effect control 2";
			case 16: return "General Purpose Controller #1";
			case 17: return "General Purpose Controller #2";
			case 18: return "General Purpose Controller #3";
			case 19: return "General Purpose Controller #4";
			case 32: return "Bank Select";
			case 33: return "Modulation wheel";
			case 34: return "Breath control";
			case 36: return "Foot controller";
			case 37: return "Portamento time";
			case 38: return "Data entry";
			case 39: return "Channel Volume (formerly Main Volume)";
			case 40: return "Balance";
			case 42: return "Pan";
			case 43: return "Expression Controller";
			case 44: return "Effect control 1";
			case 45: return "Effect control 2";
			case 48: return "General Purpose Controller #1";
			case 49: return "General Purpose Controller #2";
			case 50: return "General Purpose Controller #3";
			case 51: return "General Purpose Controller #4";
			case 64: return "Damper pedal on/off (Sustain) ";
			case 65: return "Portamento on/off";
			case 66: return "Sustenuto on/off";
			case 67: return "Soft pedal on/off";
			case 68: return "Legato Footswitch";
			case 69: return "Hold 2";
			case 70: return "Sound Controller 1 (Sound Variation)";
			case 71: return "Sound Controller 2 (Timbre)";
			case 72: return "Sound Controller 3 (Release Time)";
			case 73: return "Sound Controller 4 (Attack Time)";
			case 74: return "Sound Controller 5 (Brightness)";
			case 75: return "Sound Controller 6";
			case 76: return "Sound Controller 7";
			case 77: return "Sound Controller 8";
			case 78: return "Sound Controller 9";
			case 79: return "Sound Controller 10";
			case 80: return "General Purpose Controller #5";
			case 81: return "General Purpose Controller #6";
			case 82: return "General Purpose Controller #7";
			case 83: return "General Purpose Controller #8";
			case 84: return "Portamento Control";
			case 91: return "Effects 1 Depth";
			case 92: return "Effects 2 Depth";
			case 93: return "Effects 3 Depth";
			case 94: return "Effects 4 Depth";
			case 95: return "Effects 5 Depth";
			case 96: return "Data entry +1";
			case 97: return "Data entry -1";
			case 98: return "Non-Registered Parameter";
			case 99: return "Non-Registered Parameter";
			case 100: return "Registered Parameter";
			case 101: return "Registered Parameter";
			case 120: return "All Sound Off";
			case 121: return "Reset All Controllers";
			case 122: return "Local control on/off";
			case 123: return "All notes off";
			case 124: return "Omni mode off (+ all notes off)";
			case 125: return "Omni mode on (+ all notes off)";
			case 126: return "Poly mode on/off (+ all notes off)";
			case 127: return "Poly mode on (incl mono=off +all notes off)";
			default:return Integer.toString(control);
		}
	}
	private String getTrackTitle(Track track){
		return NumberFormat.getIntegerInstance().format(track.size())+
				MessageRegistry.getString("EVENTS",MediaEditorModule.NAME)+
				NumberFormat.getIntegerInstance().format(track.ticks())+
				MessageRegistry.getString("TICKS",MediaEditorModule.NAME);
	}
}
