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
package cc.fooledit.util;
import cc.fooledit.api.*;
import java.net.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FoolURLStreamHandler implements URLStreamHandlerFactory{
	public static final FoolURLStreamHandler INSTNACE=new FoolURLStreamHandler();
	private FoolURLStreamHandler(){

	}
	@Override
	public URLStreamHandler createURLStreamHandler(String protocol){
		URLStreamHandler handler=CoreModule.PROTOCOL_REGISTRY.getChild(protocol);
		if(handler!=null)
			return handler;
		String packagePrefix="sun.net.www.protocol";
		try{
			String clsName=packagePrefix+"."+protocol+".Handler";
			Class<?> cls=null;
			try{
				cls=Class.forName(clsName);
			}catch(ClassNotFoundException e){
				ClassLoader cl=ClassLoader.getSystemClassLoader();
				if(cl!=null){
					cls=cl.loadClass(clsName);
				}
			}
			if(cls!=null){
				handler=(URLStreamHandler)cls.newInstance();
				CoreModule.PROTOCOL_REGISTRY.addChild(clsName,handler);
				return handler;
			}
		}catch(Exception e){

		}
		return null;
	}
}
