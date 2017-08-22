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
import cc.fooledit.*;
import cc.fooledit.api.*;
import cc.fooledit.editor.*;
import cc.fooledit.editor.lex.*;
import cc.fooledit.util.*;
import com.github.chungkwong.json.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.tool.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Language{
	private static final String NAME="name";
	private static final String MIME="mimes";
	private static final String HIGHLIGHTER="highlighter";
	private static final String SUPERTYPE="supertype";
	private final String name;
	private final String[] mimeTypes;
	private final Cache<TokenHighlighter> highlighter;
	public Language(String name,String[] mimeTypes,Supplier<TokenHighlighter> highlighter){
		this.name=name;
		this.mimeTypes=mimeTypes;
		this.highlighter=new Cache<>(highlighter);
	}
	public TokenHighlighter getTokenHighlighter(){
		return highlighter.get();
	}
	public String getName(){
		return name;
	}
	public String[] getMimeTypes(){
		return mimeTypes;
	}
	public static Language fromJSON(Map<String,Object> obj){
		String name=(String)obj.get(NAME);
		String[] mime=((List<String>)obj.get(MIME)).toArray(new String[0]);
		Supplier<TokenHighlighter> highlighter;
		String lexFileName=(String)obj.get(HIGHLIGHTER);
		if(lexFileName.endsWith(".g4")){
			File lex=new File(Main.getDataPath(),lexFileName);
			File superType=new File(Main.getDataPath(),(String)obj.get(SUPERTYPE));
			highlighter=()->{
				try{
					return new AntlrHighlighter(LexerBuilder.wrap(Grammar.load(lex.getAbsolutePath())),(Map<String,String>)JSONDecoder.decode(Helper.readText(superType)));
				}catch(IOException|SyntaxException ex){
					return new AntlrHighlighter(LexerBuilder.wrap(Grammar.load(lex.getAbsolutePath())),Collections.emptyMap());
				}
			};
		}else if(lexFileName.endsWith(".json")){
			File lex=new File(Main.getDataPath(),lexFileName);
			highlighter=()->{
				NaiveLexer naiveLexer=new NaiveLexer();
				try{
					LexBuilders.fromJSON(Helper.readText(lex),naiveLexer);
					return new AdhokHighlighter(naiveLexer);
				}catch(IOException|SyntaxException ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
					return null;
				}
			};
		}else{
			int i=lexFileName.indexOf('!');
			String jar=lexFileName.substring(0,i);
			String cls=lexFileName.substring(i+1);
			highlighter=()->{
				try{
					return new AntlrHighlighter(LexerBuilder.wrap((Class<Lexer>)new URLClassLoader(new URL[]{new File(Main.getDataPath(),jar).toURI().toURL()}).loadClass(cls)));
				}catch(MalformedURLException|ClassNotFoundException ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
					return null;
				}
			};
		}
		return new Language(name,mime,highlighter);
	}
}