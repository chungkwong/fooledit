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
public class LL1Parser implements Parser{
	private static final boolean DEBUG=false;
	public static final ParserFactory FACTORY=(g)->new LL1Parser(g);
	private final String start;
	private final Map<Pair<String,String>,ProductionRule> table=new HashMap<>();
	private final Map<String,Function<String,Object>> terminals;
	public LL1Parser(ContextFreeGrammar grammar){
		this.start=grammar.getStartSymbol();
		this.terminals=grammar.getTerminals();
		List<ProductionRule> rules=grammar.getRules();
		Set<String> nullableSymbols=getNullableSymbols(rules);
		MultiMap<String,String> first=getFirst(rules,nullableSymbols,terminals);
		MultiMap<String,String> follow=getFollow(rules,start,nullableSymbols,first);
		for(ProductionRule rule:rules){
			String[] comp=rule.getMember();
			for(int i=0;i<comp.length;i++){
				first.get(comp[i]).stream().forEach(t->addItem(t,rule));
				if(!nullableSymbols.contains(comp[i]))
					break;
			}
			if(nullableSymbols.containsAll(Arrays.asList(comp))){
				follow.get(rule.getTarget()).stream().forEach(t->addItem(t,rule));
			}
		}
	}
	private void addItem(String terminal,ProductionRule rule){
		ProductionRule old=table.put(new Pair<>(rule.getTarget(),terminal),rule);
		if(old!=null){
			throw new RuntimeException();
		}
	}
	static Set<String> getNullableSymbols(List<ProductionRule> rules){
		Set<String> set=new HashSet<>();
		rules.stream().filter((rule)->rule.getMember().length==0).forEach((rule)->set.add(rule.getTarget()));//Not really needed
		boolean changed=true;
		while(changed){
			changed=false;
			for(ProductionRule rule:rules){
				if(!set.contains(rule.getTarget())&&set.containsAll(Arrays.asList(rule.getMember()))){
					changed=true;
					set.add(rule.getTarget());
				}
			}
		}
		if(DEBUG){
			System.err.println("nullable:"+set);
		}
		return set;
	}
	static MultiMap<String,String> getFirst(List<ProductionRule> rules,Set<String> nullableSymbols,Map<String,Function<String,Object>> terminals){
		MultiMap<String,String> targets=new MultiMap<>();
		terminals.keySet().forEach((t)->targets.add(t,t));
		boolean changed=true;
		while(changed){
			changed=false;
			for(ProductionRule rule:rules){
				String target=rule.getTarget();
				Set<String> found=targets.get(target);
				for(String comp:rule.getMember()){
					if(terminals.containsKey(comp)){
						if(!found.contains(comp)){
							found.add(comp);
							changed=true;
						}
					}else{
						Set<String> sub=targets.get(comp);
						if(!found.containsAll(sub)){
							found.addAll(sub);
							changed=true;
						}
					}
					if(!nullableSymbols.contains(comp)){
						break;
					}
				}
			}
		}
		if(DEBUG)
			System.err.println("first:"+targets.getMap());
		return targets;
	}
	static MultiMap<String,String> getFollow(List<ProductionRule> rules,String start,Set<String> nullableSymbols,MultiMap<String,String> first){
		MultiMap<String,String> targets=new MultiMap<>();
		targets.get(start).add("");
		for(ProductionRule rule:rules){
			String[] comp=rule.getMember();
			for(int i=1;i<comp.length;i++){
				Set<String> follow=targets.get(comp[i-1]);
				for(int j=i;j<comp.length;j++){
					follow.addAll(first.get(comp[j]));
					if(!nullableSymbols.contains(comp[j]))
						break;
				}
			}
		}
		boolean changed=true;
		while(changed){
			changed=false;
			for(ProductionRule rule:rules){
				String[] comp=rule.getMember();
				for(int i=comp.length-1;i>=0;i--){
					changed|=targets.add(comp[i],targets.get(rule.getTarget()));
					if(!nullableSymbols.contains(comp[i]))
						break;
				}
			}
		}
		if(DEBUG)
			System.err.println("follow:"+targets.getMap());
		return targets;
	}
	@Override
	public Object parse(Iterator<Token> iter){
		return parse(iter,start).getKey();
	}
	private Pair<Object,Token> parse(Iterator<Token> iter,String symbol){
		Token tmp=null;
		while(iter.hasNext()){
			tmp=iter.next();
			if(terminals.containsKey(tmp.getType())){
				return parse(iter,symbol,tmp);
			}
		}
		return parse(iter,symbol,new Token("",""));
	}
	private Pair<Object,Token> parse(Iterator<Token> iter,String symbol,Token forward){
		if(forward.getType().equals(symbol)){
			return new Pair<>(terminals.get(symbol).apply(forward.getText()),null);
		}
		if(DEBUG){
			System.err.println(symbol+"-"+forward);
		}
		ProductionRule rule=table.get(new Pair<>(symbol,forward.getType()));
		String[] member=rule.getMember();
		Object[] comp=new Object[member.length];
		for(int i=0;i<member.length;i++){
			Pair<Object,Token> tmp=forward==null?parse(iter,member[i]):parse(iter,member[i],forward);
			comp[i]=tmp.getKey();
			forward=tmp.getValue();
		}
		return new Pair<>(rule.getAction().apply(comp),forward);
	}
}
