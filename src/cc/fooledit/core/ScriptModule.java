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
import java.io.*;
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
	public void onLoad()throws Exception{
		evalScript("on-load.scm");
	}
	@Override
	public void onUnLoad()throws Exception{
		evalScript("on-unload.scm");
	}
	@Override
	public void onInstall()throws Exception{
		evalScript("on-install.scm");
	}
	@Override
	public void onUninstall()throws Exception{
		evalScript("on-uninstall.scm");
	}
	private void evalScript(String filename)throws IOException,ScriptException{
		File file=Main.INSTANCE.getFile(filename,name);
		if(file.exists()){
			Main.INSTANCE.getScriptAPI().eval(Helper.readText(file));
		}
	}
}