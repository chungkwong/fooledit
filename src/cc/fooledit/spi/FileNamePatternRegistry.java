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
public class FileNamePatternRegistry implements FileNameMap{
	private static final FileNamePatternRegistry INSTNACE=new FileNamePatternRegistry();
	private static final Map<String,String> suffices=new HashMap<>();
	private static final List<Pair<Predicate<String>,String>> wildcard=new LinkedList<>();
	private FileNamePatternRegistry(){

	}
	public static void register(String suffix,String type){
		suffices.put(suffix,type);
	}
	public static void registerWildcard(String regex,String type){
		wildcard.add(0,new Pair<>(Pattern.compile(regex).asPredicate(),type));
	}
	public static FileNamePatternRegistry get(){
		return INSTNACE;
	}
	@Override
	public String getContentTypeFor(String fileName){
		int delim=fileName.lastIndexOf('.');
		if(delim>0){
			String type=suffices.get(fileName.substring(delim+1));
			if(type!=null)
				return type;
		}
		Optional<String> type=wildcard.stream().filter((entry)->entry.getKey().test(fileName)).findFirst().map((entry)->entry.getValue());
		return type.orElse("application/octet-stream");
	}
}
