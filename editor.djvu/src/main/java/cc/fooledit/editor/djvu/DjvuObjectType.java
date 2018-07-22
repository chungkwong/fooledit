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
package cc.fooledit.editor.djvu;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import com.lizardtech.djvu.*;
import java.net.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DjvuObjectType implements DataObjectType<DjvuObject>{
	public static final DjvuObjectType INSTANCE=new DjvuObjectType();
	private DjvuObjectType(){
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
	public DjvuObject create(){
		throw new UnsupportedOperationException();
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("DJVU_DOCUMENT",Activator.NAME);
	}
	@Override
	public void writeTo(DjvuObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		throw new UnsupportedOperationException();
	}
	@Override
	public DjvuObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		Document document=new Document();
		document.read(connection.getInputStream());
		return new DjvuObject(document);
	}
}
