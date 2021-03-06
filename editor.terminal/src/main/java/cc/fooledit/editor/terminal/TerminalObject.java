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
package cc.fooledit.editor.terminal;
import cc.fooledit.core.*;
import com.kodedu.terminalfx.*;
import java.nio.file.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TerminalObject implements DataObject<TerminalObject>{
	private final TerminalTab terminal;
	public TerminalObject(){
		TerminalBuilder terminalBuilder=new TerminalBuilder();
		this.terminal=terminalBuilder.newTerminal();
	}
	public TerminalObject(Path path){
		TerminalBuilder terminalBuilder=new TerminalBuilder();
		terminalBuilder.setTerminalPath(path);
		this.terminal=terminalBuilder.newTerminal();
	}
	public TerminalTab getTerminal(){
		return terminal;
	}
	@Override
	public DataObjectType<TerminalObject> getDataObjectType(){
		return TerminalObjectType.INSTANCE;
	}

}
