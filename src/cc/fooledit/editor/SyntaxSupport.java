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
import cc.fooledit.editor.parser.Parser;
import cc.fooledit.util.*;
import javafx.beans.property.*;
import org.fxmisc.richtext.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SyntaxSupport{
	private final Parser parser;
	private final MetaLexer lex;
	private final CodeArea area;
	private final RealTimeTask<String> syntaxTask;
	private final Property<Object> syntaxTree=new SimpleObjectProperty<>();
	public SyntaxSupport(Parser parser,MetaLexer lex,CodeArea area){
		this.parser=parser;
		this.lex=lex;
		this.area=area;
		syntaxTask=new RealTimeTask<>((text)->computeSyntaxTree(text));
		area.textProperty().addListener((e,o,n)->syntaxTask.summit(n));

	}
	private void computeSyntaxTree(String text){
		syntaxTree.setValue(parser.parse(new InteruptableIterator<>(lex.split(text))));
	}
	public Property<Object> syntaxTree(){
		return syntaxTree;
	}
}
