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
import java.util.stream.*;
import javafx.collections.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class EffectToolBox implements ToolBox{
	public static final EffectToolBox INSTANCE=new EffectToolBox();
	private EffectToolBox(){

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
		ChoiceBox<EffectTool> choiceBox=new ChoiceBox<>(FXCollections.observableArrayList(
				ImageEditorModule.EFFECT_REGISTRY.getChildNames().stream().
				map((name)->ImageEditorModule.EFFECT_REGISTRY.getChild(name)).collect(Collectors.toList())));
		choiceBox.getSelectionModel().select(ImageEditorModule.EFFECT_REGISTRY.getChild("IMAGE_INPUT"));
		FlowPane option=new FlowPane(choiceBox);
		ListViewWrapper<Effect> listView=new ListViewWrapper<>(()->choiceBox.getSelectionModel().getSelectedItem().getEffect(null));
		choiceBox.getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
			listView.getListView().getItems().set(listView.getListView().getSelectionModel().getSelectedIndex(),
					choiceBox.getSelectionModel().getSelectedItem().getEffect(null));

		});
		listView.getListView().getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
			if(n!=null){

			}
		});
		return new BorderPane(listView,null,option,null,null);
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
