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
package cc.fooledit.editor.image;
import cc.fooledit.*;
import cc.fooledit.control.*;
import cc.fooledit.core.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.effect.*;
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LayerToolBox implements ToolBox{
	public static final LayerToolBox INSTANCE=new LayerToolBox();
	private LayerToolBox(){

	}
	@Override
	public String getName(){
		return "LAYER";
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("LAYER",ImageEditorModule.NAME);
	}
	@Override
	public Node createInstance(){
		return createInstance((GraphicsObject)Main.INSTANCE.getCurrentData());
	}
	Node createInstance(GraphicsObject object){
		TableView<Node> layers=new TableView<>();
		layers.getItems().setAll(object.getLayers());
		ListChangeListener<Node> layersChanged=(c)->{
			layers.getItems().setAll(c.getList());
		};
		object.getLayers().addListener(layersChanged);
		layers.getColumns().addAll(getVisibleColumn(),getOpacityColumn(),getBlendModeColumn());
		return layers;
	}
	private TableColumn<Node,BlendMode> getBlendModeColumn(){
		TableColumn<Node,BlendMode> column=new TableColumn<>(/*MessageRegistry.getString(*/"BLEND_MODE"/*,ImageEditorModule.NAME)*/);
		column.setCellValueFactory((TableColumn.CellDataFeatures<Node,BlendMode> param)->{
			return param.getValue().blendModeProperty();
		});
		column.setEditable(true);
		return column;
	}
	private TableColumn<Node,Number> getOpacityColumn(){
		TableColumn<Node,Number> column=new TableColumn<>(/*MessageRegistry.getString(*/"OPACITY"/*,ImageEditorModule.NAME)*/);
		column.setCellValueFactory((TableColumn.CellDataFeatures<Node,Number> param)->{
			return param.getValue().opacityProperty();
		});
		column.setEditable(true);
		return column;
	}
	private TableColumn<Node,Boolean> getVisibleColumn(){
		TableColumn<Node,Boolean> column=new TableColumn<>(/*MessageRegistry.getString(*/"VISIBLE"/*,ImageEditorModule.NAME)*/);
		column.setCellValueFactory((TableColumn.CellDataFeatures<Node,Boolean> param)->{
			return param.getValue().visibleProperty();
		});
		column.setCellFactory(new Callback<TableColumn<Node,Boolean>,TableCell<Node,Boolean>>() {
			@Override
			public TableCell<Node,Boolean> call(TableColumn<Node,Boolean> param){
				return new CheckBoxTableCell();
			}
		});
		column.setEditable(true);
		return column;
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public SideBar.Side[] getPerferedSides(){
		return new SideBar.Side[]{SideBar.Side.LEFT,SideBar.Side.LEFT};
	}
}