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
package cc.fooledit.core;
import cc.fooledit.core.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ContentTypeHelper{
	private static final List<ContentTypeDetector> GUESSERS=new ArrayList<>();
	private static final ContentTypeDetector.URLPatternGuesser URL_GUESSER=new ContentTypeDetector.URLPatternGuesser();
	private static final ContentTypeDetector.SuffixGuesser SUFFIX_GUESSER=new ContentTypeDetector.SuffixGuesser();
	private static final ContentTypeDetector SYSTEM_GUESSERS=new ContentTypeDetector.SystemGuesser();
	public static List<ContentTypeDetector> getGUESSERS(){
		return GUESSERS;
	}
	public static ContentTypeDetector.URLPatternGuesser getURL_GUESSER(){
		return URL_GUESSER;
	}
	public static ContentTypeDetector.SuffixGuesser getSUFFIX_GUESSER(){
		return SUFFIX_GUESSER;
	}
	public static List<String> guess(URLConnection connection){
		List<String> types=Collections.emptyList();
		for(ContentTypeDetector detector:ContentTypeHelper.getGUESSERS()){
			if(types.isEmpty())
				types=detector.listAllPossible(connection);
			else{
				List<String> likely=new ArrayList<>();
				List<String> possible=new ArrayList<>();
				for(String type:types)
					switch(detector.probe(connection,type)){
						case LIKELY:
							likely.add(type);
							break;
						case POSSIBLE:
							possible.add(type);
							break;
					}
				if(!likely.isEmpty())
					types=likely;
				else if(!possible.isEmpty())
					types=possible;
			}
		}
		List<String> cand=types.stream().map((type)->getAllSuperClasses(type)).
				flatMap((parents)->parents.stream()).distinct().collect(Collectors.toList());
		return cand.isEmpty()?Collections.singletonList("application/octet-stream"):cand;
	}
	public static void main(String[] args) throws IOException{
		//System.out.println(new String(new byte[]{0,5,0}).length());
		//System.out.println(new URL("file:///home/kwong/icon.png").openConnection().getContentType());
		//System.out.println(new URL("file:///home/kwong/NetBeansProjects/JSchemeMin/doc/overview.pdf").openConnection().getContentType());
	}
	static{
		GUESSERS.add(URL_GUESSER);
		GUESSERS.add(SUFFIX_GUESSER);
		GUESSERS.add(SYSTEM_GUESSERS);
	}
	public static String normalize(String type){
		String con;
		while((con=CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.get(type))!=null){
			type=con;
		}
		return type;
	}
	public static boolean isSubclassOf(String type,String ancestor){
		type=normalize(type);
		ancestor=normalize(ancestor);
		while(type!=null){
			if(type.equals(ancestor)){
				return true;
			}
			type=normalize(CoreModule.CONTENT_TYPE_SUPERCLASS_REGISTRY.get(type));
		}
		return false;
	}
	public static List<String> getAllSuperClasses(String type){
		ArrayList<String> list=new ArrayList<>();
		while(type!=null){
			type=normalize(type);
			list.add(type);
			type=CoreModule.CONTENT_TYPE_SUPERCLASS_REGISTRY.get(type);
		}
		return list;
	}
}
