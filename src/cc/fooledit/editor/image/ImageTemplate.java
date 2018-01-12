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
package cc.fooledit.editor.image;
import cc.fooledit.*;
import cc.fooledit.core.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ImageTemplate implements Template<ImageObject>{
	private final String name;
	private final String description;
	private final String file;
	private final String mime;
	public ImageTemplate(String name,String description,String file,String mime){
		this.name=name;
		this.description=description;
		this.file=file;
		this.mime=mime;
	}
	@Override
	public String getName(){
		return name;
	}
	@Override
	public String getDescription(){
		return description;
	}
	public String getMimeType(){
		return mime;
	}
	@Override
	public String getModule(){
		return ImageEditorModule.NAME;
	}
	@Override
	public Collection<String> getParameters(){
		return Collections.emptySet();
	}
	@Override
	public ImageObject apply(Properties properties){
		try{
			return ImageObjectType.INSTANCE.readFrom(new FileInputStream(Main.INSTANCE.getFile(file,ImageEditorModule.NAME)));
		}catch(Exception ex){
			Logger.getLogger(ImageTemplate.class.getName()).log(Level.SEVERE,null,ex);
			return ImageObjectType.INSTANCE.create();
		}
	}
}
