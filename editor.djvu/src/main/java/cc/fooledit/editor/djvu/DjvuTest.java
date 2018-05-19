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
package cc.fooledit.editor.djvu;
import com.lizardtech.djvu.*;
import java.net.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DjvuTest extends Application{
	@Override
	public void start(Stage primaryStage) throws Exception{
		DjvuViewer djvuViewer=new DjvuViewer(new Document(new URL("file:///home/kwong/sysu_learning2/黎曼几何/4520-Comparison Theorems in Riemannian Geometry.djvu")));
		djvuViewer.scaleProperty().setValue(1.0);
		primaryStage.setScene(new Scene(new BorderPane(djvuViewer)));
		primaryStage.show();
	}
	public static void main(String[] args) throws Exception{
		launch(args);
	}
}
