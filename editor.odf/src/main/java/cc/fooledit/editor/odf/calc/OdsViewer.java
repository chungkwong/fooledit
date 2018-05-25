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
package cc.fooledit.editor.odf.calc;
import java.util.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.util.*;
import org.odftoolkit.simple.*;
import org.odftoolkit.simple.table.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class OdsViewer extends TabPane{
	private final SpreadsheetDocument workbook;
	public OdsViewer(SpreadsheetDocument workbook){
		this.workbook=workbook;
		int numberOfSheets=workbook.getSheetCount();
		for(int i=0;i<numberOfSheets;i++){
			Table sheet=workbook.getSheetByIndex(i);
			getTabs().add(new Tab(sheet.getTableName(),getSheetViewer(sheet)));
		}
		setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
	}
	private Node getSheetViewer(Table sheet){
		TableView<Row> table=new TableView<>();
		int rowCount=sheet.getRowCount();
		int columnCount=sheet.getColumnCount();
		List<Row> rows=sheet.getRowList();
		table.getItems().setAll(rows);
		TableColumn<Row,Integer> rowNumber=new TableColumn<>();
		rowNumber.setCellValueFactory((TableColumn.CellDataFeatures<Row,Integer> param)->new ReadOnlyObjectWrapper<Integer>(Optional.ofNullable(param.getValue()).map((row)->row.getRowIndex()).orElse(null)));
		rowNumber.setEditable(false);
		table.getColumns().add(rowNumber);
		for(int j=0;j<rowCount;j++){
			TableColumn<Row,org.odftoolkit.simple.table.Cell> column=new TableColumn<>(getColumnName(j));
			column.setPrefWidth(sheet.getColumnByIndex(j).getWidth());
			int k=j;
			column.setCellValueFactory((TableColumn.CellDataFeatures<Row,org.odftoolkit.simple.table.Cell> param)->new ReadOnlyObjectWrapper<>(Optional.ofNullable(param.getValue()).map((row)->row.getCellByIndex(k)).orElse(null)));
			column.setCellFactory(new Callback<TableColumn<Row,org.odftoolkit.simple.table.Cell>,TableCell<Row,org.odftoolkit.simple.table.Cell>>() {
				@Override
				public TableCell<Row,org.odftoolkit.simple.table.Cell> call(TableColumn<Row,org.odftoolkit.simple.table.Cell> param){
					return new OdsCell();
				}
			});
			table.getColumns().add(column);
		}
		return table;
	}
	private String getColumnName(int i){
		String buf=Character.toString((char)(i%26+'A'));
		while((i/=26)>0){
			buf=(char)(i%26+'A')+buf;
		}
		return buf;
	}
	private static class OdsCell extends TableCell<Row,org.odftoolkit.simple.table.Cell>{
		public OdsCell(){

		}
		@Override
		protected void updateItem(org.odftoolkit.simple.table.Cell item,boolean empty){
			if(!empty&&item!=null){
				setText(item.getDisplayText());
			}else{
				setText("");
			}
		}
	}
}
