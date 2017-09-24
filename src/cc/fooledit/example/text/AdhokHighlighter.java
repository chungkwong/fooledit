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
package cc.fooledit.example.text;
import cc.fooledit.editor.lex.*;
import java.util.*;
import java.util.logging.*;
import javafx.application.*;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class AdhokHighlighter implements Highlighter{
	private final MetaLexer lex;
	public AdhokHighlighter(MetaLexer lex){
		this.lex=lex;
	}
	@Override
	public void highlight(CodeEditor editor){
		CodeArea area=editor.getArea();
		StyleSpans<Collection<String>> highlighting=computeHighlighting(area.getText());
		Platform.runLater(()->{
			try{
				area.setStyleSpans(0,highlighting);
			}catch(Exception ex){
				Logger.getGlobal().log(Level.FINEST,"",ex);
			}
		});
	}
	private StyleSpans<Collection<String>> computeHighlighting(String text){
		long time=System.currentTimeMillis();
		StyleSpansBuilder<Collection<String>> spansBuilder=new StyleSpansBuilder<>();
		Iterator<Token> iter=new InteruptableIterator<>(lex.split(text));
		while(iter.hasNext()){
			Token token=iter.next();
			spansBuilder.add(Collections.singleton(token.getType()),token.getText().length());
		}
		System.out.println(System.currentTimeMillis()-time);
		return spansBuilder.create();
	}
}
