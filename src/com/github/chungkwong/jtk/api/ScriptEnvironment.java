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
package com.github.chungkwong.jtk.api;
import java.util.*;
import java.util.function.*;
import javax.script.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ScriptEnvironment implements Bindings{
	private final CommandRegistry commands;
	private final HashMap<String,Object> bindings=new HashMap<>();
	public ScriptEnvironment(CommandRegistry commands){
		this.commands=commands;
	}
	@Override
	public Object put(String name,Object value){
		return bindings.put(name,value);
	}
	@Override
	public void putAll(Map<? extends String,? extends Object> toMerge){
		bindings.putAll(toMerge);
	}
	@Override
	public boolean containsKey(Object key){
		return bindings.containsKey(key)||commands.get(key.toString())!=null;
	}
	@Override
	public Object get(Object key){
		return bindings.containsKey(key)?bindings.get(key):commands.get(key.toString());
	}
	@Override
	public Object remove(Object key){
		return bindings.remove(key);
	}
	@Override
	public int size(){
		return bindings.size()+commands.size();
	}
	@Override
	public boolean isEmpty(){
		return bindings.isEmpty()&&commands.isEmpty();
	}
	@Override
	public boolean containsValue(Object value){
		return bindings.containsValue(value)||commands.containsValue(value);
	}
	@Override
	public void clear(){
		bindings.clear();
	}
	@Override
	public Set<String> keySet(){
		return new BiSet<>(bindings.keySet(),commands.keySet());
	}
	@Override
	public Collection<Object> values(){
		return new BiSet<>(bindings.values(),commands.values());
	}
	@Override
	public Set<Entry<String,Object>> entrySet(){
		return new BiSet<>(bindings.entrySet(),commands.entrySet());
	}
	private static class BiSet<T> extends AbstractSet<T>{
		private final Collection<T> set1,set2;
		public BiSet(Collection set1,Collection set2){
			this.set1=set1;
			this.set2=set2;
		}
		@Override
		public Iterator<T> iterator(){
			return new Iterator<T>(){
				Iterator<T> iter1=set1.iterator(),iter2=set2.iterator();
				@Override
				public boolean hasNext(){
					return iter1.hasNext()||iter2.hasNext();
				}
				@Override
				public T next(){
					return iter1.hasNext()?iter1.next():iter2.next();
				}
			};
		}
		@Override
		public int size(){
			return set1.size()+set2.size();
		}
	}
	public static void main(String[] args){
		ScriptEngineManager scriptEngineManager=new ScriptEngineManager();
		ScriptEngine engine=scriptEngineManager.getEngineByName("nashorn");
		CommandRegistry commandRegistry=new CommandRegistry();

		commandRegistry.put("command",()->System.err.println("fired"));
		Bindings bindings=new ScriptEnvironment(commandRegistry);
		bindings.put("yyy",new Function<String,Integer>() {
			@Override
			public Integer apply(String t){
				System.out.println("got: "+t);
				return 12;
			}
		});
		try{
			engine.eval("print(yyy('bad')+5);",bindings);
			engine.eval("command();",bindings);
		}catch(final ScriptException se){
			se.printStackTrace();
		}
		System.out.println(bindings.entrySet());
	}
}