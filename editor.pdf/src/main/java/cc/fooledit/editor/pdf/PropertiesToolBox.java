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
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import org.apache.pdfbox.pdmodel.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PropertiesToolBox implements ToolBox{
	public static final PropertiesToolBox INSTANCE=new PropertiesToolBox();
	private PropertiesToolBox(){
	}
	@Override
	public String getName(){
		return "PROPERTIES";
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("PROPERTIES",Activator.class);
	}
	@Override
	public Node createInstance(Node viewer,Object remark,RegistryNode<String,Object> meta){
		return new PropertiesViewer(((PdfObject)meta.get(DataObject.DATA)).getDocument());
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public Side[] getPerferedSides(){
		return new Side[]{Side.RIGHT};
	}
	static class PropertiesViewer extends TableView<String>{
		private final PDDocument document;
		public PropertiesViewer(PDDocument document){
			this.document=document;
			TableColumn<String,String> keys=new TableColumn<>();
			keys.setCellValueFactory((TableColumn.CellDataFeatures<String,String> param)->new ReadOnlyObjectWrapper<>(param.getValue()));
			TableColumn<String,String> values=new TableColumn<>();
			values.setCellValueFactory((TableColumn.CellDataFeatures<String,String> param)->new ReadOnlyObjectWrapper<>(document.getDocumentInformation().getCustomMetadataValue(param.getValue())));
			getColumns().addAll(keys,values);
			getItems().setAll(document.getDocumentInformation().getMetadataKeys());
		}

	}
}
