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
import javax.mail.*;
import javax.mail.internet.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class MessageObjectType implements DataObjectType<MessageObject>{
	public static final MessageObjectType INSTANCE=new MessageObjectType();
	private MessageObjectType(){
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
	public MessageObject create(){
		return new MessageObject(new MimeMessage((Session)null));
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("MESSAGE",EmailModule.NAME);
	}
	@Override
	public void writeTo(MessageObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		data.getMessage().writeTo(connection.getOutputStream());
	}
	@Override
	public MessageObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		return new MessageObject(new MimeMessage(null,connection.getInputStream()));
	}
}
