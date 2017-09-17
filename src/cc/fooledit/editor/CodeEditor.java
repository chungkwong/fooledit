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
package cc.fooledit.editor;
import cc.fooledit.control.*;
import cc.fooledit.editor.LineNumberFactory;
import cc.fooledit.editor.parser.*;
import cc.fooledit.util.*;
import java.text.*;
import java.util.*;
import java.util.function.*;
import javafx.application.*;
import javafx.beans.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import org.fxmisc.flowless.*;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
import org.reactfx.collection.*;
import org.reactfx.value.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CodeEditor extends BorderPane{
	private final CodeArea area=new CodeArea();
	//private final SyntaxSupport tree;
	private final LineNumberFactory header=new LineNumberFactory(area);
	private final IndentPolicy indentPolicy;
	private final StringProperty textProperty=new PlainTextProperty();
	private final TreeSet<Marker> markers=new TreeSet<>();
	private final ObservableList<IndexRange> selections=FXCollections.observableArrayList();
	private final ObservableList<Highlighter> highlighters=FXCollections.observableArrayList();
	public CodeEditor(Parser parser,Highlighter lex){
		if(lex!=null)
			lex.highlight(this);
		//tree=parser!=null?new SyntaxSupport(parser,lex,area):null;
		area.currentParagraphProperty().addListener((e,o,n)->area.showParagraphInViewport(n));
		area.setInputMethodRequests(new InputMethodRequestsObject());
		area.setOnInputMethodTextChanged((e)->{
			if(e.getCommitted()!=""){
				area.insertText(area.getCaretPosition(),e.getCommitted());
				System.out.println(e.getCommitted());
			}
		});
		area.setParagraphGraphicFactory(header);
		area.focusedProperty().addListener((e,o,n)->{
			if(n)
				area.setStyle("-fx-caret-blink-rate:500ms;");
			else
				area.setStyle("-fx-caret-blink-rate:0ms;");
			area.showCaretProperty().setValue(ViewActions.CaretVisibility.ON);
		});
		indentPolicy=IndentPolicy.AS_PREVIOUS;

		area.plainTextChanges().subscribe((e)->update(e));
		RealTimeTask<String> task=new RealTimeTask<>((text)->{
			highlighters.forEach((highlighter)->highlighter.highlight(CodeEditor.this));
		});
		selections.addListener(new ListChangeListener<IndexRange>(){
			@Override
			public void onChanged(ListChangeListener.Change<? extends IndexRange> c){
				c.next();
				if(c.wasAdded())
					SelectionHighlighter.INSTANCE.highlight(CodeEditor.this);
			}
		});
		area.textProperty().addListener((e,o,n)->task.summit(n));
		highlighters.add(lex);
		highlighters.add(SelectionHighlighter.INSTANCE);
		setCenter(new VirtualizedScrollPane(area));
	}
	public void reverseSelection(){
		FXCollections.sort(selections,(x,y)->Integer.compare(x.getStart(),y.getStart()));
		Iterator<IndexRange> iter=selections.iterator();
		if(iter.hasNext()){
			List<IndexRange> reversed=new ArrayList<>(selections.size()+1);
			IndexRange prev=iter.next();
			if(prev.getStart()!=0)
				reversed.add(new IndexRange(0,prev.getStart()));
			while(iter.hasNext()){
				IndexRange range=iter.next();
				reversed.add(new IndexRange(prev.getEnd(),range.getStart()));
				prev=range;
			}
			if(prev.getEnd()!=area.getLength())
				reversed.add(new IndexRange(prev.getEnd(),area.getLength()));
			selections.setAll(reversed);
		}else{
			selections.add(new IndexRange(0,area.getLength()));
		}
	}
	@Override
	public void requestFocus(){
		super.requestFocus();
		area.requestFocus();
	}
	private Runnable destroyCompleteSupport=null;
	public void setAutoCompleteProvider(AutoCompleteProvider provider,boolean once){
		if(destroyCompleteSupport!=null){
			destroyCompleteSupport.run();
			destroyCompleteSupport=null;
		}
		if(provider!=null)
			destroyCompleteSupport=new CompleteSupport(provider).apply(area,once);
	}
	private List<? extends org.antlr.v4.runtime.Token> tokens;
	void cache(List<? extends org.antlr.v4.runtime.Token> tokens){
		this.tokens=tokens;
	}
	public Property<Object> syntaxTree(){
		throw new UnsupportedOperationException();
//return tree.syntaxTree();
	}
	public CodeArea getArea(){
		return area;
	}
	public StringProperty textProperty(){
		return textProperty;
	}
	Map<Integer,Node> annotations(){
		return header.getMarks();
	}
	public ObservableList<IndexRange> selections(){
		return selections;
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
		if(area.getSelection().getLength()==0)
			area.deleteNextChar();
		else
			area.replaceSelection("");
	}
	public void backspace(){
		if(area.getSelection().getLength()==0)
			area.deletePreviousChar();
		else
			area.replaceSelection("");
	}
	public void deleteNextChar(){
		area.nextChar(NavigationActions.SelectionPolicy.EXTEND);
		area.replaceSelection("");
	}
	public void deletePreviousChar(){
		area.previousChar(NavigationActions.SelectionPolicy.EXTEND);
		area.replaceSelection("");
	}
	public void nextWord(NavigationActions.SelectionPolicy policy){
		int from=area.getCaretPosition();
		int to=tokens.stream().mapToInt((t)->t.getStartIndex()).filter((i)->i>from).findFirst().orElse(area.getLength());//TODO: bsearch is better
		area.moveTo(to,policy);
	}
	public void previousWord(NavigationActions.SelectionPolicy policy){
		int from=area.getCaretPosition();
		int to=tokens.stream().mapToInt((t)->t.getStartIndex()).filter((i)->i<from).max().orElse(0);//TODO: bsearch is better
		area.moveTo(to,policy);
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
		if(markers.isEmpty()&&selections.isEmpty())
			return;
		int oldPos;
		int newPos;
		switch(e.getType()){
			case DELETION:
				oldPos=e.getRemovalEnd();
				newPos=e.getPosition();
				markers.subSet(new Marker(e.getPosition(),null),new Marker(e.getRemovalEnd(),null)).clear();
				selections.removeIf((range)->range.getStart()<=e.getRemovalEnd()&&e.getPosition()<=range.getEnd());
				break;
			case INSERTION:
				oldPos=e.getPosition();
				newPos=e.getInsertionEnd();
				break;
			case REPLACEMENT:
				oldPos=e.getRemovalEnd();
				newPos=e.getInsertionEnd();
				markers.subSet(new Marker(e.getPosition(),null),new Marker(e.getRemovalEnd(),null)).clear();
				selections.removeIf((range)->range.getStart()<=e.getRemovalEnd()&&e.getPosition()<=range.getEnd());
				break;
			default:
				throw new RuntimeException();
		}
		int diff=newPos-oldPos;
		if(!markers.isEmpty())
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
		for(ListIterator<IndexRange> iter=selections.listIterator();iter.hasNext();){
			IndexRange range=iter.next();
			if(range.getStart()>=oldPos){
				iter.set(new IndexRange(range.getStart()+diff,range.getEnd()+diff));
			}else if(range.getEnd()>=oldPos){
				iter.set(new IndexRange(range.getStart(),range.getEnd()+diff));
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
class InteruptableIterator<T> implements Iterator<T>{
	private final Iterator<T> iter;
	public InteruptableIterator(Iterator<T> iter){
		this.iter=iter;
	}
	@Override
	public boolean hasNext(){
		if(Thread.currentThread().isInterrupted()){
			//System.err.println("oop");
			throw new RuntimeException();
		}
		return iter.hasNext();
	}
	@Override
	public T next(){
		return iter.next();
	}
}
class LineNumberFactory implements IntFunction<Node>{
	private static final Insets INSETS=new Insets(0.0,5.0,0.0,5.0);
	private static final Background BACKGROUND=new Background(new BackgroundFill(Color.LIGHTGRAY,null,null));
	private static final Font FONT=Font.font("monospace");
	private final NumberFormat format=NumberFormat.getIntegerInstance();
	private final Val<Integer> paragraphs;
	private final Map<Integer,Node> marks=new HashMap<>();
	LineNumberFactory(CodeArea area){
		paragraphs=LiveList.sizeOf(area.getParagraphs());
		paragraphs.addListener((e,o,n)->format.setMinimumIntegerDigits(getNumberOfDigit(n)));
	}
	public Map<Integer,Node> getMarks(){
		return marks;
	}
	@Override
	public Node apply(int idx){
		if(marks.containsKey(idx))
			return marks.get(idx);
		Val<String> formatted=paragraphs.map((n)->format.format(idx+1));
		Label lineNo=new Label();
		lineNo.setFont(FONT);
		lineNo.setBackground(BACKGROUND);
		lineNo.setPadding(INSETS);
		lineNo.getStyleClass().add("lineno");
        lineNo.textProperty().bind(formatted.conditionOnShowing(lineNo));
		return lineNo;
	}
	private static int getNumberOfDigit(int n){
		if(n<10)return 1;
		else if(n<100)return 2;
		else if(n<1000)return 3;
		else if(n<10000)return 4;
		else if(n<100000)return 5;
		else if(n<1000000)return 6;
		else if(n<10000000)return 7;
		else if(n<100000000)return 8;
		else return 9;
	}
}
class SelectionHighlighter implements Highlighter{
	public static final SelectionHighlighter INSTANCE=new SelectionHighlighter();
	@Override
	public void highlight(CodeEditor area){
		Platform.runLater(()->area.selections().forEach((range)->area.getArea().setStyleClass(range.getStart(),range.getEnd(),"selection")));
	}
}