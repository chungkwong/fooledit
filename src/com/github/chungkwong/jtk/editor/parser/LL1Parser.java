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
	public static final ParserFactory FACTORY=(g)->new LL1Parser(g);
	private final String start;
	private final Map<Pair<String,String>,ProductionRule> table=new HashMap<>();
	private final Map<String,Function<String,Object>> terminals;
	public LL1Parser(ContextFreeGrammar grammar){
		this.start=grammar.getStartSymbol();
		this.terminals=grammar.getTerminals();
		List<ProductionRule> rules=grammar.getRules();
		Set<String> nullableSymbols=getNullableSymbols(rules);
		MultiMap<String,String> first=getFirst(rules,nullableSymbols);
		MultiMap<String,String> follow=getFollow(rules,nullableSymbols,first);
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
	private Set<String> getNullableSymbols(List<ProductionRule> rules){
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
		return set;
	}
	private MultiMap<String,String> getFirst(List<ProductionRule> rules,Set<String> nullableSymbols){
		MultiMap<String,String> targets=new MultiMap<>();
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
		return targets;
	}
	private MultiMap<String,String> getFollow(List<ProductionRule> rules,Set<String> nullableSymbols,MultiMap<String,String> first){
		MultiMap<String,String> targets=new MultiMap<>();
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
					changed|=targets.add(rule.getTarget(),targets.get(comp[i]));
					if(!nullableSymbols.contains(comp[i]))
						break;
				}
			}
		}
		return targets;
	}
	@Override
	public Object parse(Iterator<Token> iter){
		return parse(iter,start).getKey();
	}
	private Pair<Object,Token> parse(Iterator<Token> iter,String symbol){
		return parse(iter,symbol,iter.next());
	}
	private Pair<Object,Token> parse(Iterator<Token> iter,String symbol,Token forward){
		if(forward.getType().equals(symbol)){
			return new Pair<>(terminals.get(symbol).apply(forward.getText()),null);
		}
		ProductionRule rule=table.get(new Pair<>(symbol,forward.getType()));
		String[] member=rule.getMember();
		Object[] comp=new Object[member.length];
		for(int i=0;i<member.length;i++){
			Pair<Object,Token> tmp=forward==null?parse(iter,symbol):parse(iter,member[i],forward);
			comp[i]=tmp.getKey();
			forward=tmp.getValue();
		}
		return new Pair<>(rule.getAction().apply(comp),forward);
	}
}
class MultiMap<K,V>{
	private final Map<K,Set<V>> map=new HashMap<>();
	public boolean add(K key,Set<V> values){
		Set<V> set=get(key);
		if(set.containsAll(values)){
			return false;
		}else{
			set.addAll(values);
			return true;
		}
	}
	public boolean add(K key,V values){
		Set<V> set=get(key);
		if(set.contains(values)){
			return false;
		}else{
			set.add(values);
			return true;
		}
	}
	public Set<V> get(K key){
		Set<V> set=map.get(key);
		if(set==null){
			set=new HashSet<>();
			map.put(key,set);
		}
		return set;
	}
	public Map<K,Set<V>> getMap(){
		return map;
	}
}
