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
package cc.fooledit.editor.odf.impress;
import cc.fooledit.core.*;
import cc.fooledit.editor.odf.OdfModule;
import cc.fooledit.spi.*;
import java.net.*;
import org.odftoolkit.simple.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class OdpObjectType implements DataObjectType<OdpObject>{
	public static final OdpObjectType INSTANCE=new OdpObjectType();
	private OdpObjectType(){
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
		return false;
	}
	@Override
	public OdpObject create(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("PRESENTATION_DOCUMENT",OdfModule.NAME);
	}
	@Override
	public void writeTo(OdpObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		data.getDocument().save(connection.getOutputStream());
	}
	@Override
	public OdpObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		return new OdpObject(PresentationDocument.loadDocument(connection.getInputStream()));
	}
}
