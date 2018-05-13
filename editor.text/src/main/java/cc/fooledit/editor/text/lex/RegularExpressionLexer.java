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
package cc.fooledit.editor.text.lex;
import cc.fooledit.util.IntCheckPointIterator;
import cc.fooledit.util.Pair;
import cc.fooledit.editor.text.lex.NFA.State;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class RegularExpressionLexer implements MetaLexer{
	private final NFA machine=new NFA();
	private boolean changed=true;
	private final Map<Integer,State> states=new HashMap<Integer,State>();
	public RegularExpressionLexer(){
	}
	@Override
	public void addType(int status,String regex,String type,int newStatus){
		changed=true;
		NFA child=RegularExpression.parseRegularExpression(regex).toNFA();
		if(states.containsKey(status)){
			states.get(status).addLambdaTransition(child.getInitState());
		}else{
			machine.getInitState().addLambdaTransition(child.getInitState());
			states.put(status,child.getInitState());
		}
		child.getAcceptState().addLambdaTransition(machine.getAcceptState());
		child.getAcceptState().addLambdaTransition(new NFA.TaggedState(type,newStatus));
	}
	@Override
	public TokenIterator split(String text,int state,int begin){
		if(changed){
			machine.prepareForRun();
			changed=false;
		}
		return new RETokenIterator(new IntCheckPointIterator(text.codePoints().iterator()),state,begin);//FIXME
	}
	private class RETokenIterator implements TokenIterator{
		private final IntCheckPointIterator src;
		private int index;
		private int status;
		public RETokenIterator(IntCheckPointIterator src,int state,int begin){
			this.src=src;
			index=begin;
			status=state;
		}
		@Override
		public Token next(){
			Pair<NFA.StateSet,String> pair=machine.run(src,states.get(status));
			if(pair.getKey()!=null){
				NFA.TaggedState type=pair.getKey().getTaggedState();
				String text=pair.getValue();
				Token token=new Token(text,type.getTag(),index);
				index+=text.length();
				status=type.getId();
				return token;
			}else if(src.hasNext()){
				return new Token(new String(new int[]{src.nextInt()},0,1),MetaLexer.UNKNOWN,index++);
			}else
				return null;
		}
		@Override
		public boolean hasNext(){
			return src.hasNext();
		}
		@Override
		public int getState(){
			return status;
		}
	}
}