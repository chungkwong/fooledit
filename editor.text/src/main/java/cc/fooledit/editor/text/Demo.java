/*
 * Copyright (C) 2018 Chan Chung Kwong
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
package cc.fooledit.editor.text;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import org.fxmisc.flowless.*;
import org.fxmisc.richtext.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Demo extends Application{
	@Override
	public void start(Stage primaryStage) throws Exception{
		CodeArea area=new CodeArea();
		area.setWrapText(true);
		Canvas canvas=new Canvas(500,500);
		canvas.getGraphicsContext2D().setStroke(Color.GREEN);
		canvas.getGraphicsContext2D().strokeLine(100,0,100,500);
		StackPane ruledArea=new StackPane(new VirtualizedScrollPane(area),canvas);
		primaryStage.setScene(new Scene(ruledArea));
		primaryStage.show();
	}
	public static void main(String[] args){
		launch(args);
	}
}
