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
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import com.github.chungkwong.json.*;
import com.sun.javafx.scene.control.skin.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.logging.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ScrollPaneWrapper extends ScrollPane{
	public ScrollPaneWrapper(){
		Main.INSTANCE.getKeymapManager().adopt(this,getKeymap(),getCommands());
	}
	public ScrollPaneWrapper(Node content){
		super(content);
		Main.INSTANCE.getKeymapManager().adopt(this,getKeymap(),getCommands());
	}
	private NavigableRegistryNode<String,String> getKeymap(){
		TreeMap<String,String> mapping=new TreeMap<>();
		try{
			mapping.putAll((Map<String,String>)JSONDecoder.decode(new InputStreamReader(getClass().getResourceAsStream("/scrollPane.json"),StandardCharsets.UTF_8)));
		}catch(IOException|SyntaxException ex){
			Logger.getLogger(TreeTableWrapper.class.getName()).log(Level.SEVERE,null,ex);
		}
		return new NavigableRegistryNode<>(mapping);
	}
	private RegistryNode<String,Command> getCommands(){
		RegistryNode<String,Command> registry=new SimpleRegistryNode<>();
		addCommand("scroll-up",()->((ScrollPaneSkin)getSkin()).vsbDecrement(),registry);
		addCommand("scroll-down",()->((ScrollPaneSkin)getSkin()).vsbIncrement(),registry);
		addCommand("scroll-left",()->((ScrollPaneSkin)getSkin()).hsbDecrement(),registry);
		addCommand("scroll-right",()->((ScrollPaneSkin)getSkin()).hsbIncrement(),registry);
		addCommand("scroll-to-top",()->setVvalue(getVmin()),registry);
		addCommand("scroll-to-bottom",()->setVvalue(getVmax()),registry);
		return registry;
	}
	private void addCommand(String name,Runnable action,ObservableMap<String,Command> registry){
		registry.put(name,new Command(name,action,Activator.class));
	}
}
