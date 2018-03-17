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
public class CharacterChooser extends Application{
	@Override
	public void start(Stage primaryStage) throws Exception{
		int charPerLine=20;
		int lines=0x110000/charPerLine+1;
		Paginator paginator=new Paginator((i)->{
			/*int start=i*charPerLine;
			StringBuilder buf=new StringBuilder();
			for(int j=start;j<start+charPerLine;j++){
				try{
					buf.appendCodePoint(j);
				}catch(Exception ex){

				}
			}*/
			//return new Label(buf.toString());
			return new Label("Hello");
		},lines);
		primaryStage.setScene(new Scene(new BorderPane(paginator)));
		primaryStage.show();
		System.out.println(new Label("hello").getHeight());
		System.out.println(new Label("hello").getMinHeight());
		paginator.pageIndex().setValue(1);
		System.out.println("a"+new Label("hello").prefHeight(300));
		System.out.println("b"+new Label("hello").getMinHeight());
	}
	public static void main(String[] args){
		launch(args);
	}
}
