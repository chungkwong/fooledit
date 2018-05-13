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
package cc.fooledit.editor.filesystem;
import cc.fooledit.core.DataObjectType;
import cc.fooledit.spi.*;
import java.net.*;
import javax.activation.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FileSystemObjectType implements DataObjectType<FileSystemObject>{
	public static final FileSystemObjectType INSTANCE=new FileSystemObjectType();
	private FileSystemObjectType(){

	}
	@Override
	public boolean canRead(){
		return false;
	}
	@Override
	public boolean canWrite(){
		return false;
	}
	@Override
	public boolean canCreate(){
		return true;
	}
	@Override
	public void writeTo(FileSystemObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public FileSystemObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		return create();
	}
	@Override
	public FileSystemObject readFrom(URLConnection connection,MimeType mime,RegistryNode<String,Object> meta) throws Exception{
		return create();
	}
	@Override
	public FileSystemObject create(){
		return new FileSystemObject(null);
	}
	@Override
	public String getDisplayName(){
		return "filesystem";
	}
}
