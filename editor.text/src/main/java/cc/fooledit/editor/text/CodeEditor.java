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
package cc.fooledit.editor.text;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import javafx.beans.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import org.fxmisc.flowless.*;
import org.fxmisc.richtext.Caret.CaretVisibility;
import org.fxmisc.richtext.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CodeEditor extends BorderPane{
	private final CodeArea area=new CodeArea();
	private final IndentPolicy indentPolicy;
	private final StringProperty textProperty=new PlainTextProperty();
	private final CompleteManager completeManager;
	private final TooltipManager tooltipManager;
	private final LanguageManager languageManager;
	private final LineLabelManager lineLabelManager;
	private final MarkerManager markerManager;
	public CodeEditor(){
		area.currentParagraphProperty().addListener((e,o,n)->area.showParagraphInViewport(n));
		area.setInputMethodRequests(new InputMethodRequestsObject());
		area.setOnInputMethodTextChanged((e)->{
			if(!e.getCommitted().isEmpty()){
				area.insertText(area.getCaretPosition(),e.getCommitted());
			}
		});
		area.focusedProperty().addListener((e,o,n)->{
			if(n){
				area.setStyle("-fx-caret-blink-rate:500ms;");
			}else{
				area.setStyle("-fx-caret-blink-rate:0ms;");
			}
			area.showCaretProperty().setValue(CaretVisibility.ON);
		});
		indentPolicy=IndentPolicy.AS_PREVIOUS;
		setCenter(new VirtualizedScrollPane(area));
		getStylesheets().add(getClass().getResource("/stylesheet.css").toString());
		this.completeManager=new CompleteManager(this);
		this.tooltipManager=new TooltipManager(this);
		this.languageManager=new LanguageManager(this);
		this.lineLabelManager=new LineLabelManager(this);
		this.markerManager=new MarkerManager(this);
	}
	public TooltipManager getTooltipManager(){
		return tooltipManager;
	}
	public CompleteManager getCompleteManager(){
		return completeManager;
	}
	public LanguageManager getLanguageManager(){
		return languageManager;
	}
	public LineLabelManager getLineLabelManager(){
		return lineLabelManager;
	}
	public MarkerManager getMarkManager(){
		return markerManager;
	}
	private static int findMatchingBraceForward(String text,int start,char left,char right){
		int level=1;
		while(start<text.length()){
			char c=text.charAt(start);
			if(c==left){
				++level;
			}
			if(c==right){
				--level;
				if(level==0){
					return start;
				}
			}
			++start;
		}
		return -1;
	}
	private static int findMatchingBraceBackward(String text,int start,char left,char right){
		int level=1;
		while(start>=0){
			char c=text.charAt(start);
			if(c==right){
				++level;
			}
			if(c==left){
				--level;
				if(level==0){
					return start;
				}
			}
			--start;
		}
		return -1;
	}
	public int find(String target){
		String text=area.getText();
		int length=target.length();
		int start=0;
		SelectionGroup group=getMarkManager().getCurrentSelectionGroup();
		group.getSelections().clear();
		while((start=text.indexOf(target,start))!=-1){
			group.select(start,start+=length);
		}
		return getMarkManager().selections().size();
	}
	public int findRegex(String regex){
		Matcher matcher=Pattern.compile(regex).matcher(area.getText());
		SelectionGroup group=getMarkManager().getCurrentSelectionGroup();
		group.getSelections().clear();
		while(matcher.find()){
			group.select(matcher.start(),matcher.end());
		}
		return getMarkManager().selections().size();
	}
	public void replace(Function<String,String> tranform){
		getMarkManager().getCurrentSelectionGroup().getSelections().forEach((s)->area.replaceText(s.getRange(),tranform.apply(s.getSelectedText())));
	}
	public CodeArea getArea(){
		return area;
	}
	public StringProperty textProperty(){
		return textProperty;
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
	public void delete(){
		if(area.getSelection().getLength()==0){
			area.deleteNextChar();
		}else{
			area.replaceSelection("");
		}
	}
	public void backspace(){
		if(area.getSelection().getLength()==0){
			area.deletePreviousChar();
		}else{
			area.replaceSelection("");
		}
	}
	public void deleteNextChar(){
		area.nextChar(NavigationActions.SelectionPolicy.EXTEND);
		area.replaceSelection("");
	}
	public void deletePreviousChar(){
		area.previousChar(NavigationActions.SelectionPolicy.EXTEND);
		area.replaceSelection("");
	}
	public void nextLine(NavigationActions.SelectionPolicy policy){
		int targetParagraph=area.getCurrentParagraph()+1;
		if(targetParagraph<area.getParagraphs().size()){
			area.moveTo(targetParagraph,Math.min(area.getCaretColumn(),area.getParagraphLength(targetParagraph)),policy);
		}else{
			area.end(policy);
		}
	}
	public void nextSelection(){
		int curr=Math.max(area.getCaretPosition(),area.getAnchor());
		getMarkManager().getCurrentSelectionGroup().getSelections().stream().filter((range)->range.getStartPosition()>=curr).findFirst().ifPresent((range)->area.selectRange(range.getStartPosition(),range.getEndPosition()));
	}
	public void previousSelection(){
		int curr=Math.min(area.getCaretPosition(),area.getAnchor());
		getMarkManager().getCurrentSelectionGroup().getSelections().stream().filter((range)->range.getStartPosition()<curr).forEach((range)->area.selectRange(range.getStartPosition(),range.getEndPosition()));
	}
	public void previousLine(NavigationActions.SelectionPolicy policy){
		int targetParagraph=area.getCurrentParagraph()-1;
		if(targetParagraph>=0){
			area.moveTo(targetParagraph,Math.min(area.getCaretColumn(),area.getParagraphLength(targetParagraph)),policy);
		}else{
			area.start(policy);
		}
	}
	public void deleteLine(){
		int currentParagraph=area.getCurrentParagraph();
		if(currentParagraph+1<area.getParagraphs().size()){
			area.deleteText(currentParagraph,0,currentParagraph+1,0);
		}else{
			area.deleteText(currentParagraph,0,currentParagraph,area.getParagraphLength(currentParagraph));
		}
	}
	public void swapAnchorAndCaret(){
		area.selectRange(area.getCaretPosition(),area.getAnchor());
	}
	public void transform(Function<String,String> transformer){
		area.replaceSelection(transformer.apply(area.getSelectedText()));
	}
	public void transformLines(Function<Stream<String>,Stream<String>> transformer){
		IndexRange selection=area.getSelection();
		String text=area.getText();
		int start=text.lastIndexOf('\n',selection.getStart());
		int end=text.indexOf('\n',selection.getEnd());
		if(start==-1){
			start=0;
		}
		if(end==-1){
			end=text.length();
		}
		area.replaceText(start,end,transformer.apply(Pattern.compile("\\r\\n?|\\n").splitAsStream(area.getSelectedText())).collect(Collectors.joining("\n")));
	}
	@Override
	public void requestFocus(){
		area.requestFocus();
	}
	class InputMethodRequestsObject implements InputMethodRequests{
		@Override
		public String getSelectedText(){
			return area.getSelectedText();
		}
		@Override
		public int getLocationOffset(int x,int y){
			return area.getCaretPosition();
		}
		@Override
		public void cancelLatestCommittedText(){
		}
		@Override
		public Point2D getTextLocation(int offset){
			return area.getCharacterBoundsOnScreen(offset,offset).map((b)->new Point2D(b.getMinX(),b.getMaxY())).orElse(Point2D.ZERO);
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
			return CodeEditor.this;
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
