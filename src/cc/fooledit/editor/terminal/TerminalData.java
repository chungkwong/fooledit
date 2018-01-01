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
import cc.fooledit.core.DataObject;
import cc.fooledit.core.DataObjectType;
import cc.fooledit.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TerminalData implements DataObject<TerminalData>{
	private final JComponent terminal;
	public TerminalData(){
		JComponent comp=null;
		try{
			File root=Main.INSTANCE.getModulePath("editor.terminal");
			List<URL> paths=new ArrayList<>();
			for(File f:new File(root,"lib").listFiles())
				paths.add(f.toURI().toURL());
			System.err.println(paths);
			URLClassLoader baseloader=new URLClassLoader(paths.toArray(new URL[0]));
			URLClassLoader loader=new URLClassLoader(new URL[]{new File(root,"Terminal.jar").toURI().toURL()},baseloader);
			comp=(JComponent)loader.loadClass("com.github.chungkwong.terminal.SimpleTerminal").newInstance();
		}catch(ReflectiveOperationException|MalformedURLException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
		terminal=comp;
	}
	public JComponent getTerminal(){
		return terminal;
	}
	@Override
	public DataObjectType<TerminalData> getDataObjectType(){
		return TerminalDataType.INSTANCE;
	}

}
