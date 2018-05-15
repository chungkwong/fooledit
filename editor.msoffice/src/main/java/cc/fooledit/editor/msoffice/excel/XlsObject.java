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
package cc.fooledit.editor.msoffice.excel;
import cc.fooledit.core.*;
import org.apache.poi.hssf.usermodel.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class XlsObject implements DataObject<XlsObject>{
	private final HSSFWorkbook document;
	public XlsObject(HSSFWorkbook document){
		this.document=document;

	}
	@Override
	public DataObjectType<XlsObject> getDataObjectType(){
		return XlsObjectType.INSTANCE;
	}
	public HSSFWorkbook getDocument(){
		return document;
	}
}