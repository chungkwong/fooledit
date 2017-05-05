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
package com.github.chungkwong.jtk.editor;
import com.github.chungkwong.jtk.control.*;
import com.github.chungkwong.jtk.editor.LineNumberFactory;
import com.github.chungkwong.jtk.editor.lex.*;
import com.github.chungkwong.jtk.editor.parser.*;
import com.github.chungkwong.jtk.util.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
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
	private Lex lex;
	private Parser parser;
	public CodeEditor(){
		area.setParagraphGraphicFactory(new LineNumberFactory(area));
		setCenter(new VirtualizedScrollPane(area));
	}
	@Override
	public void requestFocus(){
		super.requestFocus();
		area.requestFocus();
	}
	private final RealTimeTask<String> hightlightTask=new RealTimeTask<>((text)->{
		Platform.runLater(()->{
			try{
				area.setStyleSpans(0,computeHighlighting(text));
			}catch(Exception ex){
				Logger.getGlobal().log(Level.FINEST,"",ex);
			}
		});
	});
	private final ChangeListener<String> updateHighlight=(e,o,n)->{
		if(lex!=null)
			hightlightTask.summit(n);
	};
	public void setLex(Lex lex){
		if(this.lex==null)
			area.textProperty().addListener(updateHighlight);
		this.lex=lex;
		if(lex==null)
			area.textProperty().removeListener(updateHighlight);
	}
	private StyleSpans<Collection<String>> computeHighlighting(String text){
		StyleSpansBuilder<Collection<String>> spansBuilder=new StyleSpansBuilder<>();
		Iterator<Token> iter=new InteruptableIterator<>(lex.split(text));
		while(iter.hasNext()){
			Token token=iter.next();
			spansBuilder.add(Collections.singleton(token.getType()),token.getText().length());
		}
		return spansBuilder.create();
	}
	public void setAutoCompleteProvider(AutoCompleteProvider provider){
		new CompleteSupport(provider).apply(area);
	}
	private final RealTimeTask<String> syntaxTask=new RealTimeTask<>((text)->{
		Platform.runLater(()->{
			try{
				computeSyntaxTree(text);
			}catch(Exception ex){
				Logger.getGlobal().log(Level.FINEST,"",ex);
			}
		});
	});
	private final ChangeListener<String> updateSyntaxTree=(e,o,n)->{
		if(parser!=null)
			syntaxTask.summit(n);
	};
	public void setParser(Parser parser){
		if(this.parser==null)
			area.textProperty().addListener(updateSyntaxTree);
		this.parser=parser;
		if(parser==null)
			area.textProperty().removeListener(updateSyntaxTree);
	}
	private final Property<Object> syntaxTree=new SimpleObjectProperty<>();
	private void computeSyntaxTree(String text){
		syntaxTree.setValue(parser.parse(new InteruptableIterator<>(lex.split(text))));
	}
	public Property<Object> syntaxTree(){
		return syntaxTree;
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
			System.err.println("oop");
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
	private static final Insets DEFAULT_INSETS=new Insets(0.0,5.0,0.0,5.0);
	private static final Background BACKGROUND=new Background(new BackgroundFill(Color.LIGHTGRAY,null,null));
	private static final Font FONT=Font.font("monospace");
	private final Val<Integer> nParagraphs;
	LineNumberFactory(CodeArea area){
		nParagraphs=LiveList.sizeOf(area.getParagraphs());
	}
	@Override
	public Node apply(int idx){
		Val<String> formatted=nParagraphs.map(n->Integer.toString(idx+1));
		Label lineNo=new Label();
		lineNo.setFont(FONT);
		lineNo.setBackground(BACKGROUND);
		lineNo.setPadding(DEFAULT_INSETS);
		lineNo.getStyleClass().add("lineno");
        lineNo.textProperty().bind(formatted.conditionOnShowing(lineNo));
		return lineNo;
	}
}
