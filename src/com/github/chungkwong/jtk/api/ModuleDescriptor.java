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
import com.github.chungkwong.json.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ModuleDescriptor{
	private final String name,description,author,license,url;
	private final int versionMajor,versionMinor,versionRevise;
	public ModuleDescriptor(String name,String description,String author,String license,String url,int versionMajor,int versionMinor,int versionRevise){
		this.name=name;
		this.description=description;
		this.author=author;
		this.license=license;
		this.url=url;
		this.versionMajor=versionMajor;
		this.versionMinor=versionMinor;
		this.versionRevise=versionRevise;
	}
	public String getName(){
		return name;
	}
	public String getDescription(){
		return description;
	}
	public String getAuthor(){
		return author;
	}
	public String getLicense(){
		return license;
	}
	public String getURL(){
		return url;
	}
	public int getVersionMajor(){
		return versionMajor;
	}
	public int getVersionMinor(){
		return versionMinor;
	}
	public int getVersionRevise(){
		return versionRevise;
	}
	public String toJSON(){
		Map<JSONStuff,JSONStuff> object=new HashMap<>();
		object.put(NAME,new JSONString(getName()));
		object.put(DESCRIPTION,new JSONString(getDescription()));
		object.put(LICENSE,new JSONString(getLicense()));
		object.put(AUTHOR,new JSONString(getAuthor()));
		object.put(URL,new JSONString(getURL()));
		object.put(MAJOR_VERSION,new JSONNumber(getVersionMajor()));
		object.put(MINOR_VERSION,new JSONNumber(getVersionMinor()));
		object.put(REVISE_VERSION,new JSONNumber(getVersionRevise()));
		return new JSONObject(null).toString();
	}
	public static ModuleDescriptor fromJSON(String json) throws SyntaxException, IOException{
		return fromJSON((JSONObject)JSONParser.parse(json));
	}
	public static ModuleDescriptor fromJSON(JSONObject json){
		Map<JSONStuff,JSONStuff> object=json.getMembers();
		String name=((JSONString)object.get(NAME)).getValue();
		String description=((JSONString)object.get(DESCRIPTION)).getValue();
		String author=((JSONString)object.get(AUTHOR)).getValue();
		String license=((JSONString)object.get(LICENSE)).getValue();
		String url=((JSONString)object.get(URL)).getValue();
		int versionMajor=((JSONNumber)object.get(MAJOR_VERSION)).getValue().intValue();
		int versionMinor=((JSONNumber)object.get(MINOR_VERSION)).getValue().intValue();
		int versionRevise=((JSONNumber)object.get(REVISE_VERSION)).getValue().intValue();
		return new ModuleDescriptor(name,description,author,license,url,versionMajor,versionMinor,versionRevise);
	}
	private static final JSONString NAME=new JSONString("name");
	private static final JSONString DESCRIPTION=new JSONString("description");
	private static final JSONString LICENSE=new JSONString("license");
	private static final JSONString AUTHOR=new JSONString("author");
	private static final JSONString URL=new JSONString("url");
	private static final JSONString MAJOR_VERSION=new JSONString("version_major");
	private static final JSONString MINOR_VERSION=new JSONString("version_minor");
	private static final JSONString REVISE_VERSION=new JSONString("version_revise");
}
