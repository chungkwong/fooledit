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
import javafx.stage.*;
import org.fxmisc.richtext.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Demo extends Application{
	private final InlineCssTextArea area=new InlineCssTextArea();
	private Selection caret;
	@Override
	public void start(Stage primaryStage){
		area.replaceText("hello");
		System.out.println(area.addSelection(new SelectionImpl<>("brace",area,0,1)));
		Selection sel=new SelectionImpl<>("hello",area,(path)->path.getStyleClass().add("brace"));
		System.out.println(area.addSelection(sel));
		sel.selectRange(3,4);
		area.caretPositionProperty().addListener((e,o,n)->{
			if(caret!=null){
				area.removeSelection(caret);
			}
			int pos=area.getCaretPosition();
			if(pos>0){
				if(area.getText(pos-1,pos).equals(")")){
					int match=area.getText(0,pos).lastIndexOf('(');
					if(match!=-1){
						caret=new SelectionImpl("brace",area,match,match+1);
						area.addSelection(caret);
					}
				}
			}
		});
		primaryStage.setScene(new Scene(area));
		primaryStage.show();
	}
	public static void main(String[] args){
		launch(args);
	}
}
