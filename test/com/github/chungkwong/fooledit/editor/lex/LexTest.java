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
package com.github.chungkwong.fooledit.editor.lex;
import com.github.chungkwong.fooledit.api.*;
import com.github.chungkwong.json.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import org.junit.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LexTest{
	@Test
	public void testNFALex(){
		testLex1(new RegularExpressionLex());
		testLex2(new RegularExpressionLex());
	}
	@Test
	public void testNaiveLex(){
		testLex1(new NaiveLex());
		testLex2(new NaiveLex());
	}
	private void testLex1(Lex lex){
		lex.addType(Lex.INIT,"[0-9]+","NUMBER",Lex.INIT);
		lex.addType(Lex.INIT,"[a-zA-Z]+","WORD",Lex.INIT);
		lex.addType(Lex.INIT,"[^0-9a-zA-Z]","OTHER",Lex.INIT);
		assertSplit(lex,"log67m=!","log","67","m","=","!");
	}
	private void testLex2(Lex lex){
		try{
			LexBuilder.fromJSON(Helper.readText("/com/github/chungkwong/fooledit/editor/parser/mf.json"),lex);
		}catch(IOException|SyntaxException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
		assertSplit(lex,"hello","hello");
		assertSplit(lex,"hello=ui","hello","=","ui");
	}
	private void assertSplit(Lex lex,String text,String... tokens){
		Object[] result=StreamSupport.stream(Spliterators.spliteratorUnknownSize(lex.split(text),0),false).
				map((t)->t.getText()).toArray();
		Assert.assertArrayEquals(result,tokens);
	}
}