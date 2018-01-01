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
import static cc.fooledit.core.CoreModule.CONTENT_TYPE_ALIAS_REGISTRY;
import static cc.fooledit.core.CoreModule.CONTENT_TYPE_SUPERCLASS_REGISTRY;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ContentTypeRegistry{
	public static boolean isSubclassOf(String type,String ancestor){
		type=normalize(type);
		ancestor=normalize(ancestor);
		while(type!=null){
			if(type.equals(ancestor))
				return true;
			type=normalize(CONTENT_TYPE_SUPERCLASS_REGISTRY.getChild(type));
		}
		return false;
	}
	public static String normalize(String type){
		String con;
		while((con=CONTENT_TYPE_ALIAS_REGISTRY.getChild(type))!=null){
			type=con;
		}
		return type;
	}
	public static List<String> getAllSuperClasses(String type){
		ArrayList<String> list=new ArrayList<>();
		while(type!=null){
			type=normalize(type);
			list.add(type);
			type=CONTENT_TYPE_SUPERCLASS_REGISTRY.getChild(type);
		}
		return list;
	}
}
