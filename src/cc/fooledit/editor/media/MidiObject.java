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
import java.util.*;
import javax.sound.midi.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MidiObject implements DataObject<MidiObject>{
	private final Sequence sequence;
	public MidiObject(Sequence sequence){
		this.sequence=sequence;
	}
	@Override
	public DataObjectType<MidiObject> getDataObjectType(){
		return MidiObjectType.INSTANCE;
	}
	public Sequence getSequence(){
		return sequence;
	}
	public static void main(String[] args) throws InvalidMidiDataException, MidiUnavailableException, InterruptedException{
		System.out.println(Arrays.toString(MidiSystem.getSynthesizer().getAvailableInstruments()));
		ShortMessage myMsg=new ShortMessage();
		long timeStamp=-1;
		Receiver rcvr=MidiSystem.getReceiver();
		myMsg.setMessage(ShortMessage.PROGRAM_CHANGE,0,25,0);
		rcvr.send(myMsg,timeStamp);
		myMsg.setMessage(ShortMessage.NOTE_ON,0,60,93);
		rcvr.send(myMsg,timeStamp);
		Thread.sleep(4000);
	}
}
