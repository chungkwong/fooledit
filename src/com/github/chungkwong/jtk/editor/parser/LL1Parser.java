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
		Map<String,Set<String>> first=getFirst(rules,nullableSymbols);
		boolean changed=true;
		while(changed){
			changed=false;

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
	private Map<String,Set<String>> getFirst(List<ProductionRule> rules,Set<String> nullableSymbols){
		Map<String,Set<String>> targets=new HashMap<>();
		boolean changed=true;
		while(changed){
			changed=false;
			for(ProductionRule rule:rules){
				String target=rule.getTarget();
				Set<String> found=targets.get(target);
				if(found==null){
					found=new HashSet<>();
					targets.put(target,found);
				}
				for(String comp:rule.getMember()){
					if(terminals.containsKey(comp)){
						if(!found.contains(comp)){
							found.add(comp);
							changed=true;
						}
					}else{
						Set<String> sub=targets.get(comp);
						if(sub!=null&&!found.containsAll(sub)){
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
	private void getFollow(List<ProductionRule> rules,Set<String> nullableSymbols){
		
	}
	@Override
	public Object parse(Iterator<Token> iter){
		return parse(iter,start);
	}
	private Object parse(Iterator<Token> iter,String symbol){
		Token forward=iter.next();
		if(forward.getType().equals(symbol)){
			return terminals.get(symbol).apply(forward.getText());
		}
		ProductionRule rule=table.get(new Pair<>(symbol,forward.getType()));
		String[] member=rule.getMember();
		Object[] comp=new Object[member.length];
		for(int i=0;i<member.length;i++){
			comp[i]=parse(iter,member[i]);
		}
		return rule.getAction().apply(comp);
	}
}
