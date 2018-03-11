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
import org.apache.pdfbox.pdmodel.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PdfObjectType implements DataObjectType<PdfObject>{
	public static final PdfObjectType INSTANCE=new PdfObjectType();
	private PdfObjectType(){
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
	public PdfObject create(){
		return new PdfObject(new PDDocument());
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("PDF_DOCUMENT",PdfModule.NAME);
	}
	@Override
	public void writeTo(PdfObject data,URLConnection connection,RegistryNode<String,Object,String> meta) throws Exception{
		data.getDocument().save(connection.getOutputStream());
	}
	@Override
	public PdfObject readFrom(URLConnection connection,RegistryNode<String,Object,String> meta) throws Exception{
		return new PdfObject(PDDocument.load(connection.getInputStream()));
	}
}
