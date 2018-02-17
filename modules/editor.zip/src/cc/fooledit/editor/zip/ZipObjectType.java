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
package cc.fooledit.editor.zip;
import cc.fooledit.core.DataObjectRegistry;
import cc.fooledit.core.DataObject;
import cc.fooledit.core.FoolURLConnection;
import cc.fooledit.core.DataObjectType;
import cc.fooledit.spi.*;
import java.net.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ZipObjectType implements DataObjectType<ZipObject>{
	public static final ZipObjectType INSTANCE=new ZipObjectType();
	private ZipObjectType(){
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
	public boolean canCreate(){
		return true;
	}
	@Override
	public ZipObject create(){
		return new ZipObject(null);
	}
	@Override
	public ZipObject readFrom(URLConnection connection,RegistryNode<String,Object,String> meta) throws Exception{
		return new ZipObject((DataObject)DataObjectRegistry.readFrom(toURL(connection)).getChild(DataObject.DATA));
	}
	@Override
	public void writeTo(ZipObject data,URLConnection connection,RegistryNode<String,Object,String> meta) throws Exception{
		data.getContent().getDataObjectType().writeTo(data,FoolURLConnection.open(toURL(connection)),meta);
	}
	private URL toURL(URLConnection connection) throws MalformedURLException{
		return new URL("compressed","",connection.getURL().toString());
	}
	@Override
	public String getDisplayName(){
		return "zip";
	}
}
