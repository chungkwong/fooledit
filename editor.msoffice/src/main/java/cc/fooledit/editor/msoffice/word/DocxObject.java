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
import org.apache.poi.xwpf.usermodel.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DocxObject implements DataObject<DocxObject>{
	private final XWPFDocument document;
	public DocxObject(XWPFDocument document){
		this.document=document;
	}
	@Override
	public DataObjectType<DocxObject> getDataObjectType(){
		return DocxObjectType.INSTANCE;
	}
	public XWPFDocument getDocument(){
		return document;
	}
}
