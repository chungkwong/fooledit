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
import com.github.chungkwong.jtk.api.*;
import com.github.chungkwong.jtk.editor.lex.*;
import com.github.chungkwong.jtk.util.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LR1Parser implements Parser{
	private static final boolean DEBUG=true;
	public static final ParserFactory FACTORY=(g)->new LR1Parser(g);
	private final HashMap<String,Action>[] actions;
	private HashMap<String,Integer>[] gotos;
	private final Map<String,Function<String,Object>> terminals;
	public LR1Parser(ContextFreeGrammar grammar){
		this.terminals=grammar.getTerminals();
		List<Set<Tuple<ProductionRule,Integer,String>>> items=getItems(grammar);
		int len=items.size();
		actions=new HashMap[len];
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
						actions[i].put(a,new MoveIn(gotos[i].get(a)));
					}
				}
			}
		}
		if(DEBUG)
			printTable();
	}
	private void printTable(){
		for(int i=0;i<actions.length;i++){
			System.err.print(i);
			System.err.print(actions[i]);
			System.err.println(gotos[i]);
		}
	}
	private List<Set<Tuple<ProductionRule,Integer,String>>> getItems(ContextFreeGrammar grammar){
		Set<String> nullable=LL1Parser.getNullableSymbols(grammar.getRules());
		MultiMap<String,String> first=LL1Parser.getFirst(grammar.getRules(),nullable,terminals);
		List<Set<Tuple<ProductionRule,Integer,String>>> items=new ArrayList<>();
		List<HashMap<String,Integer>> gotos0=new ArrayList<>();
		Set<Tuple<ProductionRule,Integer,String>> init=new HashSet<>();
		init.add(new Tuple<>(new ProductionRule("$0",new String[]{grammar.getStartSymbol()},(o)->o[0]),0,""));
		searchItems(init,items,gotos0,nullable,first,grammar.getRules());
		gotos=gotos0.toArray(new HashMap[0]);//can shrink
		return items;
	}
	private void searchItems(Set<Tuple<ProductionRule,Integer,String>> start,List<Set<Tuple<ProductionRule,Integer,String>>> items,
			List<HashMap<String,Integer>> gotos0,Set<String> nullable,MultiMap<String,String> first,List<ProductionRule> rules){
		start=toClosure(start,nullable,first,rules);
		items.add(start);
		HashMap<String,Integer> next=new HashMap<>();
		gotos0.add(next);
		Map<String,Set<Tuple<ProductionRule,Integer,String>>> group=start.stream().
				filter((entry)->entry.getSecond()<entry.getFirst().getMember().length).
				map((entry)->new Tuple<>(entry.getFirst(),entry.getSecond()+1,entry.getThird())).
				collect(Collectors.groupingBy((entry)->entry.getFirst().getMember()[entry.getSecond()-1],Collectors.toSet()));
		for(Map.Entry<String,Set<Tuple<ProductionRule,Integer,String>>> entry:group.entrySet()){
			next.put(entry.getKey(),items.size());
			searchItems(entry.getValue(),items,gotos0,nullable,first,rules);
		}
	}
	public Set<Tuple<ProductionRule,Integer,String>> toClosure(Set<Tuple<ProductionRule,Integer,String>> set,
			Set<String> nullable,MultiMap<String,String> first,List<ProductionRule> rules){
		Set<Tuple<ProductionRule,Integer,String>> closure=new HashSet<>();
		ArrayList<Tuple<ProductionRule,Integer,String>> cand=new ArrayList<>(set);
		while(closure.addAll(cand)){
			cand.clear();
			for(Tuple<ProductionRule,Integer,String> entry:set){
				String[] members=entry.getFirst().getMember();
				if(entry.getSecond()>=members.length)
					continue;
				String target=members[entry.getSecond()];
				findFirst:for(ProductionRule rule:rules){
					if(rule.getTarget().equals(target)){
						for(int i=entry.getSecond()+1;i<members.length;i++){
							String symbol=members[i];
							for(String lookahead:first.get(symbol))
								cand.add(new Tuple<>(rule,0,lookahead));
							if(!nullable.contains(symbol))
								continue findFirst;
						}
						cand.add(new Tuple<>(rule,0,entry.getThird()));
					}
				}
			}
		}
		return closure;
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
		@Override
		public String toString(){
			return "m"+state;
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
		@Override
		public String toString(){
			return "r"+rule;
		}
	}
	class End implements Action{
		@Override
		public String toString(){
			return "acc";
		}
	}
	public static void main(String[] args){
		String start="START";
		String word="WORD",number="NUMBER",other="OTHER";
		ArrayList<ProductionRule> rules=new ArrayList<>();
		rules.add(new ProductionRule(start,new String[]{word,other,number},
				(a)->a[0].toString().substring(Integer.parseInt(a[2].toString()))));
		ContextFreeGrammar cfg=new ContextFreeGrammar(start,rules,Helper.hashMap(
				word,Function.identity(),number,Function.identity(),other,Function.identity()));
		Parser parser=new LR1Parser(cfg);
		RegularExpressionLex lex=new RegularExpressionLex();
		lex.addType(Lex.INIT,"[0-9]+","NUMBER",Lex.INIT);
		lex.addType(Lex.INIT,"[a-zA-Z]+","WORD",Lex.INIT);
		lex.addType(Lex.INIT,"[^0-9a-zA-Z]","OTHER",Lex.INIT);
		System.out.println(parser.parse(lex.split("abcd-2")));
	}
}
