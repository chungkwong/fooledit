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
import cc.fooledit.util.*;
import com.github.chungkwong.jschememin.type.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import javafx.collections.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PaginationWrapper extends Pagination{
	public PaginationWrapper(){
		installCommands();
		installKeymap();
	}
	public PaginationWrapper(int pageCount){
		super(pageCount);
		installCommands();
		installKeymap();
	}
	public PaginationWrapper(int pageCount,int pageIndex){
		super(pageCount,pageIndex);
		installCommands();
		installKeymap();
	}
	private void installKeymap(){
		TreeMap<String,String> mapping=new TreeMap<>();
		File src=Main.getFile("keymaps/pagination.json",CoreModule.NAME);
		if(src!=null)
			mapping.putAll((Map<String,String>)(Object)Main.loadJSON(src));
		NavigableRegistryNode<String,String> registry=new NavigableRegistryNode<>(mapping);
		getProperties().put(WorkSheet.KEYMAP_NAME,registry);
	}
	private void installCommands(){
		RegistryNode<String,Command> registry=new SimpleRegistryNode<>();
		getProperties().put(WorkSheet.COMMANDS_NAME,registry);
		addCommand("current-page",Collections.emptyList(),(args)->ScmInteger.valueOf(getCurrentPageIndex()),registry);
		addCommand("number-of-pages",Collections.emptyList(),(args)->ScmInteger.valueOf(getPageCount()),registry);
		addCommand("move-to-page",Arrays.asList(new Argument("PAGE_NUMBER")),(args)->{
			setCurrentPageIndex(SchemeConverter.toInteger(ScmList.first(args)));
			return null;
		},registry);
		addCommand("move-to-first-page",()->setCurrentPageIndex(0),registry);
		addCommand("move-to-last-page",()->setCurrentPageIndex(getPageCount()-1),registry);
		addCommand("move-to-next-page",()->setCurrentPageIndex(getCurrentPageIndex()+1),registry);
		addCommand("move-to-previous-page",()->setCurrentPageIndex(getCurrentPageIndex()-1),registry);
	}
	private void addCommand(String name,Runnable action,ObservableMap<String,Command> registry){
		registry.put(name,new Command(name,action,CoreModule.NAME));
	}
	private void addCommand(String name,List<Argument> parameters,Function<ScmPairOrNil,ScmObject> action,ObservableMap<String,Command> registry){
		registry.put(name,new Command(name,parameters,(args)->action.apply(args),CoreModule.NAME));
	}



}
