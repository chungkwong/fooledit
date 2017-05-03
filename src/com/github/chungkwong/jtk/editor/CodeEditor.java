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
import com.github.chungkwong.jtk.editor.lex.*;
import com.github.chungkwong.jtk.editor.parser.*;
import java.util.*;
import java.util.logging.*;
import javafx.beans.value.*;
import javafx.scene.layout.*;
import org.fxmisc.flowless.*;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CodeEditor extends BorderPane{
	private final CodeArea area=new CodeArea();
	private Lex lex;
	private Parser parser;
	public CodeEditor(){
		area.setParagraphGraphicFactory(LineNumberFactory.get(area));
		setCenter(new VirtualizedScrollPane(area));
	}
	private final ChangeListener<String> updateHighlight=(e,o,n)->{
		if(lex!=null)
			try{
				area.setStyleSpans(0,computeHighlighting(n));
			}catch(Exception ex){
				Logger.getGlobal().log(Level.FINEST,"",ex);
			}
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
		Iterator<Token> iter=lex.split(text);
		while(iter.hasNext()){
			Token token=iter.next();
			spansBuilder.add(Collections.singleton(token.getType()),token.getText().length());
		}
		return spansBuilder.create();
	}

	public void setAutoCompleteProvider(AutoCompleteProvider provider){
		new CompleteSupport(provider).apply(area);
	}
}
