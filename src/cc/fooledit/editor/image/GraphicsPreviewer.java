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
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GraphicsPreviewer extends Application{
	@Override
	public void start(Stage primaryStage) throws Exception{
		Node[] layers=new Node[]{
			new ImageView("file:///home/kwong/NetBeansProjects/fooledit/modules/core/icons/folder.png"),
			new ImageView("file:///home/kwong/NetBeansProjects/fooledit/modules/core/icons/logo.png")
		};
		
		GraphicsObject object=new GraphicsObject(layers);
		primaryStage.setScene(new Scene(new BorderPane(new ScrollPane(new GraphicsViewer(object)))));
		primaryStage.show();
	}
	public static void main(String[] args){
		launch(args);
	}
}
