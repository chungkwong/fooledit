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
package cc.fooledit.example.image;
import cc.fooledit.model.*;
import java.io.*;
import java.net.*;
import javafx.embed.swing.*;
import javafx.scene.image.*;
import javax.imageio.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ImageObjectType implements DataObjectType<ImageObject>{
	public static final ImageObjectType INSTANCE=new ImageObjectType();
	private ImageObjectType(){
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
	public void writeTo(ImageObject data,URLConnection connection) throws Exception{
		try(OutputStream out=connection.getOutputStream()){
			writeTo(data,out);
		}
	}
	public void writeTo(ImageObject data,OutputStream out) throws Exception{
		WritableImage image=new WritableImage((int)data.getCanvas().getWidth(),(int)data.getCanvas().getHeight());
		data.getCanvas().snapshot(null,image);
		ImageIO.write(SwingFXUtils.fromFXImage(image,null),"png",out);
	}
	@Override
	public ImageObject readFrom(URLConnection connection) throws Exception{
		try(InputStream in=connection.getInputStream()){
			return readFrom(in);
		}
	}
	public ImageObject readFrom(InputStream in) throws Exception{
		return new ImageObject(new Image(in));
	}
	@Override
	public boolean canCreate(){
		return true;
	}
	@Override
	public ImageObject create(){
		return new ImageObject(new WritableImage(200,200));
	}
	@Override
	public String getName(){
		return "image";
	}
}
