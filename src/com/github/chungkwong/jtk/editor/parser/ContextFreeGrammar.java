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
package com.github.chungkwong.jtk.editor.parser;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ContextFreeGrammar{
	private final String startSymbol;
	private final List<ProductionRule> rules;
	private final Map<String,Function<String,Object>> terminals;
	public ContextFreeGrammar(String startSymbol,List<ProductionRule> rules,Map<String,Function<String,Object>> terminals){
		this.startSymbol=startSymbol;
		this.rules=rules;
		this.terminals=terminals;
	}
	public String getStartSymbol(){
		return startSymbol;
	}
	public List<ProductionRule> getRules(){
		return rules;
	}
	public Map<String,Function<String,Object>> getTerminals(){
		return terminals;
	}
	@Override
	public String toString(){
		return rules.stream().map(ProductionRule::toString).collect(Collectors.joining("\n",startSymbol+"\n",""));
	}
}
