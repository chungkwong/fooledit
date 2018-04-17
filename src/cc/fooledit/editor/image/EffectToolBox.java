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
import cc.fooledit.core.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
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
		return "EFFECT";
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("EFFECT",ImageModule.NAME);
	}
	@Override
	public Node createInstance(){
		return createInstance((GraphicsObject)Main.INSTANCE.getCurrentData());
	}
	Node createInstance(GraphicsObject object){
		ChoiceBox<EffectTool> choiceBox=new ChoiceBox<>(FXCollections.observableArrayList(ImageModule.EFFECT_REGISTRY.values()));
		choiceBox.getSelectionModel().select(ImageModule.EFFECT_REGISTRY.get("IMAGE_INPUT"));
		choiceBox.getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
			object.currentLayerProperty().getValue().setEffect(n.getEffect(null));
		});
		return choiceBox;
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public Side[] getPerferedSides(){
		return new Side[]{Side.TOP,Side.BOTTOM};
	}
}