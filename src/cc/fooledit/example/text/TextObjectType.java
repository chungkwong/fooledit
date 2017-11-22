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
package cc.fooledit.example.text;
import cc.fooledit.model.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.logging.*;
import java.util.stream.*;
import javax.activation.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TextObjectType implements DataObjectType<TextObject>{
	public static final TextObjectType INSTANCE=new TextObjectType();
	private static final String CHARSET="CHAESET";
	private TextObjectType(){

	}
	@Override
	public boolean canRead(){
		return true;
	}
	@Override
	public boolean canWrite(){
		return true;
	}
	@Override
	public void writeTo(TextObject data,URLConnection connection) throws Exception{
		try(OutputStream out=connection.getOutputStream()){
			writeTo(data,out);
		}
	}
	public void writeTo(TextObject data,OutputStream out) throws Exception{
		Charset charset;
		String charsetName=data.getProperties().get(CHARSET);
		try{
			charset=Charset.forName(charsetName);
		}catch(IllegalArgumentException ex){
			Logger.getGlobal().log(Level.INFO,"Unsupported character set:{0}",charsetName);
			charset=StandardCharsets.UTF_8;
		}
		BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(out,charset));
		writer.write(data.getText().get());
		writer.flush();
	}
	@Override
	public TextObject readFrom(URLConnection connection) throws Exception{
		try(InputStream in=connection.getInputStream()){
			Charset charset=null;
			try{
				charset=checkForCharset(in,new MimeType(connection.getContentType()).getParameter("charset"));
			}catch(MimeTypeParseException ex){
				Logger.getGlobal().log(Level.INFO,null,ex);
			}
			if(charset==null)
				charset=checkForCharset(in,connection.getContentEncoding());
			if(charset==null)
				try{
					charset=CharsetDetector.probeCharset(in);
				}catch(IOException ex){
					charset=StandardCharsets.UTF_8;
				}
			return readFrom(in,charset);
		}
	}
	private static Charset checkForCharset(InputStream in,String charsetName){
		try{
			Charset charset=Charset.forName(charsetName);
			if(CharsetDetector.isPossible(in,charset))
				return charset;
		}catch(IllegalArgumentException ex){
			Logger.getGlobal().log(Level.INFO,"Unsupported character set:{0}",charsetName);
		}catch(IOException ex){

		}
		return null;
	}
	public TextObject readFrom(InputStream in,Charset charset) throws Exception{
		StringBuilder buf=new StringBuilder();
		BufferedReader reader=new BufferedReader(new InputStreamReader(in,charset));
		TextObject object=new TextObject(reader.lines().collect(Collectors.joining("\n")));
		object.getProperties().put(CHARSET,charset.name());
		return object;
	}
	@Override
	public boolean canCreate(){
		return true;
	}
	@Override
	public TextObject create(){
		return new TextObject("");
	}
	@Override
	public String getName(){
		return "text";
	}
}
