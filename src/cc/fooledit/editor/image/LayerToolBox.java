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
import cc.fooledit.control.*;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.util.stream.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.util.converter.*;
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
		return MessageRegistry.getString("LAYER",ImageModule.NAME);
	}
	@Override
	public Node createInstance(Node viewer,Object remark,RegistryNode<String,Object> meta){
		return createInstance((GraphicsObject)meta.get(DataObject.DATA));
	}
	Node createInstance(GraphicsObject object){
		TreeTableView<Node> layers=new TreeTableView<>();
		layers.setEditable(true);
		layers.setSortPolicy((c)->false);
		layers.setShowRoot(true);
		layers.setRoot(getTreeItem(object.getRoot()));
		layers.getSelectionModel().select(layers.getRoot());
		layers.getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
			object.currentLayerProperty().setValue(n!=null?n.getValue():null);
		});
		layers.getColumns().addAll(getVisibleColumn(),getOpacityColumn(),getBlendModeColumn());
		Button add=new Button("+");
		add.setOnAction((e)->{
			TreeItem<Node> selectedItem=layers.getSelectionModel().getSelectedItem();
			if(selectedItem.getParent()!=null){
				((StackPane)selectedItem.getParent().getValue()).getChildren().add(getIndexOfParent(selectedItem)+1,new Canvas());
				((LazyTreeItem)selectedItem.getParent()).refresh();
			}else{
				((StackPane)selectedItem.getValue()).getChildren().add(new Canvas());
				((LazyTreeItem)selectedItem).refresh();
			}
		});
		Button delete=new Button("-");
		delete.setOnAction((e)->{
			TreeItem<Node> selectedItem=layers.getSelectionModel().getSelectedItem();
			if(selectedItem.getParent()!=null){
				((StackPane)selectedItem.getParent().getValue()).getChildren().remove(selectedItem.getValue());
				((LazyTreeItem)selectedItem.getParent()).refresh();
			}
		});
		Button up=new Button("/\\");
		up.setOnAction((e)->{
			TreeItem<Node> selectedItem=layers.getSelectionModel().getSelectedItem();
			if(selectedItem.getParent()!=null){
				int i=getIndexOfParent(selectedItem);
				if(i>0){
					((StackPane)selectedItem.getParent().getValue()).getChildren().remove(i);
					((StackPane)selectedItem.getParent().getValue()).getChildren().add(i-1,selectedItem.getValue());
					((LazyTreeItem)selectedItem.getParent()).refresh();
				}
			}
		});
		Button down=new Button("\\/");
		down.setOnAction((e)->{
			TreeItem<Node> selectedItem=layers.getSelectionModel().getSelectedItem();
			if(selectedItem.getParent()!=null){
				int i=getIndexOfParent(selectedItem);
				if(i+1<selectedItem.getParent().getChildren().size()){
					((StackPane)selectedItem.getParent().getValue()).getChildren().remove(i);
					((StackPane)selectedItem.getParent().getValue()).getChildren().add(i+1,selectedItem.getValue());
					((LazyTreeItem)selectedItem.getParent()).refresh();
				}
			}
		});
		Button indent=new Button(">");
		indent.setOnAction((e)->{
			TreeItem<Node> selectedItem=layers.getSelectionModel().getSelectedItem();
			if(selectedItem.getParent()!=null){
				int i=getIndexOfParent(selectedItem);
				((StackPane)selectedItem.getParent().getValue()).getChildren().remove(i);
				if(i>0&&selectedItem.getParent().getChildren().get(i-1)instanceof LazyTreeItem){
					((StackPane)selectedItem.getParent().getChildren().get(i-1).getValue()).getChildren().add(selectedItem.getValue());
				}else{
					((StackPane)selectedItem.getParent().getValue()).getChildren().add(i,new StackPane(selectedItem.getValue()));
				}
				((LazyTreeItem)selectedItem.getParent()).refresh();
			}
		});
		Button unindent=new Button("<");
		down.setOnAction((e)->{
			TreeItem<Node> selectedItem=layers.getSelectionModel().getSelectedItem();
			if(selectedItem.getParent()!=null&&selectedItem.getParent().getParent()!=null){
				int i=getIndexOfParent(selectedItem);
				int j=getIndexOfParent(selectedItem.getParent());
				((StackPane)selectedItem.getParent().getValue()).getChildren().remove(selectedItem.getValue());
				if(i<selectedItem.getParent().getChildren().size()/2){
					((StackPane)selectedItem.getParent().getParent().getValue()).getChildren().add(j,selectedItem.getValue());
				}else{
					((StackPane)selectedItem.getParent().getParent().getValue()).getChildren().add(j+1,selectedItem.getValue());
				}
				((LazyTreeItem)selectedItem.getParent().getParent()).refresh();
			}
		});
		ToolBar bar=new ToolBar(add,delete,up,down,unindent,indent);
		return new BorderPane(layers,null,null,bar,null);
	}
	private TreeItem getTreeItem(Node layer){
		if(layer instanceof StackPane){
			return new LazyTreeItem(layer,()->((StackPane)layer).getChildren().stream().map((l)->getTreeItem(l)).collect(Collectors.toList()));
		}else{
			return new TreeItem(layer);
		}
	}
	private int getIndexOfParent(TreeItem<?> item){
		if(item.getParent()!=null)
			return item.getParent().getChildren().indexOf(item);
		else
			return -1;
	}
	private TreeTableColumn<Node,BlendMode> getBlendModeColumn(){
		TreeTableColumn<Node,BlendMode> column=new TreeTableColumn<>(/*MessageRegistry.getString(*/"BLEND_MODE"/*,ImageEditorModule.NAME)*/);
		column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Node,BlendMode> param)->{
			return param.getValue().getValue().blendModeProperty();
		});
		ObservableList<BlendMode> options=FXCollections.<BlendMode>observableArrayList(BlendMode.values());
		options.add(null);
		column.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(options));
		column.setEditable(true);
		return column;
	}
	private TreeTableColumn<Node,Number> getOpacityColumn(){
		TreeTableColumn<Node,Number> column=new TreeTableColumn<>(/*MessageRegistry.getString(*/"OPACITY"/*,ImageEditorModule.NAME)*/);
		column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Node,Number> param)->{
			return param.getValue().getValue().opacityProperty();
		});
		column.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn(new NumberStringConverter()));
		column.setEditable(true);
		return column;
	}
	private TreeTableColumn<Node,Boolean> getVisibleColumn(){
		TreeTableColumn<Node,Boolean> column=new TreeTableColumn<>(/*MessageRegistry.getString(*/"VISIBLE"/*,ImageEditorModule.NAME)*/);
		column.setCellValueFactory((TreeTableColumn.CellDataFeatures<Node,Boolean> param)->{
			return param.getValue().getValue().visibleProperty();
		});
		column.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(column));
		column.setEditable(true);
		return column;
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public Side[] getPerferedSides(){
		return new Side[]{Side.LEFT,Side.RIGHT};
	}
}