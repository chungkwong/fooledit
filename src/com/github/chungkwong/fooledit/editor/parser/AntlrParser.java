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
package com.github.chungkwong.fooledit.editor.parser;
import java.io.*;
import java.nio.file.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.tool.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class AntlrParser{

	public static void main(String[] args) throws IOException{
		parse("/home/kwong/projects/grammars-v4/java8/examples/helloworld.java","/home/kwong/projects/grammars-v4/java8/Java8.g4","compilationUnit");
	}
	public static ParseTree parse(String fileName,String combinedGrammarFileName,String startRule)throws IOException{
		final Grammar g=Grammar.load(combinedGrammarFileName);
		LexerInterpreter lexEngine=g.createLexerInterpreter(CharStreams.fromPath(Paths.get(fileName)));
		CommonTokenStream tokens=new CommonTokenStream(lexEngine);
		ParserInterpreter parser=g.createParserInterpreter(tokens);
		ParseTree t=parser.parse(g.getRule(startRule).index);
		System.out.println("parse tree: "+t.toStringTree(parser));
		return t;
	}
}
