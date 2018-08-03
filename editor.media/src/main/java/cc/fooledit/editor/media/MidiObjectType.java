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
import java.net.*;
import javax.sound.midi.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MidiObjectType implements DataObjectType<MidiObject>{
	public static final MidiObjectType INSTANCE=new MidiObjectType();
	private MidiObjectType(){
	}
	@Override
	public boolean canRead(){
		return true;
	}
	@Override
	public boolean canWrite(){
		return true;
	}
	@Override
	public boolean canCreate(){
		return true;
	}
	@Override
	public MidiObject create(){
		try{
			return new MidiObject(new Sequence(Sequence.PPQ,4));
		}catch(InvalidMidiDataException ex){
			throw new RuntimeException(ex);
		}
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("MIDI",Activator.class);
	}
	@Override
	public void writeTo(MidiObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		MidiSystem.write(data.getSequence(),1,connection.getOutputStream());
	}
	@Override
	public MidiObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		return new MidiObject(MidiSystem.getSequence(connection.getInputStream()));
	}
}
