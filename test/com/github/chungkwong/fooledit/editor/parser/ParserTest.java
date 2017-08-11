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
import com.github.chungkwong.fooledit.api.*;
import com.github.chungkwong.fooledit.editor.lex.*;
import com.github.chungkwong.fooledit.util.*;
import com.github.chungkwong.json.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import org.junit.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ParserTest{
	@Test
	public void testLR1Parser(){
		test1(LR1Parser.FACTORY);
		test2(LR1Parser.FACTORY);
	}
	@Test
	public void testLL1Parser(){
		test1(LL1Parser.FACTORY);
		test2(LL1Parser.FACTORY);
	}
	@Test
	public void testNaiveParser(){
		test1(NaiveParser.FACTORY);
		test2(NaiveParser.FACTORY);
	}
	public void test1(ParserFactory factory){
		String start="START";
		String word="WORD",number="NUMBER",other="OTHER";
		ArrayList<ProductionRule> rules=new ArrayList<>();
		rules.add(new ProductionRule(start,new String[]{word,other,number},
				(a)->a[0].toString().substring(Integer.parseInt(a[2].toString()))));
		ContextFreeGrammar cfg=new ContextFreeGrammar(start,rules,Helper.hashMap(
				word,Function.identity(),number,Function.identity(),other,Function.identity()));
		Parser parser=factory.createParser(cfg);
		RegularExpressionLexer lex=new RegularExpressionLexer();
		lex.addType(MetaLexer.INIT,"[0-9]+","NUMBER",MetaLexer.INIT);
		lex.addType(MetaLexer.INIT,"[a-zA-Z]+","WORD",MetaLexer.INIT);
		lex.addType(MetaLexer.INIT,"[^0-9a-zA-Z]","OTHER",MetaLexer.INIT);
		Assert.assertEquals(parser.parse(lex.split("abcd-2")),"cd");
	}
	public void test2(ParserFactory factory){
		String start="properties";
		List<ProductionRule> rules=new ArrayList<>();
		rules.add(new ProductionRule("property",new String[]{"key","value"},(o)->new Pair<>(o[0],o[1])));
		rules.add(new ProductionRule("properties",new String[]{},(o)->Collections.emptyMap()));
		rules.add(new ProductionRule("properties",new String[]{"property","properties"},(o)->{
			Map<String,String> o0=(Map<String,String>)o[1];
			Map<String,String> re=new HashMap<>(o0);
			Pair<String,String> head=(Pair<String,String>)o[0];
			re.put(head.getKey(),head.getValue());
			return re;
		}));
		Map<String,Function<String,Object>> terminals=new HashMap<>();
		terminals.put("key",(s)->s.trim());
		terminals.put("value",(s)->s);
		Parser parser=factory.createParser(new ContextFreeGrammar(start,rules,terminals));
		NaiveLexer lex=new NaiveLexer();
		try{
			LexBuilders.fromJSON(Helper.readText("/com/github/chungkwong/fooledit/editor/parser/mf.json"),lex);
		}catch(IOException|SyntaxException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
		Object result=parser.parse(lex.split("gj=78 3\nbdn: 780"));
		Assert.assertEquals(result,Helper.hashMap("gj","78 3","bdn","780"));
	}
}
