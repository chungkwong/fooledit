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
import com.github.chungkwong.jtk.util.*;
import java.util.*;
import java.util.regex.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class NaiveLex implements Lex{
	private final Map<Integer,Map<Pattern,Pair<String,Integer>>> types=new HashMap<>();
	public NaiveLex(){

	}
	@Override
	public void addType(int status,String regex,String type,int newStatus){
		Map<Pattern,Pair<String,Integer>> map=types.get(status);
		if(map==null){
			map=new HashMap<>();
			types.put(status,map);
		}
		map.put(Pattern.compile(regex,Pattern.DOTALL|Pattern.MULTILINE),new Pair<>(type,newStatus));
	}
	@Override
	public Iterator<Token> split(String text){
		return new TokenIterator(text);
	}
	private class TokenIterator implements Iterator<Token>{
		private final String text;
		private int index=0;
		private Token token;
		private int status=INIT;
		public TokenIterator(String text){
			this.text=text;
		}
		@Override
		public boolean hasNext(){
			if(token!=null)
				return true;
			for(Map.Entry<Pattern,Pair<String,Integer>> entry:types.get(status).entrySet()){
				Matcher matcher=entry.getKey().matcher(text);
				matcher.region(index,text.length());
				if(matcher.lookingAt()){
					token=new Token(matcher.group(),entry.getValue().getKey());
					index=matcher.end();
					status=entry.getValue().getValue();
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