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
import com.github.chungkwong.json.*;
import com.github.chungkwong.jtk.api.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class OptionDescriptor{
	private final String shortDescription,longDescription,type;
	private final String defaultValue;
	public OptionDescriptor(String shortDescription,String longDescription,String type,String defaultValue){
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
	public String getDefaultValue(){
		return defaultValue;
	}
	private static final JSONString SHORT_DESCRIPTION=new JSONString("short_description");
	private static final JSONString LONG_DESCRIPTION=new JSONString("long_description");
	private static final JSONString TYPE=new JSONString("type");
	private static final JSONString DEFAULT=new JSONString("default_value");
	private JSONObject toJSONObject(){
		return new JSONObject(Helper.hashMap(SHORT_DESCRIPTION,new JSONString(shortDescription),
				LONG_DESCRIPTION,new JSONString(longDescription),
				TYPE,new JSONString(type),
				DEFAULT,new JSONString(defaultValue)));
	}
	static OptionDescriptor fromJSONObject(JSONObject obj){
		Map<JSONStuff,JSONStuff> table=obj.getMembers();
		return new OptionDescriptor(table.get(SHORT_DESCRIPTION).toString(),
				table.get(LONG_DESCRIPTION).toString(),
				table.get(TYPE).toString(),
				table.get(DEFAULT).toString());
	}
	@Override
	public String toString(){
		return toJSONObject().toString();
	}
}
