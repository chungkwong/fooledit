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
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GraphicsViewer extends StackPane{
	private final Property<GraphicsObject> graphics=new SimpleObjectProperty<>();
	public GraphicsViewer(GraphicsObject object){
		getChildren().setAll(object.getLayers());
		ListChangeListener<Node> layersChanged=(c)->{
			getChildren().setAll(c.getList());
		};
		graphics.addListener((e,o,n)->{
			if(o!=null)
				o.getLayers().removeListener(layersChanged);
			if(n!=null){
				n.getLayers().addListener(layersChanged);
			}
		});
		graphics.setValue(object);
	}
	public ObservableValue<GraphicsObject> graphicsProperty(){
		return graphics;
	}
}
