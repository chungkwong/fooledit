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
package cc.fooledit.api;
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
public interface MimeGeusser{
	List<String> geuss(byte[] beginning);
	List<String> geuss(URL url);
	public static class URLPatternGeusser implements MimeGeusser{
		private final List<Pair<Predicate<String>,String>> pattern2mime=new ArrayList<>();
		public void registerPathPattern(String regex,String mime){
			registerPathPattern(Pattern.compile(regex).asPredicate(),mime);
		}
		public void registerPathPattern(Predicate<String> pred,String mime){
			pattern2mime.add(new Pair<>(pred,mime));
		}
		@Override
		public List<String> geuss(byte[] beginning){
			return Collections.emptyList();
		}
		@Override
		public List<String> geuss(URL url){
			String name=url.toString();
			List<String> candidates=pattern2mime.stream().filter((pair)->pair.getKey().test(name)).
					map(Pair::getValue).collect(Collectors.toList());
			return candidates;
		}
	}
	public static class SystemGeusser implements MimeGeusser{
		@Override
		public List<String> geuss(byte[] beginning){
			return Collections.emptyList();
		}
		@Override
		public List<String> geuss(URL url){
			try{
				String type=Files.probeContentType(new File(url.getFile()).toPath());
				if(type==null)
					return Collections.emptyList();
				else
					return Collections.singletonList(type);
			}catch(IOException ex){
				return Collections.emptyList();
			}
		}
	}
}
