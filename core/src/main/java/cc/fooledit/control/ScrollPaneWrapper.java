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
package cc.fooledit.control;
import cc.fooledit.*;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import com.sun.javafx.scene.control.skin.*;
import java.io.*;
import java.util.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ScrollPaneWrapper extends ScrollPane{
	public ScrollPaneWrapper(){
		installCommands();
		installKeymap();
	}
	public ScrollPaneWrapper(Node content){
		super(content);
		installCommands();
		installKeymap();
	}
	private void installKeymap(){
		TreeMap<String,String> mapping=new TreeMap<>();
		File src=Main.getFile("keymaps/scrollPane.json",CoreModule.NAME);
		if(src!=null)
			mapping.putAll((Map<String,String>)(Object)Main.loadJSON(src));
		NavigableRegistryNode<String,String> registry=new NavigableRegistryNode<>(mapping);
		getProperties().put(WorkSheet.KEYMAP_NAME,registry);
	}
	private void installCommands(){
		RegistryNode<String,Command> registry=new SimpleRegistryNode<>();
		getProperties().put(WorkSheet.COMMANDS_NAME,registry);
		addCommand("scroll-up",()->((ScrollPaneSkin)getSkin()).vsbDecrement(),registry);
		addCommand("scroll-down",()->((ScrollPaneSkin)getSkin()).vsbIncrement(),registry);
		addCommand("scroll-left",()->((ScrollPaneSkin)getSkin()).hsbDecrement(),registry);
		addCommand("scroll-right",()->((ScrollPaneSkin)getSkin()).hsbIncrement(),registry);
		addCommand("scroll-to-top",()->setVvalue(getVmin()),registry);
		addCommand("scroll-to-bottom",()->setVvalue(getVmax()),registry);
	}
	private void addCommand(String name,Runnable action,ObservableMap<String,Command> registry){
		registry.put(name,new Command(name,action,CoreModule.NAME));
	}
}
