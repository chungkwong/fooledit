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
package cc.fooledit.example.terminal;
import cc.fooledit.*;
import cc.fooledit.api.*;
import cc.fooledit.spi.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TerminalModule{
	public static final String NAME="editor.terminal";
	public static void onLoad(){
		ApplicationRegistry.register("terminal","fooledit/terminal",TerminalDataType.INSTANCE,TerminalData.class,()->TerminalEditor.INSTANCE);
		Main.INSTANCE.getGlobalCommandRegistry().put("terminal",()->Main.INSTANCE.addAndShow(DataObjectRegistry.create(TerminalDataType.INSTANCE)),NAME);
	}
	public static void onUnLoad(){

	}
}
