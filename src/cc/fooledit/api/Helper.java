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
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Helper{
	public static HashMap hashMap(Object... val){
		HashMap map=new HashMap();
		for(int i=0;i<val.length;i+=2)
			map.put(val[i],val[i+1]);
		return map;
	}
	public static String readText(String resource){
		return readText(new InputStreamReader(Helper.class.getResourceAsStream(resource),StandardCharsets.UTF_8));
	}
	public static String readText(Reader in){
		return new BufferedReader(in).lines().collect(Collectors.joining("\n"));
	}
	public static String readText(File file) throws IOException{
		return Files.readAllLines(file.toPath()).stream().collect(Collectors.joining("\n"));
	}
	public static void writeText(String text,File file) throws IOException{
		Files.write(file.toPath(),text.getBytes(StandardCharsets.UTF_8));
	}
	public static <T> void addEntry(T obj,List<T> list,int limit){
		if(list.size()>=limit)
			list.subList(limit-1,list.size()).clear();
		list.add(0,obj);
	}
	public static <T> T getEntry(List<T> list){
		return list.get(0);
	}
}
