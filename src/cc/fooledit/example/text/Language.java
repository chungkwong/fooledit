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
package cc.fooledit.example.text;
import cc.fooledit.api.*;
import cc.fooledit.editor.*;
import cc.fooledit.editor.lex.*;
import com.github.chungkwong.json.*;
import java.io.*;
import java.util.*;
import org.antlr.v4.tool.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Language{
	private static final String NAME="name";
	private static final String MIME="mime";
	private static final String HIGHLIGHTER="highlighter";
	private static final String SUPERTYPE="supertype";
	private final String name;
	private final String[] mimeTypes;
	private final TokenHighlighter highlighter;
	public Language(String name,String[] mimeTypes,TokenHighlighter highlighter){
		this.name=name;
		this.mimeTypes=mimeTypes;
		this.highlighter=highlighter;
	}
	public TokenHighlighter getTokenHighlighter(){
		return highlighter;
	}
	public String getName(){
		return name;
	}
	public String[] getMimeTypes(){
		return mimeTypes;
	}
	public String toJSON(){
		Map<String,Object> obj=new HashMap<>();
		obj.put(NAME,name);
		obj.put(MIME,Arrays.asList(mimeTypes));
		if(highlighter instanceof AntlrHighlighter){

		}else if(highlighter instanceof AdhokHighlighter){

		}
		obj.put(HIGHLIGHTER,name);
		return JSONEncoder.encode(obj);
	}
	public static Language fromJSON(String json) throws IOException,SyntaxException{
		Map<String,Object> obj=(Map<String,Object>)(Object)JSONDecoder.decode(json);
		String name=(String)obj.get(NAME);
		String[] mime=((List<String>)obj.get(MIME)).toArray(new String[0]);
		TokenHighlighter highlighter;
		String lex=(String)obj.get(HIGHLIGHTER);
		if(lex.endsWith(".g4")){
			highlighter=new AntlrHighlighter(LexerBuilder.wrap(Grammar.load(lex)),(Map<String,String>)obj.get(SUPERTYPE));
		}else if(lex.endsWith(".json")){
			NaiveLexer naiveLexer=new NaiveLexer();
			LexBuilders.fromJSON(Helper.readText(lex),naiveLexer);
			highlighter=new AdhokHighlighter(naiveLexer);
		}else{
			highlighter=null;
		}
		return new Language(name,mime,highlighter);
	}
}
