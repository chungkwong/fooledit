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
import javafx.scene.control.*;
import javafx.stage.*;
import org.fxmisc.richtext.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Demo extends Application{
	private final CodeEditor area=new CodeEditor(null,null);
	private Selection caret;
	private int id=0;
	@Override
	public void start(Stage primaryStage){
		area.getStylesheets().add("file:///home/kwong/NetBeansProjects/fooledit/editor.text/stylesheets/default.css");
		Button addButton=new Button("Added caret");
		addButton.setOnAction((e)->{
			CaretNode caret=new CaretNode("caret"+(++id),area.getArea(),area.getArea().getCaretPosition());
			caret.setStyle("-fx-stroke:red");
			area.getArea().addCaret(caret);
		});
		area.setBottom(addButton);
		Scene scene=new Scene(area);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	public static void main(String[] args){
		launch(args);
	}
}
