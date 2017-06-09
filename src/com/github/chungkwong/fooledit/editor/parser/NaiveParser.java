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
package com.github.chungkwong.fooledit.editor.parser;
import com.github.chungkwong.fooledit.util.MultiMap;
import com.github.chungkwong.fooledit.util.Pair;
import com.github.chungkwong.fooledit.editor.lex.Token;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class NaiveParser implements Parser{
	public static final boolean DEBUG=false;
	public static final ParserFactory FACTORY=(g)->new NaiveParser(g);
	private final ContextFreeGrammar grammar;
	private final boolean nullable;
	public NaiveParser(ContextFreeGrammar grammar){
		this.grammar=normalize(grammar);
		if(DEBUG)
			System.out.println(this.grammar);
		this.nullable=this.grammar.getRules().stream().anyMatch((rule)->rule.getMember().length==0);
	}
	private static ContextFreeGrammar normalize(ContextFreeGrammar grammar){
		String start="$0";
		List<ProductionRule> rules=new ArrayList<>(grammar.getRules());
		rules.add(new ProductionRule(start,new String[]{grammar.getStartSymbol()},(v)->v[0]));
		removeNullRules(rules);
		removeEqualRules(rules,grammar.getTerminals());
		rewriteRules(rules);
		return new ContextFreeGrammar(start,rules,grammar.getTerminals());
	}
	private static void removeNullRules(List<ProductionRule> rules){
		outer:while(true){
			Iterator<ProductionRule> iter=rules.iterator();
			while(iter.hasNext()){
				ProductionRule curr=iter.next();
				if(curr.getMember().length==0&&!curr.getTarget().equals("$0")){
					iter.remove();
					removeNullRule(curr,rules);
					continue outer;
				}
			}
			break;
		}
	}
	private static void removeNullRule(ProductionRule curr,List<ProductionRule> rules){
		String cand=curr.getTarget();
		for(int i=0;i<rules.size();i++){
			ProductionRule rule=rules.get(i);
			String[] comp=rule.getMember();
			for(int j=0;j<comp.length;j++){
				if(comp[j].equals(cand)){
					String[] reducedComp=arrayDelete(j,comp);
					int k=j;
					rules.add(new ProductionRule(rule.getTarget(),reducedComp,
							(a)->rule.getAction().apply(arrayInsert(k,curr.getAction().apply(EMPTY_ARRAY),a))));
				}
			}
		}
	}
	private static final Object[] EMPTY_ARRAY=new Object[0];
	private static String[] arrayDelete(int index,String[] array){
		String[] deleted=new String[array.length-1];
		System.arraycopy(array,0,deleted,0,index);
		System.arraycopy(array,index+1,deleted,index,array.length-index-1);
		return deleted;
	}
	private static Object[] arrayInsert(int index,Object element,Object[] array){
		Object[] inserted=new Object[array.length+1];
		System.arraycopy(array,0,inserted,0,index);
		inserted[index]=element;
		System.arraycopy(array,index,inserted,index+1,array.length-index);
		return inserted;
	}
	private static void removeEqualRules(List<ProductionRule> rules,Map<String,Function<String,Object>> terminals){
		MultiMap<String,String> pairs=new MultiMap<>();
		Map<Pair<String,String>,Function<Object,Object>> actions=new HashMap<>();
		Iterator<ProductionRule> iter=rules.iterator();
		while(iter.hasNext()){
			ProductionRule curr=iter.next();
			if(curr.getMember().length==1&&!terminals.containsKey(curr.getMember()[0])){
				iter.remove();
				String from=curr.getMember()[0],to=curr.getTarget();
				pairs.add(from,to);
				actions.put(new Pair<>(from,to),(o)->curr.getAction().apply(new Object[]{o}));
			}
		}
		boolean changed=true;
		while(changed){
			changed=false;
			for(Map.Entry<String,Set<String>> entry:pairs.getMap().entrySet()){
				Set<String> old=entry.getValue();
				Set<Pair<String,String>> cand=old.stream().map((m)->pairs.get(m).stream().map((t)->new Pair<>(m,t))).flatMap((s)->s).collect(Collectors.toSet());
				for(Pair<String,String> pair:cand){
					if(!old.contains(pair.getValue())){
						old.add(pair.getValue());
						Function<Object,Object> step2=actions.get(pair);
						Function<Object,Object> step1=actions.get(new Pair<>(entry.getKey(),pair.getKey()));
						actions.put(new Pair<>(entry.getKey(),pair.getValue()),step1.andThen(step2));
						changed=true;
					}
				}
			}
		}
		List<ProductionRule> newRules=rules.stream().map((rule)->pairs.get(rule.getTarget()).stream().
				map((t)->new ProductionRule(t,rule.getMember(),rule.getAction().andThen(actions.get(new Pair<>(rule.getTarget(),t)))))).
				flatMap((s)->s).collect(Collectors.toList());
		rules.addAll(newRules);
	}
	private static void rewriteRules(List<ProductionRule> rules){
		int count=0;
		ListIterator<ProductionRule> iter=rules.listIterator();
		while(iter.hasNext()){
			ProductionRule rule=iter.next();
			String[] comp=rule.getMember();
			int len=comp.length;
			if(len>2){
				iter.remove();
				String prev;
				String curr="$"+(++count);
				iter.add(new ProductionRule(rule.getTarget(),new String[]{comp[0],curr},(p)->{
					((Object[])p[1])[0]=p[0];
					return rule.getAction().apply((Object[])p[1]);
				}));
				for(int i=1;i<len-2;i++){
					prev=curr;
					curr="$"+(++count);
					int k=i;
					iter.add(new ProductionRule(prev,new String[]{comp[i],curr},(p)->{
						((Object[])p[1])[k]=p[0];
						return p[1];
					}));
				}
				iter.add(new ProductionRule(curr,new String[]{comp[len-2],comp[len-1]},(p)->{
					Object[] array=new Object[len];
					array[len-2]=p[0];
					array[len-1]=p[1];
					return array;
				}));
			}
		}
	}
	@Override
	public Object parse(Iterator<Token> iter){
		List<Token> tokens=new ArrayList<>();
		iter.forEachRemaining((t)->{
			if(grammar.getTerminals().containsKey(t.getType()))
				tokens.add(t);
		});
		int n=tokens.size();
		if(nullable&&n==0)
			return null;
		SymbolSet[][] table=new SymbolSet[n][n];
		for(int i=0;i<n;i++){
			String type=tokens.get(i).getType();
			Function<String,Object> convertor=grammar.getTerminals().get(type);
			Object val=convertor.apply(tokens.get(i).getText());
			addSymbolInstance(i,i,new SymbolInstance(type,val),table);
			for(ProductionRule rule:grammar.getRules())
				if(rule.getMember().length==1&&rule.getMember()[0].equals(type))
					addSymbolInstance(i,i,new SymbolInstance(rule.getTarget(),val),table);
		}
		if(DEBUG)
			printTable(table);
		for(int l=2;l<=n;l++){
			for(int i=0;i<=n-l;i++){
				int j=i+l-1;
				for(int k=i;k<j;k++)
					for(ProductionRule rule:grammar.getRules()){
						SymbolInstance first,second;
						if(rule.getMember().length==2&&(first=containsSymbol(i,k,rule.getMember()[0],table))!=null&&
								(second=containsSymbol(k+1,j,rule.getMember()[1],table))!=null){
							addSymbolInstance(i,j,rule.apply(first,second),table);
						}
					}
			}
			if(DEBUG)
				printTable(table);
		}
		return table[0][n-1].contains(grammar.getStartSymbol()).getSemanticValue();
	}
	private static void addSymbolInstance(int i,int j,SymbolInstance instance,SymbolSet[][] table){
		if(table[i][j]==null)
			table[i][j]=new Singleton(instance);
		else
			table[i][j]=table[i][j].addSymbol(instance);
	}
	private static SymbolInstance containsSymbol(int i,int j,String symbol,SymbolSet[][] table){
		if(table[i][j]==null)
			return null;
		else
			return table[i][j].contains(symbol);
	}
	private static void printTable(SymbolSet[][] table){
		int n=table.length;
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				System.out.print(table[i][j]);
				System.out.print('\t');
			}
			System.out.println();
		}
	}
	private interface SymbolSet{
		SymbolSet addSymbol(SymbolInstance s);
		SymbolInstance contains(String symbol);
	}
	private static class Singleton implements SymbolSet{
		private final SymbolInstance instance;
		public Singleton(SymbolInstance instance){
			this.instance=instance;
		}
		@Override
		public SymbolSet addSymbol(SymbolInstance s){
			return new Pack(instance).addSymbol(s);
		}
		@Override
		public SymbolInstance contains(String symbol){
			return instance.getSymbol().equals(symbol)?instance:null;
		}
		@Override
		public String toString(){
			return instance.toString();
		}
	}
	private static class Pack implements SymbolSet{
		private final List<SymbolInstance> instances=new LinkedList<>();
		public Pack(SymbolInstance instance){
			this.instances.add(instance);
		}
		@Override
		public SymbolSet addSymbol(SymbolInstance s){
			instances.add(s);
			return this;
		}
		@Override
		public SymbolInstance contains(String symbol){
			return instances.stream().filter((s)->s.getSymbol().equals(symbol)).findAny().orElse(null);
		}
		@Override
		public String toString(){
			return instances.toString();
		}
	}
}