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
package com.github.chungkwong.jtk.editor.lex;
import java.util.*;
import java.util.regex.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class NaiveLex implements Lex{
	private final HashMap<String,Pattern> types=new HashMap<>();
	public NaiveLex(){

	}
	@Override
	public void addType(String type,String regex){
		types.put(type,Pattern.compile(regex));
	}
	@Override
	public Iterator<Token> split(String text){
		return new TokenIterator(text);
	}
	private class TokenIterator implements Iterator<Token>{
		private final String text;
		private int index=0;
		private Token token;
		public TokenIterator(String text){
			this.text=text;
		}
		@Override
		public boolean hasNext(){
			if(token!=null)
				return true;
			for(Map.Entry<String,Pattern> entry:types.entrySet()){
				Matcher matcher=entry.getValue().matcher(text);
				matcher.region(index,text.length());
				if(matcher.lookingAt()){
					token=new Token(matcher.group(),entry.getKey());
					index=matcher.end();
					return true;
				}
			}
			return false;
		}
		@Override
		public Token next(){
			if(hasNext()){
				Token tmp=token;
				token=null;
				return tmp;
			}else{
				throw new NoSuchElementException();
			}
		}
	}
}