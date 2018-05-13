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
package cc.fooledit.editor.text.parser;
import java.util.*;
import java.util.logging.*;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.tool.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public interface ParserBuilder{
	Object parse(List<? extends Token> tokens);
	static ParserBuilder wrap(Class<? extends Parser> cls,String rule){
		return (tokens)->{
			try{
				Parser parser=cls.getConstructor(TokenStream.class).newInstance(new CommonTokenStream(new ListTokenSource(tokens)));
				return cls.getMethod(rule).invoke(parser);
			}catch(ReflectiveOperationException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				return null;
			}
		};
	}
	static ParserBuilder wrap(Grammar grammar,String rule){
		return (tokens)->grammar.createParserInterpreter(new CommonTokenStream(new ListTokenSource(tokens)))
				.parse(grammar.getRule(rule).index);
	}
}
