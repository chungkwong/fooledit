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
package com.github.chungkwong.jtk.util;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MimeType{
	private final String type,subtype;
	private final Map<String,String> parameters;
	public MimeType(String type,String subtype,Map<String,String> parameters){
		this.type=type.toLowerCase();
		this.subtype=subtype.toLowerCase();
		this.parameters=parameters;
	}
	public static MimeType fromString(String text){
		int subTypeStart=text.indexOf('/');
		String type=text.substring(0,subTypeStart).trim();
		int parameterStart=text.indexOf(';',subTypeStart);
		if(parameterStart==-1)
			parameterStart=text.length();
		String subtype=text.substring(subTypeStart+1,parameterStart).trim();
		Map<String,String> parameters=new HashMap<>();
		int valueStart=text.indexOf('=',parameterStart);
		while(valueStart!=-1){
			String key=text.substring(parameterStart,valueStart).trim().toLowerCase();
			int index=valueStart+1;
			while(Character.isWhitespace(text.charAt(index))){
				++index;
			}
			String value;
			if(text.charAt(index)=='"'){
				StringBuilder buf=new StringBuilder();
				while(true){
					char c=text.charAt(index++);
					if(c!='"')
						buf.append(c);
					else if(index<text.length()&&text.charAt(index)=='"'){
						++index;
						buf.append(c);
					}else{
						break;
					}
				}
				value=buf.toString();
				parameterStart=text.indexOf(';',index);
			}else{
				parameterStart=text.indexOf(';',index);
				value=text.substring(index,parameterStart).trim();
			}
			parameters.put(key,value);
			valueStart=parameterStart==-1?-1:text.indexOf('=',parameterStart);
		}
		return new MimeType(type,subtype,parameters);
	}
	@Override
	public String toString(){
		StringBuilder buf=new StringBuilder();
		buf.append(type).append('/').append(subtype);
		parameters.forEach((k,v)->buf.append(";").append(k).append('=').append(v));
		return buf.toString();
	}

	public String getType(){
		return type;
	}
	public String getSubtype(){
		return subtype;
	}
	public Map<String,String> getParameters(){
		return parameters;
	}
}
