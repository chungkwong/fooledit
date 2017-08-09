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
import com.github.chungkwong.fooledit.control.*;
import com.github.chungkwong.fooledit.editor.LineNumberFactory;
import com.github.chungkwong.fooledit.editor.lex.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.*;
import javafx.beans.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.tool.*;
import org.fxmisc.flowless.*;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CodeEditor2 extends BorderPane{
	private final CodeArea area=new CodeArea();
	private final AntlrHighlightSupport highlighter;
	//private final SyntaxSupport tree;
	private final LineNumberFactory header=new LineNumberFactory(area);
	private final IndentPolicy indentPolicy;
	private final StringProperty textProperty=new PlainTextProperty();
	private final TreeSet<Marker> markers=new TreeSet<>();
	public CodeEditor2(Grammar g) throws MalformedURLException, ClassNotFoundException{
		highlighter=g!=null?new AntlrHighlightSupport(LexerBuilder.wrap((Class<? extends Lexer>)new URLClassLoader(new URL[]{new File("/home/kwong/projects/grammars-v4/java8").toURL()}).loadClass("Java8Lexer")),area):null;
		//tree=g!=null?new SyntaxSupport(parser,lex,area):null;
		area.setInputMethodRequests(new InputMethodRequestsObject());
		area.setOnInputMethodTextChanged((e)->{
			if(e.getCommitted()!=""){
				area.insertText(area.getCaretPosition(),e.getCommitted());
			}
		});
		area.setParagraphGraphicFactory(header);
		indentPolicy=IndentPolicy.AS_PREVIOUS;

		area.plainTextChanges().subscribe((e)->update(e));

		setCenter(new VirtualizedScrollPane(area));
	}
	@Override
	public void requestFocus(){
		super.requestFocus();
		area.requestFocus();
	}
	public void setAutoCompleteProvider(AutoCompleteProvider provider){
		new CompleteSupport(provider).apply(area);
	}
	/*public Property<Object> syntaxTree(){
		return tree.syntaxTree();
	}*/
	public CodeArea getArea(){
		return area;
	}
	public StringProperty textProperty(){
		return textProperty;
	}
	Map<Integer,Node> annotations(){
		return header.getMarks();
	}
	public void newline(){
		area.insertText(area.getCaretPosition(),"\n");
		indentLine(area.getCurrentParagraph());
	}
	public void unindentLine(int line){
		area.deleteText(line,0,line,area.getText(line).replaceFirst("\\S.*","").length());
	}
	public void reindentLine(int line){
		unindentLine(line);
		indentLine(line);
	}
	public void indentLine(int line){
		area.insertText(line,0,indentPolicy.apply(area,line));
	}
	public void nextWord(NavigationActions.SelectionPolicy policy){
		int pos=area.getCaretPosition();
		StyleSpans<Collection<String>> styleSpans=area.getStyleSpans(pos,area.getLength());
		area.moveTo(styleSpans.getSpanCount()==0?area.getLength():pos+styleSpans.getStyleSpan(0).getLength(),policy);
	}
	public void previousWord(NavigationActions.SelectionPolicy policy){
		int pos=area.getCaretPosition();
		StyleSpans<Collection<String>> styleSpans=area.getStyleSpans(0,pos);
		area.moveTo(styleSpans.getSpanCount()==0?0:pos-styleSpans.getStyleSpan(styleSpans.getSpanCount()-1).getLength(),policy);
	}
	public void deleteNextWord(){
		nextWord(NavigationActions.SelectionPolicy.EXTEND);
		area.replaceSelection("");
	}
	public void deletePreviousWord(){
		previousWord(NavigationActions.SelectionPolicy.EXTEND);
		area.replaceSelection("");
	}
	public void nextLine(NavigationActions.SelectionPolicy policy){
		int targetParagraph=area.getCurrentParagraph()+1;
		if(targetParagraph<area.getParagraphs().size())
			area.moveTo(targetParagraph,Math.min(area.getCaretColumn(),area.getParagraphLenth(targetParagraph)),policy);
		else
			area.end(policy);
	}
	public void previousLine(NavigationActions.SelectionPolicy policy){
		int targetParagraph=area.getCurrentParagraph()-1;
		if(targetParagraph>=0)
			area.moveTo(targetParagraph,Math.min(area.getCaretColumn(),area.getParagraphLenth(targetParagraph)),policy);
		else
			area.start(policy);
	}
	public void deleteLine(){
		int currentParagraph=area.getCurrentParagraph();
		if(currentParagraph+1<area.getParagraphs().size())
			area.deleteText(currentParagraph,0,currentParagraph+1,0);
		else
			area.deleteText(currentParagraph,0,currentParagraph,area.getParagraphLenth(currentParagraph));
	}
	public void transform(Function<String,String> transformer){
		area.replaceSelection(transformer.apply(area.getSelectedText()));
	}
	private void update(PlainTextChange e){
		if(markers.isEmpty())
			return;;
		int oldPos;
		int newPos;
		switch(e.getType()){
			case DELETION:
				oldPos=e.getRemovalEnd();
				newPos=e.getPosition();
				markers.subSet(new Marker(e.getPosition(),null),new Marker(e.getRemovalEnd(),null)).clear();
				break;
			case INSERTION:
				oldPos=e.getPosition();
				newPos=e.getInsertionEnd();
				break;
			case REPLACEMENT:
				oldPos=e.getRemovalEnd();
				newPos=e.getInsertionEnd();
				markers.subSet(new Marker(e.getPosition(),null),new Marker(e.getRemovalEnd(),null)).clear();
				break;
			default:
				throw new RuntimeException();
		}
		int diff=newPos-oldPos;
		if(diff<0){
			Marker marker=markers.ceiling(new Marker(oldPos,null));
			while(marker!=null){
				marker.setOffset(marker.getOffset()+diff);
				marker=markers.higher(marker);
			}
		}else if(diff>0){
			Marker marker=markers.last();
			while(marker!=null&&marker.getOffset()>=oldPos){
				marker.setOffset(marker.getOffset()+diff);
				marker=markers.lower(marker);
			}
		}
	}
	public void mark(int offset,String tag){
		markers.add(new Marker(offset,tag));
	}
	public TreeSet<Marker> getMarkers(){
		return markers;
	}
	class InputMethodRequestsObject implements InputMethodRequests{
		@Override
		public String getSelectedText(){
			return "";
		}
		@Override
		public int getLocationOffset(int x,int y){
			return 0;
		}
		@Override
		public void cancelLatestCommittedText(){
		}
		@Override
		public Point2D getTextLocation(int offset){
			return new Point2D(0,0);
		}
	}
	class PlainTextProperty extends StringProperty{
		private ObservableValue<? extends String> observable=null;
		private InvalidationListener listener=null;
		@Override
		public String get(){
			return area.getText();
		}
		@Override
		public void addListener(ChangeListener<? super String> cl){
			area.textProperty().addListener((e,o,n)->cl.changed(PlainTextProperty.this,o,n));
		}
		@Override
		public void removeListener(ChangeListener<? super String> cl){
			area.textProperty().removeListener(cl);
		}
		@Override
		public void addListener(InvalidationListener il){
			area.textProperty().addListener((e)->il.invalidated(PlainTextProperty.this));
		}
		@Override
		public void removeListener(InvalidationListener il){
			area.textProperty().removeListener(il);
		}
		@Override
		public Object getBean(){
			return CodeEditor2.this;
		}
		@Override
		public String getName(){
			return "text";
		}
		@Override
		public void bind(ObservableValue<? extends String> observable){
			if(observable==null){
				throw new NullPointerException("Cannot bind to null");
			}
			if(!observable.equals(this.observable)){
				unbind();
				this.observable=observable;
				if(listener==null){
					listener=(e)->area.replaceText(observable.getValue());
				}
				this.observable.addListener(listener);
				area.replaceText(observable.getValue());
			}
		}
		@Override
		public void unbind(){
			if(observable!=null){
				area.replaceText(observable.getValue());
				observable.removeListener(listener);
				observable=null;
			}
		}
		@Override
		public boolean isBound(){
			return observable!=null;
		}
		@Override
		public void set(String t){
			if(isBound()){
				throw new java.lang.RuntimeException("A bound value cannot be set.");
			}
			area.replaceText(t);
		}
	}
}