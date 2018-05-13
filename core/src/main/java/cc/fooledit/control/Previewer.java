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
import javafx.geometry.*;
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

		SideBarPane root=new SideBarPane(new TextArea());
		Button button=new Button("AAAA");

		root.getSideBar(Side.TOP).getTabs().add(new Tab("TOP",button));
		root.getSideBar(Side.BOTTOM).getTabs().add(new Tab("BOTTOM",new TextArea("This a a \n good news")));
		root.getSideBar(Side.LEFT).getTabs().add(new Tab("LEFT",new Button("BBBBB")));
		//root.getSideBar(Side.RIGHT).addItem("RIGHT",null,new TextArea("This a a \n good news"));

		Scene scene=new Scene(root);
		//scene.getStylesheets().add("file:///home/kwong/NetBeansProjects/fooledit/modules/core/stylesheets/dark.css");
		primaryStage.setScene(scene);
		//primaryStage.setScene(new Scene(new BorderPane(new BeanViewer(new Date()))));
		primaryStage.show();
		primaryStage.setMaximized(true);
		primaryStage.getScene().focusOwnerProperty().addListener((e,o,n)->{
			System.out.println(n);
		});
		System.out.println(new BorderPane().isDisable()+":"+new BorderPane().isDisabled());
	}
	public static void main(String[] args){
		launch(args);
	}
}
