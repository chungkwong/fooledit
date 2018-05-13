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
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TransformToolBox implements ToolBox{
	public static final TransformToolBox INSTANCE=new TransformToolBox();
	private TransformToolBox(){

	}
	@Override
	public String getName(){
		return "TRANSFORM";
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("TRANSFORM",ImageModule.NAME);
	}
	@Override
	public Node createInstance(Node viewer,Object remark,RegistryNode<String,Object> meta){
		return createInstance((GraphicsObject)meta.get(DataObject.DATA));
	}
	Node createInstance(GraphicsObject object){
		Spinner<Double> scaleChooser=new Spinner<>(0.0,4.0,1.0);
		scaleChooser.valueProperty().addListener((e,o,n)->{
			Node layer=object.currentLayerProperty().getValue();
			layer.setScaleX(n);
			layer.setScaleY(n);
		});
		Spinner<Double> rotateChooser=new Spinner<>(-180.0,180.0,0.0);
		rotateChooser.valueProperty().addListener((e,o,n)->{
			Node layer=object.currentLayerProperty().getValue();
			layer.setRotate(n);
		});
		return new HBox(scaleChooser,rotateChooser);
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
