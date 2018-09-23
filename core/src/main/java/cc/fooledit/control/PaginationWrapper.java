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
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import javafx.collections.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PaginationWrapper extends Pagination{
	public PaginationWrapper(){
		Main.INSTANCE.getKeymapManager().adopt(this,getKeymap(),getCommands());
	}
	public PaginationWrapper(int pageCount){
		super(pageCount);
		Main.INSTANCE.getKeymapManager().adopt(this,getKeymap(),getCommands());
	}
	public PaginationWrapper(int pageCount,int pageIndex){
		super(pageCount,pageIndex);
		Main.INSTANCE.getKeymapManager().adopt(this,getKeymap(),getCommands());
	}
	private NavigableRegistryNode<String,String> getKeymap(){
		TreeMap<String,String> mapping=new TreeMap<>();
		try{
			mapping.putAll((Map<String,String>)JSONDecoder.decode(new InputStreamReader(getClass().getResourceAsStream("/pagination.json"),StandardCharsets.UTF_8)));
		}catch(IOException|SyntaxException ex){
			Logger.getLogger(TreeTableWrapper.class.getName()).log(Level.SEVERE,null,ex);
		}
		return new NavigableRegistryNode<>(mapping);
	}
	private RegistryNode<String,Command> getCommands(){
		RegistryNode<String,Command> registry=new SimpleRegistryNode<>();
		addCommand("current-page",Collections.emptyList(),(args)->getCurrentPageIndex(),registry);
		addCommand("number-of-pages",Collections.emptyList(),(args)->getPageCount(),registry);
		addCommand("move-to-page",Arrays.asList(new Argument("PAGE_NUMBER")),(args)->{
			setCurrentPageIndex(((Number)args[0]).intValue());
			return null;
		},registry);
		addCommand("move-to-first-page",()->setCurrentPageIndex(0),registry);
		addCommand("move-to-last-page",()->setCurrentPageIndex(getPageCount()-1),registry);
		addCommand("move-to-next-page",()->setCurrentPageIndex(getCurrentPageIndex()+1),registry);
		addCommand("move-to-previous-page",()->setCurrentPageIndex(getCurrentPageIndex()-1),registry);
		return registry;
	}
	private void addCommand(String name,Runnable action,ObservableMap<String,Command> registry){
		registry.put(name,new Command(name,action,Activator.class));
	}
	private void addCommand(String name,List<Argument> parameters,Function<Object[],Object> action,ObservableMap<String,Command> registry){
		registry.put(name,new Command(name,parameters,(args)->action.apply(args),Activator.class));
	}
}
