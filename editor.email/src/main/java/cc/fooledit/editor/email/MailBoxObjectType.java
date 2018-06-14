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
package cc.fooledit.editor.email;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.net.*;
import java.util.*;
import javax.activation.*;
import javax.mail.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MailBoxObjectType implements DataObjectType<MailBoxObject>{
	public static final MailBoxObjectType INSTANCE=new MailBoxObjectType();
	private static final String MIME="text/html";
	private MailBoxObjectType(){
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
	public void writeTo(MailBoxObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public MailBoxObject create(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean canCreate(){
		return false;
	}
	@Override
	public MailBoxObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		Properties properties=new Properties();
		return new MailBoxObject(Session.getDefaultInstance(properties));
	}
	@Override
	public MailBoxObject readFrom(URLConnection connection,MimeType mime,RegistryNode<String,Object> meta) throws Exception{
		return create();
	}
	@Override
	public String getDisplayName(){
		return "email";
	}
}
