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
import cc.fooledit.editor.lex.*;
import java.util.*;
import java.util.logging.*;
import javafx.application.*;
import org.antlr.v4.runtime.*;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class AntlrHighlighter implements Highlighter{
	private final LexerBuilder lexerBuilder;
	private Collection[] styles;
	private Map<String,String> superType;
	public AntlrHighlighter(LexerBuilder lexerBuilder){
		this(lexerBuilder,Collections.emptyMap());
	}
	public AntlrHighlighter(LexerBuilder lexerBuilder,Map<String,String> superType){
		this.lexerBuilder=lexerBuilder;
		this.superType=superType;
	}
	@Override
	public void highlight(CodeEditor editor){
		CodeArea area=editor.getArea();
		List<? extends org.antlr.v4.runtime.Token> tokens=computeTokens(area.getText());
		if(tokens.isEmpty())
			return;
		StyleSpans<Collection<String>> highlighting=computeHighlighting(tokens);
		Platform.runLater(()->{
			try{
				area.setStyleSpans(0,highlighting);
				editor.cache(tokens);
			}catch(Exception ex){
				Logger.getGlobal().log(Level.FINEST,"",ex);
			}
		});
	}
	private List<? extends org.antlr.v4.runtime.Token> computeTokens(String text){
		//LexerInterpreter lexEngine=grammar.createLexerInterpreter(CharStreams.fromString(text));
		Lexer lexEngine=lexerBuilder.create(text);
		if(styles==null){
			initStyles(lexEngine);
		}
		//CommonTokenStream tokenstream=new CommonTokenStream(lexEngine);
		//tokenstream.fill();
		//List<org.antlr.v4.runtime.Token> tokens=tokenstream.getTokens();
		return lexEngine.getAllTokens();
	}
	private StyleSpans<Collection<String>> computeHighlighting(List<? extends org.antlr.v4.runtime.Token> tokens){
		StyleSpansBuilder<Collection<String>> spansBuilder=new StyleSpansBuilder<>();
		index=0;
		tokens.forEach((t)->{
			if(t.getStartIndex()>index)
				spansBuilder.add(Collections.singleton("comment"),t.getStartIndex()-index);
			index=t.getStopIndex()+1;
			spansBuilder.add(styles[t.getType()+1],index-t.getStartIndex());
		});
		return spansBuilder.create();
	}
	private void initStyles(Lexer lexEngine){
		styles=new Collection[lexEngine.getTokenTypeMap().values().stream().mapToInt((i)->i).max().orElse(-1)+2];
		styles[0]=Collections.singleton("EOF");
		lexEngine.getTokenTypeMap().forEach((s,i)->{
			if(i>=0){
				if(styles[i+1]==null)
					styles[i+1]=new LinkedList();
				styles[i+1].add(s);
				String subType=s;
				while(superType.containsKey(subType)){
					subType=superType.get(subType);
					styles[i+1].add(subType);
				}
			}
		});
		superType=null;
	}
	private int index=0;
}
