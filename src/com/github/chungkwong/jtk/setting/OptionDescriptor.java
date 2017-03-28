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
package com.github.chungkwong.jtk.setting;
import java.io.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class OptionDescriptor{
	private final String shortDescription,longDescription,type;
	private final Object defaultValue;
	public OptionDescriptor(String shortDescription,String longDescription,String type,Object defaultValue){
		this.shortDescription=shortDescription;
		this.longDescription=longDescription;
		this.type=type;
		this.defaultValue=defaultValue;
	}
	public String getShortDescription(){
		return shortDescription;
	}
	public String getLongDescription(){
		return longDescription;
	}
	public String getType(){
		return type;
	}
	public static void encode(Map<String,OptionDescriptor> data,File out){

	}
	public static Map<String,OptionDescriptor> decode(File in){
		HashMap<String,OptionDescriptor> data=new HashMap<>();

		return data;
	}
}
