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
		buf.append(sequence.getResolution()).append(' ');
		buf.append(MessageRegistry.getString("DIVISION_TYPE",MediaEditorModule.NAME)).append(':');
		buf.append(getDivisionTypeName(sequence.getDivisionType())).append(' ');
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
	private TreeTableColumn<Object,Number> getTypeColumn(){
		TreeTableColumn<Object,Number> column=new TreeTableColumn<>(MessageRegistry.getString("TYPE",MediaEditorModule.NAME));
		column.setCellValueFactory((param)->{
			Object val=param.getValue().getValue();
			if(val instanceof MidiEvent){
				MidiMessage value=((MidiEvent)val).getMessage();
				if(value instanceof ShortMessage)
					return new ReadOnlyLongWrapper(((ShortMessage)value).getCommand());
					//return new SimpleLongProperty(value,"tick",((MidiEvent)value).getTick());
				else if(value instanceof MetaMessage)
					return new ReadOnlyLongWrapper(((MetaMessage)value).getType());
				else
					return new ReadOnlyLongWrapper(-1);
			}else
				return new ReadOnlyLongWrapper(-1);
		});
		return column;
	}
	private TreeTableColumn<Object,Number> getData1Column(){
		TreeTableColumn<Object,Number> column=new TreeTableColumn<>(MessageRegistry.getString("DATA1",MediaEditorModule.NAME));
		column.setCellValueFactory((param)->{
			Object val=param.getValue().getValue();
			if(val instanceof MidiEvent){
				MidiMessage value=((MidiEvent)val).getMessage();
				if(value instanceof ShortMessage)
					return new ReadOnlyLongWrapper(((ShortMessage)value).getData1());
					//return new SimpleLongProperty(value,"tick",((MidiEvent)value).getTick());
				else
					return new ReadOnlyLongWrapper(value.getLength());
			}else
				return new ReadOnlyLongWrapper(-1);
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
					return new ReadOnlyObjectWrapper<>(((ShortMessage)value).getData2());
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
	private String getTrackTitle(Track track){
		return NumberFormat.getIntegerInstance().format(track.size())+
				MessageRegistry.getString("EVENTS",MediaEditorModule.NAME)+
				NumberFormat.getIntegerInstance().format(track.ticks())+
				MessageRegistry.getString("TICKS",MediaEditorModule.NAME);
	}
}
