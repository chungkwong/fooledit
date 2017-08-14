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
import cc.fooledit.util.*;
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
public class AntlrHighlighter implements TokenHighlighter{
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
	public void apply(CodeArea area){
		RealTimeTask<String> task=new RealTimeTask<>((text)->{
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
		long time=System.currentTimeMillis();
		//LexerInterpreter lexEngine=grammar.createLexerInterpreter(CharStreams.fromString(text));
		Lexer lexEngine=lexerBuilder.create(text);
		if(styles==null){
			styles=new Collection[lexEngine.getTokenTypeMap().values().stream().mapToInt((i)->i).max().orElse(-1)+1];
			lexEngine.getTokenTypeMap().forEach((s,i)->{
				if(i>=0){
					if(styles[i]==null)
						styles[i]=new LinkedList();
					styles[i].add(s);
					String subType=s;
					while(superType.containsKey(subType)){
						subType=superType.get(subType);
						styles[i].add(subType);
					}
				}
			});
			superType=null;
		}
		CommonTokenStream tokenstream=new CommonTokenStream(lexEngine);
		tokenstream.fill();
		List<org.antlr.v4.runtime.Token> tokens=tokenstream.getTokens();
		StyleSpansBuilder<Collection<String>> spansBuilder=new StyleSpansBuilder<>();
		index=0;
		tokens.forEach((t)->{
			if(t.getStartIndex()>index)
				spansBuilder.add(Collections.singleton("whitespace"),t.getStartIndex()-index);
			index=t.getStopIndex()+1;
			if(t.getType()!=-1)
				spansBuilder.add(styles[t.getType()],index-t.getStartIndex());
		});
		System.err.println(System.currentTimeMillis()-time);
		return spansBuilder.create();
	}
	private int index=0;
}
