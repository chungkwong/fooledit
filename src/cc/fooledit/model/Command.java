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
package cc.fooledit.model;
import cc.fooledit.api.*;
import cc.fooledit.util.*;
import com.github.chungkwong.jschememin.type.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Command{
	private final String name;
	private final ThrowableFunction<ScmPairOrNil,ScmObject> action;
	private final List<String> parameters;
	private static Pair<Command,ScmPairOrNil> lastCommand=new Pair<>(null,null);
	public Command(String name,ThrowableFunction<ScmPairOrNil,ScmObject> action){
		this(name,Collections.emptyList(),action);
	}
	public Command(String name,List<String> parameters,ThrowableFunction<ScmPairOrNil,ScmObject> action){
		this.action=action;
		this.name=name;
		this.parameters=parameters;
	}
	public String getDisplayName(){
		return MessageRegistry.getString(name.toUpperCase().replace('-','_'));
	}
	public List<String> getParameters(){
		return parameters;
	}
	public ScmObject accept(ScmPairOrNil t){
		try{
			return action.accept(t);
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,name+MessageRegistry.getString("FAILED"),ex);
			return null;
		}finally{
			if(!(name.equals("command")||name.equals("restore")||name.equals("repeat")))
				lastCommand=new Pair<>(this,t);
		}
	}
	public static ScmObject repeat(int times){
		if(lastCommand!=null){
			ScmListBuilder buf=new ScmListBuilder();
			for(int i=0;i<times;i++)
				buf.add(lastCommand.getKey().accept(lastCommand.getValue()));
			return buf.toList();
		}else
			return ScmNil.NIL;
	}
}