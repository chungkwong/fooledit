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
package com.github.chungkwong.jtk.editor.parser;
import com.github.chungkwong.jtk.editor.lex.*;
import com.github.chungkwong.jtk.util.*;
import java.util.*;
import java.util.function.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LR1Parser implements Parser{
	private static final boolean DEBUG=false;
	public static final ParserFactory FACTORY=(g)->new LR1Parser(g);
	private final HashMap<String,Action>[] actions;
	private final HashMap<String,Integer>[] gotos;
	private final Map<String,Function<String,Object>> terminals;
	public LR1Parser(ContextFreeGrammar grammar){
		this.terminals=grammar.getTerminals();
		List<Set<Tuple<ProductionRule,Integer,String>>> items=getItems(grammar);
		int len=items.size();
		actions=new HashMap[len];
		gotos=new HashMap[len];
		for(int i=0;i<len;i++){
			actions[i]=new HashMap<>();
			for(Tuple<ProductionRule,Integer,String> item:items.get(i)){
				ProductionRule rule=item.getFirst();
				int pos=item.getSecond();
				String t=item.getThird();
				if(pos==rule.getMember().length){
					if(rule.getTarget().equals("$0")){
						actions[i].put("",new End());
					}else{
						actions[i].put(t,new Reduce(rule));
					}
				}else{
					String a=rule.getMember()[i];
					if(terminals.containsKey(a)){
						actions[i].put(a,new MoveIn(items.indexOf(getGotos(items.get(i),a))));//FIXME too slow
					}
				}
			}
			gotos[i]=new HashMap<>();//TODO

		}
	}
	public List<Set<Tuple<ProductionRule,Integer,String>>> getItems(ContextFreeGrammar grammar){
		List<Set<Tuple<ProductionRule,Integer,String>>> items=new ArrayList<>();
		Set<Tuple<ProductionRule,Integer,String>> init=new HashSet<>();
		init.add(new Tuple<>(new ProductionRule("$0",new String[]{grammar.getStartSymbol()},(o)->o[0]),0,""));
		toClosure(init);
		items.add(init);
		boolean changed=true;
		while(changed){
			changed=false;
			for(int i=0;i<items)
		}

		return items;
	}
	public List<Set<Tuple<ProductionRule,Integer,String>>> getGotos(Set<Tuple<ProductionRule,Integer,String>> set,String symbol){

	}
	public void toClosure(Set<Tuple<ProductionRule,Integer,String>> set){

	}

	@Override
	public Object parse(Iterator<Token> iter){
		DefaultIntList stack=new DefaultIntList();
		ArrayList<Object> symbols=new ArrayList<>();
		stack.add(0);
		Token token=nextToken(iter);
		while(true){
			Action action=actions[stack.top()].get(token.getType());
			if(action instanceof MoveIn){
				stack.push(((MoveIn)action).getState());
				symbols.add(terminals.get(token.getType()).apply(token.getText()));
				token=nextToken(iter);
			}else if(action instanceof Reduce){
				ProductionRule rule=((Reduce)action).getRule();
				stack.subList(stack.size()-rule.getMember().length,stack.size()).clear();
				stack.add(gotos[stack.top()].get(rule.getTarget()));
				List<Object> comp=symbols.subList(symbols.size()-rule.getMember().length,symbols.size());
				Object result=rule.getAction().apply(comp.toArray());
				comp.clear();
				symbols.add(result);
			}else if(action instanceof End){
				return symbols.get(0);
			}else{
				throw new RuntimeException();
			}
		}
	}
	private Token nextToken(Iterator<Token> iter){
		while(iter.hasNext()){
			Token curr=iter.next();
			if(terminals.containsKey(curr.getType()))
				return curr;
		}
		return new Token("","");
	}
	interface Action{}
	class MoveIn implements Action{
		private final int state;
		public MoveIn(int state){
			this.state=state;
		}
		public int getState(){
			return state;
		}
	}
	class Reduce implements Action{
		private final ProductionRule rule;
		public Reduce(ProductionRule rule){
			this.rule=rule;
		}
		public ProductionRule getRule(){
			return rule;
		}
	}
	class End implements Action{}
}
