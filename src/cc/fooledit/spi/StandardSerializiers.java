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
package cc.fooledit.spi;
import com.github.chungkwong.json.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class StandardSerializiers{
	public static String PROPERTIES="properties";
	public static String JSON="json";
	public static class PropertiesSerializier implements Serializier<Object>{
		@Override
		public Object decode(String code){
			try{
				Properties properties=new Properties();
				properties.load(new StringReader(code));
				return new SimpleRegistryNode<>(properties);
			}catch(IOException ex){
				throw new RuntimeException(ex);
			}
		}
		@Override
		public String encode(Object obj){
			Properties properties=new Properties();
			StringWriter out=new StringWriter();
			try{
				properties.store(out,null);
				return out.toString();
			}catch(IOException ex){
				throw new RuntimeException(ex);
			}
		}
	}
	public static class JSONSerializier implements Serializier<Object>,JSONWalker<SimpleRegistryNode,ListRegistryNode>{
		@Override
		public Object decode(String code){
			try{
				Object decode=JSONDecoder.decode(code);
				return JSONDecoder.walk(code,this);
			}catch(IOException|SyntaxException ex){
				throw new RuntimeException(ex);
			}
		}
		@Override
		public String encode(Object obj){
			return JSONEncoder.encode(obj);
		}
		@Override
		public SimpleRegistryNode createMap(){
			return new SimpleRegistryNode();
		}
		@Override
		public ListRegistryNode createList(){
			return new ListRegistryNode();
		}
		@Override
		public void onEntry(Object value,Object key,SimpleRegistryNode registry){
			registry.addChild(key.toString(),value);
		}
		@Override
		public void onComponent(Object value,int index,ListRegistryNode registry){
			registry.addChild(index,value);
		}
	}
}
