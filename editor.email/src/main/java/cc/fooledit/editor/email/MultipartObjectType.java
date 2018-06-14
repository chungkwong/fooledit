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
package cc.fooledit.editor.email;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.net.*;
import javax.activation.*;
import javax.mail.internet.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class MultipartObjectType implements DataObjectType<MultipartObject>{
	public static final MultipartObjectType INSTANCE=new MultipartObjectType();
	private MultipartObjectType(){
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
	public MultipartObject create(){
		return new MultipartObject(new MimeMultipart());
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("MULTIPART",EmailModule.NAME);
	}
	@Override
	public void writeTo(MultipartObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		data.getObject().writeTo(connection.getOutputStream());
	}
	@Override
	public MultipartObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		return new MultipartObject(new MimeMultipart(new URLDataSource(connection.getURL())));
	}
}
