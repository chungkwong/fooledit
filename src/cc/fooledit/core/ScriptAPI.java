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
import javax.script.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ScriptAPI{
	private final Evaluator SCHEME_ENGINE=new Evaluator(true);
	private final SimpleScriptContext CONTEXT;
	public ScriptAPI(){
		this.CONTEXT=new SimpleScriptContext();
		CONTEXT.setBindings(new ScriptEnvironment(),SimpleScriptContext.GLOBAL_SCOPE);
	}
	public Object eval(Reader reader) throws ScriptException{
		return SCHEME_ENGINE.eval(reader,CONTEXT);
	}
	public Object eval(String code) throws ScriptException{
		return SCHEME_ENGINE.eval(code,CONTEXT);
	}
}
