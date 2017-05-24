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
package com.github.chungkwong.jtk.api;
import java.io.*;
import java.nio.file.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FiletypeRegistry{
	/*private static final List<Pair<Predicate<String>,String>> pattern2mime=new ArrayList<>();
	public static void registerPathPattern(String regex,String mime){
		registerPathPattern(Pattern.compile(regex).asPredicate(),mime);
	}
	public static void registerPathPattern(Predicate<String> pred,String mime){
		pattern2mime.add(new Pair<>(pred,mime));
	}
	public static List<String> probeMimeType(Path path){
		String name=path.toString();
		List<String> candidates=pattern2mime.stream().filter((pair)->pair.getKey().test(name)).
				map(Pair::getValue).collect(Collectors.toList());
		return candidates;
	}*/
	public static void main(String[] args) throws IOException{
		System.out.println(Files.probeContentType(new File("/home/kwong/print").toPath()));
	}
}
