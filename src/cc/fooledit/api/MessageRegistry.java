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
import cc.fooledit.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MessageRegistry{
	private static final HashMap<String,ResourceBundle> bundles=new HashMap<>();
	public static void addBundle(String id,ResourceBundle bundle){
		bundles.put(id,bundle);
	}
	public static void addBundle(String module){
		try{
			addBundle(module,ResourceBundle.getBundle("messages",Locale.getDefault(),
					new URLClassLoader(new URL[]{new File(new File(Main.getDataPath(),module),"locales").toURI().toURL()})));
		}catch(MalformedURLException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
	}
	public static String getString(String key,String bundleId){
		ResourceBundle bundle=bundles.get(bundleId);
		String value;
		if(bundle!=null&&(value=bundle.getString(key))!=null)
			return value;
		else{
			Logger.getGlobal().log(Level.INFO,"Missing string: {0}",key);
			return key;
		}
	}
	static{
		addBundle("core");
	}
}
