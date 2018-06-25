/*
 * Copyright (C) 2017,2018 Chan Chung Kwong <1m02math@126.com>
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
import cc.fooledit.util.*;
import java.util.*;
import javafx.application.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Command{
	private final String module;
	private final String name;
	private final ThrowableVarargsFunction<Object,Object> action;
	private final List<Argument> parameters;
	private final boolean interactive;
	private static Pair<Command,Object[]> lastCommand=new Pair<>(null,null);
	public Command(String name,Runnable action,String module){
		this(name,action,module,true);
	}
	public Command(String name,ThrowableVarargsFunction<Object,Object> action,String module){
		this(name,action,module,true);
	}
	public Command(String name,List<Argument> parameters,ThrowableVarargsFunction<Object,Object> action,String module){
		this(name,parameters,action,module,true);
	}
	public Command(String name,Runnable action,String module,boolean interactive){
		this(name,(t)->{
			action.run();
			return null;
		},module,interactive);
	}
	public Command(String name,ThrowableVarargsFunction<Object,Object> action,String module,boolean interactive){
		this(name,Collections.emptyList(),action,module,interactive);
	}
	public Command(String name,List<Argument> parameters,ThrowableVarargsFunction<Object,Object> action,String module,boolean interactive){
		this.action=action;
		this.module=module;
		this.name=name;
		this.parameters=parameters;
		this.interactive=interactive;
		//getDisplayName();
	}
	public String getDisplayName(){
		return MessageRegistry.getString(name.toUpperCase().replace('-','_'),module);
	}
	public String getName(){
		return name;
	}
	public String getModule(){
		return module;
	}
	public List<Argument> getParameters(){
		return parameters;
	}
	public boolean isInteractive(){
		return interactive;
	}
	public Object accept(Object[] t) throws Exception{
		try{
			if(isInteractive()&&!Platform.isFxApplicationThread()){
				Object[] result=new Object[1];
				Exception[] exception=new Exception[1];
				Platform.runLater(()->{
					try{
						result[0]=action.accept(t);
					}catch(Exception ex){
						exception[0]=ex;
					}
					synchronized(result){
						result.notifyAll();
					}
				});
				synchronized(result){
					result.wait();
				}
				if(exception[0]==null){
					return result[0];
				}else{
					throw new Exception(exception[0]);
				}
			}else{
				return action.accept(t);
			}
		}finally{
			if(!(name.equals("command")||name.equals("restore")||name.equals("repeat"))){
				lastCommand=new Pair<>(this,t);
			}
		}
	}
	public static Object repeat(int times) throws Exception{
		if(lastCommand!=null){
			List<Object> buf=new ArrayList<>();
			for(int i=0;i<times;i++){
				buf.add(lastCommand.getKey().accept(lastCommand.getValue()));
			}
			return buf;
		}else{
			return null;
		}
	}
	/*public static void main(String[] args) throws ScriptException{
		ScriptEngine engine=EvaluatorFactory.INSTANCE.getScriptEngine();
		SimpleBindings bindings=new SimpleBindings();
		Function<String,String> f=new Function<String,String>(){
			@Override
			public String apply(String t){
				return t.toUpperCase();
			}
		};
		bindings.put("up",f);
		bindings.put("str","hello");
		engine.setBindings(bindings,ScriptContext.GLOBAL_SCOPE);
		System.out.println(engine.eval("(up \"hello\")"));
	}*/
}
