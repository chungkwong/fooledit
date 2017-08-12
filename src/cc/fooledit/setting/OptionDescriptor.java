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
package cc.fooledit.setting;
import cc.fooledit.api.Helper;
import com.github.chungkwong.json.*;
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
	private static final String SHORT_DESCRIPTION="short_description";
	private static final String LONG_DESCRIPTION="long_description";
	private static final String TYPE="type";
	private static final String DEFAULT="default_value";
	private Map<Object,Object> toJSONObject(){
		return Helper.hashMap(SHORT_DESCRIPTION,shortDescription,
				LONG_DESCRIPTION,longDescription,
				TYPE,type,
				DEFAULT,defaultValue);
	}
	static OptionDescriptor fromJSONObject(Map<Object,Object> table){
		return new OptionDescriptor((String)table.get(SHORT_DESCRIPTION),
				(String)table.get(LONG_DESCRIPTION),(String)table.get(TYPE),(String)table.get(DEFAULT));
	}
	@Override
	public String toString(){
		return JSONEncoder.encode(toJSONObject());
	}
}
