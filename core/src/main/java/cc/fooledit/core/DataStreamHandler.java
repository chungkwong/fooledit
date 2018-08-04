/*
 * Copyright (C) 2018 Chan Chung Kwong <1m02math@126.com>
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
import java.io.*;
import java.net.*;
import java.util.*;
import org.osgi.service.url.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DataStreamHandler extends AbstractURLStreamHandlerService{
	@Override
	public URLConnection openConnection(URL u) throws IOException{
		return new DataURLConnection(u);
	}
	private class DataURLConnection extends URLConnection{
		private final String mime;
		private final byte[] data;
		private final int length;
		private static final String DEFAULT_MIME="text/plain;charset=US-ASCII";
		public DataURLConnection(URL url){
			super(url);
			String str=url.toString();
			int dataStart=str.indexOf(',');
			boolean base64=str.substring(0,dataStart).endsWith(";base64");
			if(base64){
				mime=dataStart-7==5?DEFAULT_MIME:str.substring(5,dataStart-7);
				data=Base64.getMimeDecoder().decode(str.substring(dataStart+1));
				length=data.length;
			}else{
				mime=dataStart==5?DEFAULT_MIME:str.substring(5,dataStart);
				data=new byte[str.length()-dataStart-1];
				int count=0;
				for(int i=dataStart+1, len=str.length();i<len;++count){
					char c=str.charAt(i);
					if(c=='%'){
						data[count]=Byte.parseByte(str.substring(i+1,i+3),16);
						i+=3;
					}else{
						data[count]=(byte)c;
						++i;
					}
				}
				length=count;
			}
		}
		@Override
		public void connect() throws IOException{
		}
		@Override
		public String getContentType(){
			return mime;
		}
		@Override
		public long getContentLengthLong(){
			return length;
		}
		@Override
		public int getContentLength(){
			return length;
		}
		@Override
		public InputStream getInputStream() throws IOException{
			return new ByteArrayInputStream(data,0,length);
		}
	}
}
