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
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DFA{
	private static final State FAILED=new State();
	private final State init;
	public DFA(State init){
		this.init=init;
	}
	public Token run(IntCheckPointIterator input){
		return run(input,init);
	}
	public Token run(IntCheckPointIterator input,State start){
		State curr=start;
		AcceptedState lastAccept=null;
		StringBuilder buf=new StringBuilder();
		input.startPreread();
		while(curr!=FAILED&&input.hasNext()){
			curr=curr.nextState(input.nextInt());
			if(curr instanceof AcceptedState){
				for(int c:input.endPrereadForward())
					buf.appendCodePoint(c);
				lastAccept=(AcceptedState)curr;
				input.startPreread();
			}
		}
		input.endPrereadBackward();
		String text=buf.toString();
		return lastAccept!=null?new Token(text,lastAccept.getTokenType(),0):null;
	}
	public boolean isAccepted(IntCheckPointIterator input){
		return run(input)!=null&&!input.hasNext();
	}
	public boolean isAccepted(IntCheckPointIterator input,State start){
		return run(input,start)!=null&&!input.hasNext();
	}
	public DFA toMinimizedDFA(){
		return this;
	}
	public static class State{
		private List<Pair<CharacterSet,State>> transitionTable=new LinkedList<>();
		public State(){

		}
		public void addTransition(CharacterSet set,State next,boolean checkOverlap){
			if(checkOverlap&&transitionTable.stream().anyMatch((pair)->
					CharacterSetFactory.createIntersectionCharacterSet(pair.getKey(),set).stream().findAny().isPresent())){
				throw new AmbiguousException();
			}
			transitionTable.add(new Pair<>(set,next));
		}
		public State nextState(int codePoint){
			Optional<Pair<CharacterSet,State>> found=transitionTable.stream().filter((pair)->pair.getKey().contains(codePoint)).findFirst();
			return found.isPresent()?found.get().getValue():FAILED;
		}
	}
	public static class AcceptedState extends State{
		private final String tokenType;
		public AcceptedState(String tokenType){
			this.tokenType=tokenType;
		}
		public String getTokenType(){
			return tokenType;
		}
	}
}