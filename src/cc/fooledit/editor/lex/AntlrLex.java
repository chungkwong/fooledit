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
package cc.fooledit.editor.lex;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.tool.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class AntlrLex{
	public static void main(String[] args) throws IOException{
		split("/home/kwong/projects/grammars-v4/java8/examples/helloworld.java","/home/kwong/projects/grammars-v4/java8/Java8.g4");
	}
	public static List<org.antlr.v4.runtime.Token> split(String fileName,String combinedGrammarFileName)throws IOException{
		final Grammar g=Grammar.load(combinedGrammarFileName);
		LexerInterpreter lexEngine=g.createLexerInterpreter(CharStreams.fromPath(Paths.get(fileName)));
		CommonTokenStream tokens=new CommonTokenStream(lexEngine);
		tokens.fill();
		System.out.println(tokens.getTokens().stream().map((t)->g.getTokenName(t.getType())+t.getText()).collect(Collectors.toList()));
		return tokens.getTokens();
	}
}
