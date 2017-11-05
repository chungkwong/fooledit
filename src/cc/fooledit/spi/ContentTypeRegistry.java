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
public class ContentTypeRegistry implements ContentHandlerFactory{
	private static final ContentTypeRegistry INSTNACE=new ContentTypeRegistry();
	private static final Map<String,Supplier<ContentHandler>> registry=new HashMap<>();
	private static final List<Pair<Predicate<String>,Supplier<ContentHandler>>> wildcard=new LinkedList<>();
	private ContentTypeRegistry(){

	}
	public static void registry(String mimetype,Supplier<ContentHandler> handler){
		registry.put(mimetype,handler);
	}
	public static void registryWildcard(String regex,Supplier<ContentHandler> handler){
		wildcard.add(0,new Pair<>(Pattern.compile(regex).asPredicate(),handler));
	}
	public static ContentTypeRegistry get(){
		return INSTNACE;
	}
	@Override
	public ContentHandler createContentHandler(String mimetype){
		Supplier<ContentHandler> handler=registry.get(mimetype);
		if(handler!=null)
			return handler.get();
		return wildcard.stream().filter((entry)->entry.getKey().test(mimetype)).findFirst().get().getValue().get();
	}
}
