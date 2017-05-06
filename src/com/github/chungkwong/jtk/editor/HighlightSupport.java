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
import com.github.chungkwong.jtk.editor.lex.*;
import com.github.chungkwong.jtk.util.*;
import java.util.*;
import java.util.logging.*;
import javafx.application.*;
import org.fxmisc.richtext.*;
import org.fxmisc.richtext.model.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class HighlightSupport{
	private final CodeArea area;
	private final Lex lex;
	private final RealTimeTask<Pair<PlainTextChange,String>> task;
	private TreeMap<Integer,Integer> checkpoint;
	private static final int CHECKPOINT_INTERVAL=1024;
	public HighlightSupport(Lex lex,CodeArea area){
		this.area=area;
		this.lex=lex;
		this.task=new RealTimeTask<>((p)->updateHighlighting(p.getKey(),p.getValue()));
		checkpoint.put(0,Lex.INIT);
		area.plainTextChanges().subscribe((e)->task.summit(new Pair<>(e,area.getText())));
	}
	private void updateHighlighting(PlainTextChange e,String text){
		StyleSpans<Collection<String>> highlighting=computeHighlighting(text);
		Platform.runLater(()->{
			try{
				area.setStyleSpans(0,highlighting);
			}catch(Exception ex){
				Logger.getGlobal().log(Level.FINEST,"",ex);
			}
		});
	}
	private StyleSpans<Collection<String>> computeHighlighting(String text){
		//long t=System.nanoTime();
		StyleSpansBuilder<Collection<String>> spansBuilder=new StyleSpansBuilder<>();
		Iterator<Token> iter=new InteruptableIterator<>(lex.split(text));
		while(iter.hasNext()){
			Token token=iter.next();
			spansBuilder.add(Collections.singleton(token.getType()),token.getText().length());
		}
		//System.err.println(System.nanoTime()-t);
		return spansBuilder.create();
	}

}
