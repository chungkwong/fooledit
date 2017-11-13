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
package cc.fooledit.spi;
import cc.fooledit.util.*;
import java.net.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ContentTypeRegistry{
	private static final ContentTypeRegistry INSTNACE=new ContentTypeRegistry();
	private static final List<Pair<Predicate<String>,Supplier<ContentHandler>>> wildcard=new LinkedList<>();
	private static final Map<String,String> SUBCLASSES=new HashMap<>();
	private static final Map<String,String> ALIASES=new HashMap<>();
	private ContentTypeRegistry(){

	}
	public static void registerWildcard(String regex,Supplier<ContentHandler> handler){
		wildcard.add(0,new Pair<>(Pattern.compile(regex).asPredicate(),handler));
	}
	public static ContentTypeRegistry get(){
		return INSTNACE;
	}
	public static void registerSubclass(String subclass,String parent){
		SUBCLASSES.put(subclass,parent);
	}
	public static boolean isSubclassOf(String type,String ancestor){
		type=normalize(type);
		ancestor=normalize(ancestor);
		while(type!=null){
			if(type.equals(ancestor))
				return true;
			type=normalize(SUBCLASSES.get(type));
		}
		return false;
	}
	public static void registerAlias(String alias,String standard){
		ALIASES.put(alias,standard);
	}
	public static String normalize(String type){
		return ALIASES.getOrDefault(type,type);
	}
	public static List<String> getAllSuperClasses(String type){
		ArrayList<String> list=new ArrayList<>();
		while(type!=null){
			type=normalize(type);
			list.add(type);
			type=SUBCLASSES.get(type);
		}
		return list;
	}
}
