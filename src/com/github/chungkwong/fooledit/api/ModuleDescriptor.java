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
package com.github.chungkwong.fooledit.api;
import com.github.chungkwong.json.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ModuleDescriptor{
	private final String name,description,author,license,url;
	private final int versionMajor,versionMinor,versionRevise;
	private final List<String> dependency;
	public ModuleDescriptor(String name,String description,String author,String license,String url,
			int versionMajor,int versionMinor,int versionRevise,List<String> dependency){
		this.name=name;
		this.description=description;
		this.author=author;
		this.license=license;
		this.url=url;
		this.versionMajor=versionMajor;
		this.versionMinor=versionMinor;
		this.versionRevise=versionRevise;
		this.dependency=dependency;
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
	public List<String> getDependency(){
		return dependency;
	}
	public String toJSON(){
		Map<Object,Object> object=new HashMap<>();
		object.put(NAME,getName());
		object.put(DESCRIPTION,getDescription());
		object.put(LICENSE,getLicense());
		object.put(AUTHOR,getAuthor());
		object.put(URL,getURL());
		object.put(MAJOR_VERSION,getVersionMajor());
		object.put(MINOR_VERSION,getVersionMinor());
		object.put(REVISE_VERSION,getVersionRevise());
		return JSONEncoder.encode(object);
	}
	public static ModuleDescriptor fromJSON(Map<Object,Object> object){
		String name=(String)object.get(NAME);
		String description=(String)object.get(DESCRIPTION);
		String author=(String)object.get(AUTHOR);
		String license=(String)object.get(LICENSE);
		String url=(String)object.get(URL);
		int versionMajor=((Number)object.get(MAJOR_VERSION)).intValue();
		int versionMinor=((Number)object.get(MINOR_VERSION)).intValue();
		int versionRevise=((Number)object.get(REVISE_VERSION)).intValue();
		List<String> dependency=(List<String>)object.get(REVISE_VERSION);
		return new ModuleDescriptor(name,description,author,license,url,versionMajor,versionMinor,versionRevise,dependency);
	}
	private static final String NAME="name";
	private static final String DESCRIPTION="description";
	private static final String LICENSE="license";
	private static final String AUTHOR="author";
	private static final String URL="url";
	private static final String MAJOR_VERSION="version_major";
	private static final String MINOR_VERSION="version_minor";
	private static final String REVISE_VERSION="version_revise";
	private static final String DEPENDENCY="dependency";
}
