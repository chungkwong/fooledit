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
package com.github.chungkwong.jtk.editor.lex;
import com.github.chungkwong.json.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LexBuilder{
	public static void fromJSON(String json,Lex lex) throws IOException, SyntaxException{
		Map<String,Object> obj=(Map<String,Object>)JSONConvertor.fromJSON(json);
		Map<String,Number> states=(Map<String,Number>)obj.get("states");
		List<Map<String,Object>> rules=(List<Map<String,Object>>)obj.get("rules");
		for(Map<String,Object> rule:rules){
			String name=(String)rule.get("type");
			String regex=(String)rule.get("regex");
			Integer oldState=toState(rule.get("old state"),states);
			Integer newState=toState(rule.get("new state"),states);
			lex.addType(oldState,regex,name,newState);
		}
	}
	private static int toState(Object state,Map<String,Number> states){
		if(state==null)
			return Lex.INIT;
		else if(state instanceof Number)
			return ((Number)state).intValue();
		else
			return states.get((String)state).intValue();
	}
}
