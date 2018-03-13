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
		TextArea content=new TextArea("");
		Label box1=new Label("AAAAAAAAAA");
		Label box2=new Label("BBBBBBBBBB");
		TitledPane p1=new TitledPane("1",new Button("CCC"));
		TitledPane p2=new TitledPane("2",new Button("DDD"));
		SideBar bar=new SideBar(SideBar.Side.RIGHT);
		bar.addItem("AA",null,box1);
		bar.addItem("BB",null,box2);
		primaryStage.setScene(new Scene(new BorderPane(content,null,bar,null,null)));
		//primaryStage.setScene(new Scene(new BorderPane(new BeanViewer(new Date()))));
		primaryStage.show();
	}
	public static void main(String[] args){
		launch(args);
	}
}
