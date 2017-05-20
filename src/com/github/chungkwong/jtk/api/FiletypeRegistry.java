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
import com.github.chungkwong.jtk.util.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FiletypeRegistry{
	private static final TreeMap<String,List<String>> ext2mime=new TreeMap<>();
	private static final List<Pair<Pattern,String>> pattern2mime=new ArrayList<>();
	public static void registerPathPattern(String regex,String mime){
		pattern2mime.add(new Pair<>(Pattern.compile(regex),mime));
	}
	public static String probeMimeType(Path path){
		return null;
	}
}
