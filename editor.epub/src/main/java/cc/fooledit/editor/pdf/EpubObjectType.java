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
package cc.fooledit.editor.pdf;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.net.*;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class EpubObjectType implements DataObjectType<EpubObject>{
	public static final EpubObjectType INSTANCE=new EpubObjectType();
	private EpubObjectType(){
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
	public EpubObject create(){
		return new EpubObject(new Book());
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("EPUB_DOCUMENT",Activator.NAME);
	}
	@Override
	public void writeTo(EpubObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		new Epub3Writer().write(data.getDocument(),connection.getOutputStream());
	}
	@Override
	public EpubObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		return new EpubObject(new EpubReader().readEpub(connection.getInputStream()));
	}
}
