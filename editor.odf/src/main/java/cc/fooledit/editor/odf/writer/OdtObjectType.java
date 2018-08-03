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
package cc.fooledit.editor.odf.writer;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.net.*;
import java.util.logging.*;
import org.odftoolkit.simple.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class OdtObjectType implements DataObjectType<OdtObject>{
	public static OdtObjectType INSTANCE=new OdtObjectType();
	private OdtObjectType(){
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
	public OdtObject create(){
		try{
			return new OdtObject(TextDocument.newTextDocument());
		}catch(Exception ex){
			Logger.getLogger(OdtObjectType.class.getName()).log(Level.SEVERE,null,ex);
			return null;
		}
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("WRITER_DOCUMENT",cc.fooledit.editor.odf.Activator.class);
	}
	@Override
	public void writeTo(OdtObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		data.getDocument().save(connection.getOutputStream());
	}
	@Override
	public OdtObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		return new OdtObject(TextDocument.loadDocument(connection.getInputStream()));
	}
}
