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
package cc.fooledit.api;
import cc.fooledit.model.*;
import com.github.chungkwong.jschememin.type.*;
import java.util.*;
import java.util.function.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CommandRegistry extends HashMap<String,Command>{
	public CommandRegistry(){
	}
	public void put(String name,Runnable action){
		put(name,(t)->{
			action.run();
			return null;
		});
	}
	public void put(String name,ThrowableFunction<ScmPairOrNil,ScmObject> action){
		put(name,new Command(name,action));
		//System.err.println(new Command(name,action).getDisplayName());
	}
	public void putOnDemand(String name,Supplier<Command> supplier){
		put(name,(t)->{
			Command command=supplier.get();
			put(name,command);
			return command.accept(t);
		});
	}
}
