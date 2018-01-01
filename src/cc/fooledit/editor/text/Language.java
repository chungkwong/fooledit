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
package cc.fooledit.editor.text;
import cc.fooledit.core.Helper;
import cc.fooledit.editor.text.parser.ParserBuilder;
import cc.fooledit.editor.text.lex.NaiveLexer;
import cc.fooledit.editor.text.lex.LexBuilders;
import cc.fooledit.editor.text.lex.LexerBuilder;
import cc.fooledit.*;
import cc.fooledit.util.*;
import com.github.chungkwong.json.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import org.antlr.v4.tool.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Language{
	private static final String NAME="name";
	private static final String MIME="mimes";
	private static final String HIGHLIGHTER="highlighter";
	private static final String PARSER="parser";
	private static final String SUPERTYPE="supertype";
	private static final String RULE="rule";
	private final String name;
	private final String[] mimeTypes;
	private final Cache<Highlighter> highlighter;
	private final Cache<ParserBuilder> parser;
	public Language(String name,String[] mimeTypes,Supplier<Highlighter> highlighter,Supplier<ParserBuilder> parser){
		this.name=name;
		this.mimeTypes=mimeTypes;
		this.highlighter=new Cache<>(highlighter);
		this.parser=new Cache<>(parser);
	}
	public Highlighter getTokenHighlighter(){
		return highlighter.get();
	}
	public ParserBuilder getParserBuilder(){
		return parser.get();
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
		Supplier<Highlighter> highlighter;
		Supplier<ParserBuilder> parser=()->null;
		String lexFileName=(String)obj.get(HIGHLIGHTER);
		if(lexFileName.endsWith(".json")){
			File lex=new File(Main.INSTANCE.getDataPath(),lexFileName);
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
			File superTypeFile=new File(Main.INSTANCE.getDataPath(),(String)obj.get(SUPERTYPE));
			Map<String,String> superType=new HashMap<>();
			try{
				superType.putAll((Map<String,String>)JSONDecoder.decode(Helper.readText(superTypeFile)));
			}catch(IOException|SyntaxException ex){
				Logger.getLogger(Language.class.getName()).log(Level.INFO,null,ex);
			}
			if(lexFileName.endsWith(".g4")){
				File lex=new File(Main.INSTANCE.getDataPath(),lexFileName);
				highlighter=()->{
					LexerBuilder lexer=LexerBuilder.wrap(Grammar.load(lex.getAbsolutePath()));
					return new AntlrHighlighter(lexer,superType);
				};
			}else{
				highlighter=()->{
					try{
						return new AntlrHighlighter(LexerBuilder.wrap(loadClass(lexFileName)),superType);
					}catch(MalformedURLException|ClassNotFoundException ex){
						Logger.getGlobal().log(Level.SEVERE,null,ex);
						return null;
					}
				};
			}
		}
		String parserFileName=(String)obj.get(PARSER);
		String rule=(String)obj.get(RULE);
		if(rule!=null&&parserFileName!=null){
			File p=new File(Main.INSTANCE.getDataPath(),parserFileName);
			if(parserFileName.endsWith(".g4")){
				parser=()->{
					return ParserBuilder.wrap(Grammar.load(p.getAbsolutePath()),rule);
				};
			}else{
				parser=()->{
					try{
						return ParserBuilder.wrap(loadClass(parserFileName),rule);
					}catch(MalformedURLException|ClassNotFoundException ex){
						Logger.getGlobal().log(Level.SEVERE,null,ex);
						return null;
					}
				};
			}
		}
		return new Language(name,mime,highlighter,parser);
	}
	private static <T> Class<T> loadClass(String location) throws ClassNotFoundException, MalformedURLException{
		int i=location.indexOf('!');
		String jar=location.substring(0,i);
		String cls=location.substring(i+1);
		return (Class<T>)new URLClassLoader(new URL[]{new File(Main.INSTANCE.getDataPath(),jar).toURI().toURL()}).loadClass(cls);
	}
}