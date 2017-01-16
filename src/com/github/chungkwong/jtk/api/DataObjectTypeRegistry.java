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
import com.github.chungkwong.jtk.model.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DataObjectTypeRegistry{
	private static final HashMap<String,List<DataLoader>> loaders=new HashMap<>();
	private static final HashMap<String,List<DataEditor>> editors=new HashMap<>();
	private static final HashMap<String,List<DataWriter>> writers=new HashMap<>();
	public static void addDataLoader(String mime,DataLoader loader){
		if(!loaders.containsKey(mime))
			loaders.put(mime,new LinkedList<>());
		loaders.get(mime).add(0,loader);
	}
	public static void addDataWriter(String mime,DataWriter writer){
		if(!writers.containsKey(mime))
			writers.put(mime,new LinkedList<>());
		writers.get(mime).add(0,writer);
	}
	public static void addDataEditor(String mime,DataEditor editor){
		if(!editors.containsKey(mime))
			editors.put(mime,new LinkedList<>());
		editors.get(mime).add(0,editor);
	}
	public static List<DataLoader> getDataLoaders(String mime){
		return loaders.get(mime);
	}
	public static List<DataWriter> getDataWriters(String mime){
		return writers.get(mime);
	}
	public static List<DataEditor> getDataEditors(String mime){
		return editors.get(mime);
	}
}
