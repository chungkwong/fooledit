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
import java.util.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.util.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class XlsViewer extends TabPane{
	private final Workbook workbook;
	public XlsViewer(Workbook workbook){
		this.workbook=workbook;
		int numberOfSheets=workbook.getNumberOfSheets();
		for(int i=0;i<numberOfSheets;i++){
			getTabs().add(new Tab(workbook.getSheetName(i),getSheetViewer(workbook.getSheetAt(i))));
		}
		setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
	}
	private Node getSheetViewer(Sheet sheet){
		TableView<Row> table=new TableView<>();
		int firstRow=sheet.getFirstRowNum();
		int lastRow=sheet.getLastRowNum();
		Row[] rows=new Row[lastRow-firstRow+1];
		for(int i=firstRow;i<=lastRow;i++)
			rows[i]=sheet.getRow(i);
		table.getItems().setAll(rows);
		TableColumn<Row,Integer> rowNumber=new TableColumn<>();
		rowNumber.setCellValueFactory((TableColumn.CellDataFeatures<Row,Integer> param)->new ReadOnlyObjectWrapper<Integer>(Optional.ofNullable(param.getValue()).map((row)->row.getRowNum()).orElse(null)));
		rowNumber.setEditable(false);
		table.getColumns().add(rowNumber);
		Pair<Short,Short> columnRange=getColumnRange(sheet);
		short firstColumn=columnRange.getKey();
		short lastColumn=columnRange.getValue();
		for(short j=firstColumn;j<=lastColumn;j++){
			TableColumn<Row,Cell> column=new TableColumn<>(getColumnName(j));
			column.setPrefWidth(sheet.getColumnWidthInPixels(j));
			int k=j;
			column.setCellValueFactory((TableColumn.CellDataFeatures<Row,Cell> param)->new ReadOnlyObjectWrapper<>(Optional.ofNullable(param.getValue()).map((row)->row.getCell(k)).orElse(null)));
			column.setCellFactory(new Callback<TableColumn<Row,Cell>,TableCell<Row,Cell>>() {
				@Override
				public TableCell<Row,Cell> call(TableColumn<Row,Cell> param){
					return new XlsCell();
				}
			});
			table.getColumns().add(column);
		}
		return table;
	}
	private Pair<Short,Short> getColumnRange(Sheet sheet){
		short first=Short.MAX_VALUE;
		short last=0;
		for(Row row:sheet){
			short lastCellNum=row.getLastCellNum();
			if(lastCellNum>last)
				last=lastCellNum;
			short firstCellNum=row.getFirstCellNum();
			if(firstCellNum<first)
				first=firstCellNum;
		}
		if(first>last){
			first=last;
		}
		return new Pair<>(first,last);
	}
	private String getColumnName(int i){
		String buf=Character.toString((char)(i%26+'A'));
		while((i/=26)>0){
			buf=(char)(i%26+'A')+buf;
		}
		return buf;
	}
	private static class XlsCell extends TableCell<Row,Cell>{
		public XlsCell(){

		}
		@Override
		protected void updateItem(Cell item,boolean empty){
			if(!empty&&item!=null){
				CellType type=item.getCellTypeEnum();
				if(type==CellType.FORMULA){
					if(isEditing()){
						setText(item.getCellFormula());
					}else{
						renderByType(item,item.getCachedFormulaResultTypeEnum());
					}
				}else{
					renderByType(item,type);
				}
			}else{
				setText("");
			}
		}
		private void renderByType(Cell item,CellType type){
			switch(type){
				case BOOLEAN:
					setText(Boolean.toString(item.getBooleanCellValue()));
					break;
				case ERROR:
					setText(Byte.toString(item.getErrorCellValue()));
					break;
				case NUMERIC:
					setText(Double.toString(item.getNumericCellValue()));
					break;
				case STRING:
					setText(item.getStringCellValue());
					break;
				case _NONE:
				case BLANK:
				default:
					setText("");
					break;
			}
		}
	}
}
