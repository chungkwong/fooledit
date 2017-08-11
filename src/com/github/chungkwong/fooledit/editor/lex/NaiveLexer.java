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
import com.github.chungkwong.fooledit.util.Pair;
import java.util.*;
import java.util.regex.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class NaiveLexer implements MetaLexer{
	private final Map<Integer,Map<Pattern,Pair<String,Integer>>> types=new HashMap<>();
	public NaiveLexer(){

	}
	@Override
	public void addType(int status,String regex,String type,int newStatus){
		Map<Pattern,Pair<String,Integer>> map=types.get(status);
		if(map==null){
			map=new LinkedHashMap<>();
			types.put(status,map);
		}
		map.put(Pattern.compile(regex,Pattern.DOTALL|Pattern.MULTILINE),new Pair<>(type,newStatus));
	}
	@Override
	public TokenIterator split(String text,int state,int begin){
		return new NaiveTokenIterator(text,state,begin);
	}
	private class NaiveTokenIterator implements TokenIterator{
		private final String text;
		private int index;
		private Token token;
		private int status;
		public NaiveTokenIterator(String text,int state,int begin){
			this.text=text;
			index=begin;
			status=state;
		}
		@Override
		public boolean hasNext(){
			if(token!=null)
				return true;
			int tmpIndex=index;
			for(Map.Entry<Pattern,Pair<String,Integer>> entry:types.get(status).entrySet()){
				Matcher matcher=entry.getKey().matcher(text);
				matcher.useTransparentBounds(true);
				matcher.region(index,text.length());
				if(matcher.lookingAt()&&(token==null||matcher.group().length()>token.getText().length())){
					token=new Token(matcher.group(),entry.getValue().getKey(),index);
					tmpIndex=matcher.end();
					status=entry.getValue().getValue();
				}
			}
			if(token!=null){
				index=tmpIndex;
				return true;
			}
			if(index<text.length()){
				token=new Token(text.substring(index,index+1),MetaLexer.UNKNOWN,index++);
				return true;
			}
			return false;
		}
		@Override
		public Token next(){
			if(hasNext()){
				Token tmp=token;
				//System.out.println(status+":"+token);
				token=null;
				return tmp;
			}else{
				throw new NoSuchElementException();
			}
		}
		@Override
		public int getState(){
			return status;
		}
	}
	public static void main(String[] args){
		Matcher matcher=Pattern.compile("ifndef|if").matcher("ifndef");
		if(matcher.lookingAt())
			System.err.println(matcher.end());
	}
}