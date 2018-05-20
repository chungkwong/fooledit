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
package cc.fooledit.editor.chm;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.io.*;
import java.net.*;
import org.jchmlib.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ChmObjectType implements DataObjectType<ChmObject>{
	public static final ChmObjectType INSTANCE=new ChmObjectType();
	private ChmObjectType(){
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
	public boolean canCreate(){
		return false;
	}
	@Override
	public ChmObject create(){
		throw new UnsupportedOperationException();
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("CHM_DOCUMENT",ChmModule.NAME);
	}
	@Override
	public void writeTo(ChmObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		throw new UnsupportedOperationException();
	}
	@Override
	public ChmObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		File file;
		if("file".equals(connection.getURL().getProtocol())){
			file=new File(connection.getURL().toURI());
		}else{
			file=File.createTempFile("cache",".chm");
			file.deleteOnExit();
			byte[] buf=new byte[4096];
			try(OutputStream out=new FileOutputStream(file);InputStream in=connection.getInputStream()){
				int c;
				while((c=in.read(buf))!=-1)
					out.write(buf,0,c);
			}
		}
		return new ChmObject(new ChmFile(file.toString()));
	}
}
