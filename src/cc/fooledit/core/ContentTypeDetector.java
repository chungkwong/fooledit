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
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import javafx.collections.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public interface ContentTypeDetector{
	enum State{LIKELY,POSSIBLE,IMPOSSIBLE}
	List<String> listAllPossible(URLConnection connection);
	State probe(URLConnection connection,String mime);
	public static class URLPatternGuesser implements ContentTypeDetector,MapChangeListener<String,String>{
		private final List<Pair<Predicate<String>,String>> pattern2mime=new ArrayList<>();
		private boolean used=false;
		public void registerPathPattern(String regex,String mime){
			CoreModule.GLOB_REGISTRY.put(regex,mime);
		}
		public void registerPathPattern(Predicate<String> pred,String mime){
			pattern2mime.add(new Pair<>(pred,ContentTypeHelper.normalize(mime)));
		}
		@Override
		public List<String> listAllPossible(URLConnection connection){
			init();
			String name=getFile(connection);
			List<String> candidates=pattern2mime.stream().filter((pair)->pair.getKey().test(name)).map(Pair::getValue).collect(Collectors.toList());
			return candidates;
		}
		@Override
		public State probe(URLConnection connection,String mime){
			init();
			String name=getFile(connection);
			boolean possible=pattern2mime.stream().filter((pair)->pair.getKey().test(name)).map(Pair::getValue).allMatch((type)->Objects.equals(mime,type));
			return possible?State.LIKELY:State.POSSIBLE;
		}
		private void init(){
			if(!used){
				CoreModule.GLOB_REGISTRY.forEach((regex,mime)->addRegex(regex,mime));
				CoreModule.GLOB_REGISTRY.addListener(this);
				used=true;
			}
		}
		private void addRegex(String regex,String mime){
			pattern2mime.add(new Pair<>(Pattern.compile(regex).asPredicate(),ContentTypeHelper.normalize(mime)));
		}
		@Override
		public void onChanged(Change<? extends String,? extends String> change){
			if(change.wasAdded())
				addRegex(change.getKey(),change.getValueAdded());
			else if(change.wasRemoved())
				pattern2mime.remove(change.getKey());
		}
	}
	public static class SuffixGuesser implements ContentTypeDetector{
		public void registerSuffix(String suffix,String mime){
			MultiRegistryNode.addChildElement(suffix.toLowerCase(),ContentTypeHelper.normalize(mime),CoreModule.SUFFIX_REGISTRY);
		}
		@Override
		public List<String> listAllPossible(URLConnection connection){
			String name=getFile(connection);
			int delim=name.lastIndexOf('.');
			if(delim>0&&name.charAt(delim-1)!='/'&&name.charAt(delim-1)!='\\'){
				return MultiRegistryNode.getChildElements(name.substring(delim+1).toLowerCase(),CoreModule.SUFFIX_REGISTRY);
			}
			return Collections.emptyList();
		}
		@Override
		public State probe(URLConnection connection,String mime){
			return listAllPossible(connection).contains(mime)?State.LIKELY:State.POSSIBLE;
		}
	}
	public static class SystemGuesser implements ContentTypeDetector{
		@Override
		public List<String> listAllPossible(URLConnection connection){
			String guess=guess(connection);
			return guess==null?Collections.emptyList():Collections.singletonList(ContentTypeHelper.normalize(guess));
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
					return connection.getContentType();
			}catch(IOException|URISyntaxException ex){
				return null;
			}
		}
	}
	static String getFile(URLConnection connection){
		String file=connection.getHeaderField("content-name");
		return file!=null?file:connection.getURL().getFile();
	}
}
