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
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GraphicsObject implements DataObject<GraphicsObject>{
	private final StackPane root;
	private final Property<Node> currentLayer=new SimpleObjectProperty<>();
	private final Property<Rectangle2D> viewport=new SimpleObjectProperty<>();
	public GraphicsObject(StackPane root){
		this.root=root;
		currentLayer.setValue(root);
	}
	@Override
	public DataObjectType<GraphicsObject> getDataObjectType(){
		return GraphicsObjectType.INSTANCE;
	}
	public StackPane getRoot(){
		return root;
	}
	public Property<Rectangle2D> viewportProperty(){
		return viewport;
	}
	public Property<Node> currentLayerProperty(){
		return currentLayer;
	}
	public static Canvas imageToCanvas(Image image){
		Canvas canvas=new Canvas(image.getWidth(),image.getWidth());
		canvas.getGraphicsContext2D().drawImage(image,0,0);
		return canvas;
	}
}
