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
package cc.fooledit.core;
import cc.fooledit.*;
import cc.fooledit.util.*;
import com.github.chungkwong.jschememin.lib.*;
import com.github.chungkwong.jschememin.type.*;
import java.util.*;
import java.util.stream.*;
import javax.script.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ScriptEnvironment implements Bindings{
	private static Object pack(Command command){
		return new NativeEvaluable((o)->{
			return command.accept((ScmPairOrNil)o);
		});
	}
	private static Command unpack(Object o){
		if(o instanceof Command)
			return (Command)o;
		return null;
	}
	@Override
	public Object put(String name,Object value){
		return pack(Main.INSTANCE.getGlobalCommandRegistry().put(name,unpack(value)));
	}
	@Override
	public void putAll(Map<? extends String,? extends Object> toMerge){
		toMerge.forEach((k,v)->put(k,v));
	}
	@Override
	public boolean containsKey(Object key){
		return Main.INSTANCE.getCommand(Objects.toString(key))!=null;
	}
	@Override
	public Object get(Object key){
		Command command=Main.INSTANCE.getCommand(Objects.toString(key));
		return command==null?null:pack(command);
	}
	@Override
	public Object remove(Object key){
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	public int size(){
		return keySet().size();
	}
	@Override
	public boolean isEmpty(){
		return keySet().isEmpty();
	}
	@Override
	public boolean containsValue(Object value){
		return values().contains(unpack(value));
	}
	@Override
	public void clear(){
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	public Set<String> keySet(){
		return Main.INSTANCE.getCommandNames();
	}
	@Override
	public Collection<Object> values(){
		return keySet().stream().map((k)->pack((Command)get(k))).collect(Collectors.toList());
	}
	@Override
	public Set<Entry<String,Object>> entrySet(){
		return keySet().stream().map((k)->new Pair<>(k,pack((Command)get(k)))).collect(Collectors.toSet());
	}
}