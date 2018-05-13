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
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GraphicsViewer extends StackPane{
	private final GraphicsObject object;
	private final Canvas backMatter=new Canvas();
	private final Canvas frontMatter=new Canvas();
	private final ObservableList<Number> verticalReferenceLines=FXCollections.observableArrayList();
	private final ObservableList<Number> horizontalReferenceLines=FXCollections.observableArrayList();
	public GraphicsViewer(GraphicsObject object){
		this.object=object;
		getChildren().setAll(backMatter,object.getRoot(),frontMatter);
		horizontalReferenceLines.addListener((ListChangeListener.Change<? extends Number> c)->updateReferenceLines());
		verticalReferenceLines.addListener((ListChangeListener.Change<? extends Number> c)->updateReferenceLines());
		setCursor(Cursor.CROSSHAIR);
		setFocusTraversable(true);
	}
	public Canvas getFrontMatter(){
		return frontMatter;
	}
	public Canvas getBackMatter(){
		return backMatter;
	}
	public ObservableList<Number> horizontalReferenceLinesProperty(){
		return horizontalReferenceLines;
	}
	public ObservableList<Number> verticalReferenceLinesProperty(){
		return verticalReferenceLines;
	}
	private void updateReferenceLines(){
		GraphicsContext g2d=frontMatter.getGraphicsContext2D();
		Rectangle2D viewport=object.viewportProperty().getValue();
		g2d.clearRect(viewport.getMinX(),viewport.getMinY(),viewport.getWidth(),viewport.getHeight());
	}
}
