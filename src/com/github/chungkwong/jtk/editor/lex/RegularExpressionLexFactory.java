/*
 * Copyright (C) 2016 Chan Chung Kwong <1m02math@126.com>
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
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class RegularExpressionLexFactory implements Lex{
	private final NFA machine=new NFA();
	private boolean changed=true;
	public RegularExpressionLexFactory(){
	}
	@Override
	public void addType(int status,String regex,String type,int newStatus){
		changed=true;
		NFA child=RegularExpression.parseRegularExpression(regex).toNFA();
		machine.getInitState().addLambdaTransition(child.getInitState());
		child.getAcceptState().addLambdaTransition(machine.getAcceptState());
		child.getAcceptState().addLambdaTransition(new NFA.TaggedState(type));
	}
	@Override
	public Iterator<Token> split(String text){
		if(changed){
			machine.prepareForRun();
			changed=false;
		}
		return new RegularExpressionLex(new IntCheckPointIterator(text.codePoints().iterator()));//FIXME
	}
	private class RegularExpressionLex implements Iterator<Token>{
		private final IntCheckPointIterator src;
		public RegularExpressionLex(IntCheckPointIterator src){
			this.src=src;
		}
		@Override
		public Token next(){
			Pair<NFA.StateSet,String> pair=machine.run(src);
			if(pair.getKey()!=null){
				String type=pair.getKey().getTag();
				String text=pair.getValue();
				return new Token(text,type);
			}else
				return null;
		}
		@Override
		public boolean hasNext(){
			return src.hasNext();
		}
	}
	/*public static void main(String[] args){
		RegularExpressionLexFactory factory=new RegularExpressionLexFactory();
		factory.addTokenType("NUMBER","[0-9]+");
		factory.addTokenType("WORD","[a-zA-Z]+");
		factory.addTokenType("OTHER","[^0-9a-zA-Z]");
		Lex lex=factory.createLex(new IntCheckPointIterator("fe2672j-=".codePoints().iterator()));
		Token t;
		while(lex.hasNext()){
			System.out.println(lex.next());
		}
	}*/
}