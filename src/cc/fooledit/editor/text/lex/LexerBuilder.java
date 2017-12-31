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
package cc.fooledit.editor.text.lex;
import java.util.logging.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.tool.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public interface LexerBuilder{
	Lexer create(String text);
	static LexerBuilder wrap(Class<? extends Lexer> cls){
		return (text)->{
			try{
				return cls.getConstructor(CharStream.class).newInstance(CharStreams.fromString(text));
			}catch(ReflectiveOperationException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				return null;
			}
		};
	}
	static LexerBuilder wrap(Grammar grammar){
		return (text)->grammar.createLexerInterpreter(CharStreams.fromString(text));
	}
}
