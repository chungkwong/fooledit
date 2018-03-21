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
package cc.fooledit.control;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Previewer extends Application{
	@Override
	public void start(Stage primaryStage) throws Exception{
		BorderPane inner=new BorderPane(new TextArea(),new Label("a"),new Label("b"),new Label("c"),new Label("d"));
		BorderPane content=new BorderPane(inner,new Label("e"),new Label("f"),new Label("g"),new Label("h"));
		Button b1=new Button("UP");

		b1.setOnAction((e)->{
			primaryStage.getScene().focusOwnerProperty();
			inner.requestFocus();
		});


		VBox box1=new VBox(b1);
		Label box2=new Label("BBBBBBBBBB");
		Tab t=new Tab("MMM");

		SideBar bar=new SideBar(SideBar.Side.RIGHT);
		bar.addItem("AA",null,box1);
		bar.addItem("BB",null,box2);
		Scene scene=new Scene(new BorderPane(content,null,bar,null,null));
		scene.getStylesheets().add("file:///home/kwong/NetBeansProjects/fooledit/modules/core/stylesheets/dark.css");
		primaryStage.setScene(scene);
		//primaryStage.setScene(new Scene(new BorderPane(new BeanViewer(new Date()))));
		primaryStage.show();
		primaryStage.getScene().focusOwnerProperty().addListener((e,o,n)->{
			System.out.println(n);
		});

	}
	public static void main(String[] args){
		launch(args);
	}
}
