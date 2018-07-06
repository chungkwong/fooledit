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
package cc.fooledit.editor.msoffice.word;
import cc.fooledit.core.*;
import cc.fooledit.editor.msoffice.MsOfficeModule;
import cc.fooledit.spi.*;
import java.net.*;
import org.apache.poi.hwpf.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DocObjectType implements DataObjectType<DocObject>{
	public static DocObjectType INSTANCE=new DocObjectType();
	private DocObjectType(){
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
	public DocObject create(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("OLD_WORD_DOCUMENT",MsOfficeModule.NAME);
	}
	@Override
	public void writeTo(DocObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		data.getDocument().write(connection.getOutputStream());
	}
	@Override
	public DocObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		return new DocObject(new HWPFDocument(connection.getInputStream()));
	}
}