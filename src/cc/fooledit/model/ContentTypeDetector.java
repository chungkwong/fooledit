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
package cc.fooledit.model;
import cc.fooledit.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public interface ContentTypeDetector{
	enum State{LIKELY,POSSIBLE,IMPOSSIBLE}
	List<String> listAllPossible(URLConnection connection);
	State probe(URLConnection connection,String mime);
	public static class URLPatternGeusser implements ContentTypeDetector{
		private final List<Pair<Predicate<String>,String>> pattern2mime=new ArrayList<>();
		public void registerPathPattern(String regex,String mime){
			registerPathPattern(Pattern.compile(regex).asPredicate(),mime);
		}
		public void registerPathPattern(Predicate<String> pred,String mime){
			pattern2mime.add(new Pair<>(pred,mime));
		}
		@Override
		public List<String> listAllPossible(URLConnection connection){
			String name=connection.getURL().toString();
			List<String> candidates=pattern2mime.stream().filter((pair)->pair.getKey().test(name)).map(Pair::getValue).collect(Collectors.toList());
			return candidates;
		}
		@Override
		public State probe(URLConnection connection,String mime){
			String name=connection.getURL().toString();
			boolean possible=pattern2mime.stream().filter((pair)->pair.getKey().test(name)).map(Pair::getValue).allMatch((type)->Objects.equals(mime,type));
			return possible?State.LIKELY:State.POSSIBLE;
		}
	}
	public static class SystemGeusser implements ContentTypeDetector{
		@Override
		public List<String> listAllPossible(URLConnection connection){
			String guess=guess(connection);
			return guess==null?Collections.emptyList():Collections.singletonList(guess);
		}
		@Override
		public State probe(URLConnection connection,String mime){
			return Objects.equals(guess(connection),mime)?State.LIKELY:State.POSSIBLE;
		}
		private String guess(URLConnection connection){
			try{
				URL url=connection.getURL();
				String type;
				if(url.getProtocol().equals("file"))
					return Files.probeContentType(new File(url.toURI()).toPath());
				else
					return URLConnection.guessContentTypeFromName(url.getFile());
			}catch(IOException|URISyntaxException ex){
				return null;
			}
		}
	}
}
