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
import com.github.chungkwong.fooledit.util.*;
import java.util.*;
import java.util.logging.*;
import javafx.application.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.tool.*;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class AntlrHighlightSupport{
	private final CodeArea area;
	private final Grammar grammar;
	private final RealTimeTask<String> task;
	public AntlrHighlightSupport(Grammar grammar,CodeArea area){
		this.area=area;
		this.grammar=grammar;
		this.task=new RealTimeTask<>((text)->{
			StyleSpans<Collection<String>> highlighting=computeHighlighting(text);
			Platform.runLater(()->{
				try{
					area.setStyleSpans(0,highlighting);
				}catch(Exception ex){
					Logger.getGlobal().log(Level.FINEST,"",ex);
				}
			});
		});
		area.textProperty().addListener((e,o,n)->task.summit(n));
	}
	private StyleSpans<Collection<String>> computeHighlighting(String text){
		LexerInterpreter lexEngine=grammar.createLexerInterpreter(CharStreams.fromString(text));
		CommonTokenStream tokenstream=new CommonTokenStream(lexEngine);
		tokenstream.fill();
		List<org.antlr.v4.runtime.Token> tokens=tokenstream.getTokens();
		StyleSpansBuilder<Collection<String>> spansBuilder=new StyleSpansBuilder<>();
		tokens.forEach((t)->{
			spansBuilder.add(Collections.singleton(grammar.getTokenName(t.getType())),t.getText().length());
		});
		return spansBuilder.create();
	}
}
