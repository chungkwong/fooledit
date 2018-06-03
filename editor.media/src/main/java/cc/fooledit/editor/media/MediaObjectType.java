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
package cc.fooledit.editor.media;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.io.*;
import java.net.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MediaObjectType implements DataObjectType<MediaObject>{
	public static final MediaObjectType INSTANCE=new MediaObjectType();
	private MediaObjectType(){
	}
	@Override
	public boolean canRead(){
		return true;
	}
	@Override
	public boolean canWrite(){
		return false;
	}
	@Override
	public MediaObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		return new MediaObject(connection.getURL().toString());
		/*try(InputStream in=connection.getInputStream()){
			return readFrom(in);
		}*/
	}
	public MediaObject readFrom(InputStream in) throws Exception{
		File tmp=File.createTempFile("fooledit","");
		try(OutputStream out=new FileOutputStream(tmp)){
			byte[] buf=new byte[4096];
			int c;
			while((c=in.read(buf))!=-1){
				out.write(buf,0,c);
			}
		}
		MediaObject data=new MediaObject(tmp.toURI().toString());
		tmp.delete();
		return data;
	}
	@Override
	public boolean canCreate(){
		return false;
	}
	@Override
	public MediaObject create(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String getDisplayName(){
		return "media";
	}
	@Override
	public void writeTo(MediaObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
