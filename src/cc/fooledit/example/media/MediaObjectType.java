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
package cc.fooledit.example.media;
import cc.fooledit.model.DataObjectType;
import java.io.*;
import javafx.scene.media.*;
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
	public void writeTo(MediaObject data,OutputStream out) throws Exception{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public MediaObject readFrom(InputStream in) throws Exception{
		File tmp=File.createTempFile("jtk","");
		try(OutputStream out=new FileOutputStream(tmp)){
			byte[] buf=new byte[4096];
			int c;
			while((c=in.read(buf))!=-1){
				out.write(buf,0,c);
			}
		}
		MediaObject data=new MediaObject(new MediaPlayer(new Media(tmp.toURI().toString())));
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
}
