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
import cc.fooledit.api.*;
import cc.fooledit.model.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.activation.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ApplicationRegistry extends URLStreamHandler{
	private static final Map<String,MimeType> registry=new HashMap<>();
	public static void register(String path,String baseType,DataObjectType factory) throws MimeTypeParseException{
		registry.put(path,new MimeType(baseType));
		DataObjectTypeRegistry.addDataObjectType(factory);
		DataObjectTypeRegistry.registerMime(baseType,factory.getName());
	}
	@Override
	protected URLConnection openConnection(URL u) throws IOException{
		return new ApplicationURLConnection(u);
	}
	private class ApplicationURLConnection extends URLConnection{
		public ApplicationURLConnection(URL url){
			super(url);
		}
		@Override
		public void connect() throws IOException{

		}
		@Override
		public String getContentType(){
			return super.getContentType();
		}

	}
}
