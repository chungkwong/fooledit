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
import javafx.scene.paint.*;
import javafx.stage.*;
import javafx.util.*;
import org.fxmisc.richtext.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Demo extends Application{
	private InlineCssTextArea area;
	@Override
	public void start(Stage primaryStage){
		// initialize area with some lines of text
		String alphabet="abcdefghijklmnopqrstuvwxyz";
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<10;i++){
			sb.append(i).append(" :").append(alphabet).append("\n");
		}
		area=new InlineCssTextArea(sb.toString());
		setupRTFXSpecificCSSShapes();
		addExtraCaret();
		addExtraSelection();
		// select some other range with the regular caret/selection before showing area
		area.selectRange(2,0,2,4);
		primaryStage.setScene(new Scene(area,400,400));
		primaryStage.show();
		// request focus so carets blink
		area.requestFocus();
	}
	private void addExtraCaret(){
		CaretNode extraCaret=new CaretNode("another caret",area);
		if(!area.addCaret(extraCaret)){
			throw new IllegalStateException("caret was not added to area");
		}
		extraCaret.moveTo(3,8);
		// since the CSS properties are re-set when it applies the CSS from files
		// remove the style class so that properties set below are not overridden by CSS
		extraCaret.getStyleClass().remove("caret");
		extraCaret.setStrokeWidth(10.0);
		extraCaret.setStroke(Color.BROWN);
		extraCaret.setBlinkRate(Duration.millis(200));
	}
	private void addExtraSelection(){
		Selection<String,String,String> extraSelection=new SelectionImpl<>("another selection",area,
				path->{
					// make rendered selection path look like a yellow highlighter
					path.setStrokeWidth(0);
					path.setFill(Color.YELLOW);
				}
		);
		if(!area.addSelection(extraSelection)){
			throw new IllegalStateException("selection was not added to area");
		}
		// select something so it is visible
		extraSelection.selectRange(7,2,7,8);
	}
	/**
	 * Shows that RTFX-specific-CSS shapes are laid out in correct order, so
	 * that selection and text and caret appears on top of them when made/moved
	 */
	private void setupRTFXSpecificCSSShapes(){
		String background="-rtfx-background-color: red; ";
		String underline="-rtfx-underline-color: blue; "
				+"-rtfx-underline-width: 2.0;";
		String border="-rtfx-border-stroke-color: green; "
				+"-rtfx-border-stroke-width: 3.0;";
		// set all of them at once on a give line to insure they display properly
		area.setStyle(0,background+underline+border);
		// set each one over a given segment
		area.setStyle(1,0,3,background);
		area.setStyle(1,4,7,underline);
		area.setStyle(1,8,11,border);
	}
	public static void main(String[] args){
		launch(args);
	}
}
