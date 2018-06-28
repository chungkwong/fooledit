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
import com.github.chungkwong.jschememin.*;
import java.io.*;
import java.util.*;
import javax.script.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ScriptAPI{
	public static final String ENGINE="script_engine";
	private final ScriptEngineManager manager=new ScriptEngineManager();
	private final SimpleScriptContext CONTEXT;
	private final Evaluator evaluator=new Evaluator(true);
	public ScriptAPI(){
		this.CONTEXT=new SimpleScriptContext();
		EvaluatorFactory factory=EvaluatorFactory.INSTANCE;
		factory.getExtensions().forEach((ext)->manager.registerEngineExtension(ext,factory));
		factory.getNames().forEach((name)->manager.registerEngineExtension(name,factory));
		factory.getNames().forEach((name)->manager.put(name,factory));
		factory.getMimeTypes().forEach((type)->manager.registerEngineExtension(type,factory));
		CONTEXT.setBindings(new ScriptEnvironment(),SimpleScriptContext.GLOBAL_SCOPE);
	}
	public Object eval(Reader reader) throws ScriptException{
		return getDefaultEngine().eval(reader,CONTEXT);
	}
	public Object eval(String code) throws ScriptException{
		return getDefaultEngine().eval(code,CONTEXT);
	}
	public ScriptEngine getDefaultEngine(){
		String name=Objects.toString(CoreModule.MISC_REGISTRY.get(ENGINE));
		ScriptEngine engine=manager.getEngineByName(name);
		return engine!=null?engine:evaluator;
	}
	public static void main(String[] args){
		java.net.URL.setURLStreamHandlerFactory(FoolURLStreamHandler.INSTNACE);
		new Evaluator(true);
	}
}
