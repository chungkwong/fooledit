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
package com.github.chungkwong.fooledit.api;
import com.github.chungkwong.fooledit.*;
import java.io.*;
import java.util.logging.*;
import javax.script.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ScriptModule extends Module{
	public ScriptModule(String name){
		super(name);
	}
	@Override
	public void onLoad(){
		evalScript("on-load.scm");
	}
	@Override
	public void onUnLoad(){
		evalScript("on-unload.scm");
	}
	private void evalScript(String file){
		try{
			Main.INSTANCE.getScriptAPI().eval(Helper.readText(new File(Main.getModulePath(name),file)));
		}catch(IOException|ScriptException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
}
