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
package cc.fooledit.editor.browser;
import cc.fooledit.core.DataObjectType;
import cc.fooledit.spi.*;
import java.net.*;
import javax.activation.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BrowserObjectType implements DataObjectType<BrowserObject>{
	public static final BrowserObjectType INSTANCE=new BrowserObjectType();
	private static final String MIME="text/html";
	private BrowserObjectType(){
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
	public void writeTo(BrowserObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public BrowserObject create(){
		return new BrowserObject();
	}
	@Override
	public boolean canCreate(){
		return true;
	}
	@Override
	public BrowserObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		return create();
	}
	@Override
	public BrowserObject readFrom(URLConnection connection,MimeType mime,RegistryNode<String,Object> meta) throws Exception{
		return create();
	}
	@Override
	public String getDisplayName(){
		return "browser";
	}
}
