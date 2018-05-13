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
import cc.fooledit.editor.text.parser.ParserBuilder;
import cc.fooledit.util.*;
import java.util.function.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Language{
	private final String mimeType;
	private final Cache<Highlighter> highlighter;
	private final Cache<ParserBuilder> parser;
	public Language(String mimeType,Supplier<Highlighter> highlighter,Supplier<ParserBuilder> parser){
		this.mimeType=mimeType;
		this.highlighter=new Cache<>(highlighter);
		this.parser=new Cache<>(parser);
	}
	public Highlighter getTokenHighlighter(){
		return highlighter.get();
	}
	public ParserBuilder getParserBuilder(){
		return parser.get();
	}
	public String getMimeTypes(){
		return mimeType;
	}
}