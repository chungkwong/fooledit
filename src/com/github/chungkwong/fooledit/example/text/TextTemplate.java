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
package com.github.chungkwong.fooledit.example.text;
import com.github.chungkwong.fooledit.*;
import com.github.chungkwong.fooledit.api.*;
import com.github.chungkwong.fooledit.model.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TextTemplate implements Template<TextObject>{
	private final String name;
	private final String description;
	private final String file;
	private final String mime;
	public TextTemplate(String name,String description,String file,String mime){
		this.name=name;
		this.description=description;
		this.file=file;
		this.mime=mime;
	}
	@Override
	public String getName(){
		return name;
	}
	@Override
	public String getDescription(){
		return description;
	}
	public String getMimeType(){
		return mime;
	}
	@Override
	public Collection<String> getParameters(){
		try{
			String text=Helper.readText(Main.getFile(file,"code-editor"));
			Set<String> parameters=new HashSet<>();
			int pos=0;
			while((pos=text.indexOf('$',pos))!=-1){
				if(text.charAt(pos+1)=='$'){
					++pos;
				}else{
					int newpos=text.indexOf('}',pos);
					parameters.add(text.substring(pos+2,newpos));
					pos=newpos;
				}
			}
			return parameters;
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
		return Collections.emptySet();
	}
	@Override
	public TextObject apply(Properties properties){
		String text;
		try{
			text=Helper.readText(Main.getFile(file,"code-editor"));
		}catch(IOException ex){
			Logger.getLogger(TextTemplate.class.getName()).log(Level.INFO,null,ex);
			text="";
		}
		StringBuilder buf=new StringBuilder();
		for(int i=0;i<text.length();i++){
			char c=text.charAt(i);
			if(c=='$'){
				if(text.charAt(i+1)=='$'){
					++i;
					buf.append('$');
				}else{
					int j=text.indexOf('}',i);
					String key=text.substring(i+2,j);
					buf.append(properties.getProperty(key,key));
					i=j;
				}
			}else{
				buf.append(c);
			}
		}
		return new TextObject(text);
	}
}
