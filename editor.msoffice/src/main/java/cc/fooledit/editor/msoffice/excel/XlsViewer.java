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
import javafx.scene.*;
import javafx.scene.control.*;
import org.apache.poi.hssf.usermodel.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class XlsViewer extends TabPane{
	private final HSSFWorkbook workbook;
	public XlsViewer(HSSFWorkbook workbook){
		this.workbook=workbook;
		int numberOfSheets=workbook.getNumberOfSheets();
		for(int i=0;i<numberOfSheets;i++){
			getTabs().add(new Tab(workbook.getSheetName(i),getSheetViewer(workbook.getSheetAt(i))));
		}
	}
	private Node getSheetViewer(HSSFSheet sheet){
		TableView<HSSFRow> table=new TableView<>();
		HSSFRow[] rows=new HSSFRow[sheet.getPhysicalNumberOfRows()];
		for(int i=0;i<rows.length;i++)
			rows[i]=sheet.getRow(i);
		table.getItems().setAll(rows);
		int[] columnBreaks=sheet.getColumnBreaks();
		for(int j=0;j<columnBreaks.length-1;j++){
			TableColumn<HSSFRow,String> column=new TableColumn<>();
			
			table.getColumns().add(column);
		}
		return table;
	}

}
