/*
 * Copyright (C) 2018 Chan Chung Kwong <1m02math@126.com>
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
import cc.fooledit.util.*;
import java.io.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Argument{
	private final String name;
	private final ThrowableSupplier<Object> def;
	public Argument(String name){
		this(name,null);
	}
	public Argument(String name,ThrowableSupplier<Object> def){
		this.name=name;
		this.def=def;
	}
	public String getName(){
		return name;
	}
	public ThrowableSupplier<Object> getDef(){
		return def;
	}
	public static void main(String[] args){
		for(File f:new File("/home/kwong/NetBeansProjects/fooledit").listFiles()){
			if(f.getName().startsWith("mode.")){
				File tokens=new File(f,"tokens.json");
				if(tokens.exists()){
					File distFolder=new File(f,"src/main/resources/cc/fooledit/editor/text/mode/"+f.getName().substring(5));
					distFolder.mkdirs();
					tokens.renameTo(new File(distFolder,"tokens.json"));
				}
			}
		}
	}
}
