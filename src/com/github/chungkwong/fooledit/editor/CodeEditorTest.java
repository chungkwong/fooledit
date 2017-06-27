/*
 * Copyright (C) 2017 Chan Chung Kwong <1m02math@126.com>
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
package com.github.chungkwong.fooledit.editor;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.fxmisc.richtext.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CodeEditorTest extends Application{
	@Override
	public void start(Stage primaryStage) throws Exception{
		BorderPane pane=new BorderPane();
		CodeEditor editor=new CodeEditor(null,null);
		CodeArea area=editor.getArea();
		pane.setCenter(editor);
		Label label=new Label();
		pane.setTop(label);
		Button mark=new Button("Mark");
		mark.setOnAction((e)->editor.mark(area.getCaretPosition(),""));
		pane.setBottom(mark);
		area.textProperty().addListener((e,o,n)->{
			label.setText(editor.getMarkers().toString());
		});
		primaryStage.setScene(new Scene(pane));
		primaryStage.show();
	}
	public static void main(String[] args){
		launch(args);
	}
}
