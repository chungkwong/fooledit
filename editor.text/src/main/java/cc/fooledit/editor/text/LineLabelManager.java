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
import java.text.*;
import java.util.*;
import java.util.function.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import org.reactfx.collection.*;
import org.reactfx.value.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class LineLabelManager{
	private final CodeEditor editor;
	private final ObservableMap<Integer,Node> marks=FXCollections.observableMap(new HashMap<>());
	public LineLabelManager(CodeEditor editor){
		this.editor=editor;
		editor.getArea().setParagraphGraphicFactory(new LineNumberFactory());
	}
	public ObservableMap<Integer,Node> getLabels(){
		return marks;
	}
	private class LineNumberFactory implements IntFunction<Node>{
		private final Insets INSETS=new Insets(0.0,5.0,0.0,5.0);
		private final Background BACKGROUND=new Background(new BackgroundFill(Color.LIGHTGRAY,null,null));
		private final Font FONT=Font.font("monospace");
		private final NumberFormat format=NumberFormat.getIntegerInstance();
		private final Val<Integer> paragraphs;
		LineNumberFactory(){
			paragraphs=LiveList.sizeOf(editor.getArea().getParagraphs());
			paragraphs.addListener((e,o,n)->format.setMinimumIntegerDigits(getNumberOfDigit(n)));
		}
		public Map<Integer,Node> getMarks(){
			return marks;
		}
		@Override
		public Node apply(int idx){
			if(marks.containsKey(idx)){
				return marks.get(idx);
			}
			Val<String> formatted=paragraphs.map((n)->format.format(idx+1));
			Label lineNo=new Label();
			lineNo.setFont(FONT);
			lineNo.setBackground(BACKGROUND);
			lineNo.setPadding(INSETS);
			lineNo.getStyleClass().add("lineno");
			lineNo.textProperty().bind(formatted.conditionOnShowing(lineNo));
			return lineNo;
		}
		private int getNumberOfDigit(int n){
			if(n<10){
				return 1;
			}else if(n<100){
				return 2;
			}else if(n<1000){
				return 3;
			}else if(n<10000){
				return 4;
			}else if(n<100000){
				return 5;
			}else if(n<1000000){
				return 6;
			}else if(n<10000000){
				return 7;
			}else if(n<100000000){
				return 8;
			}else{
				return 9;
			}
		}
	}
}
