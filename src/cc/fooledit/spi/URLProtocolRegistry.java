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
package cc.fooledit.spi;
import java.net.*;
import java.util.*;
import java.util.function.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class URLProtocolRegistry implements URLStreamHandlerFactory{
	private static final URLProtocolRegistry INSTNACE=new URLProtocolRegistry();
	private static final Map<String,Supplier<URLStreamHandler>> registry=new HashMap<>();
	private URLProtocolRegistry(){

	}
	public static void register(String protocol,Supplier<URLStreamHandler> handler){
		registry.put(protocol,handler);
	}
	public static URLProtocolRegistry get(){
		return INSTNACE;
	}
	@Override
	public URLStreamHandler createURLStreamHandler(String protocol){
		Supplier<URLStreamHandler> supplier=registry.get(protocol);
		if(supplier!=null)
			return supplier.get();
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
				URLStreamHandler handler=(URLStreamHandler)cls.newInstance();
				registry.put(protocol,()->handler);
				return handler;
			}
		}catch(Exception e){

		}
		return null;
	}
}
