/*
 * Copyright (C) 2018 Chan Chung Kwong
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
package cc.fooledit.editor.media;
import java.io.*;
import java.net.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class NaiveStreamHandler extends URLStreamHandler{
	@Override
	protected URLConnection openConnection(URL url) throws IOException{
		return new NaiveConnection(url);
	}
}
class NaiveConnection extends URLConnection{
	NaiveConnection(URL url){
		super(url);
	}
	@Override
	public void connect() throws IOException{
		
	}
	@Override
	public String getContentType(){
		return "video/unknown";
	}
}
