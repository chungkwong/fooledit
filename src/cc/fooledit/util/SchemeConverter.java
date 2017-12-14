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
package cc.fooledit.util;
import com.github.chungkwong.jschememin.type.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SchemeConverter{
	public static int toInteger(ScmObject obj){
		if(obj instanceof ScmComplex)
			return ((ScmComplex)obj).intValueExact();
		else if(obj instanceof ScmString)
			return Integer.parseInt(((ScmString)obj).getValue());
		else
			throw new NumberFormatException();
	}
	public static String toString(ScmObject obj){
		return ((ScmString)obj).getValue();
	}
	public static Object toJava(ScmObject obj){
		if(obj instanceof ScmJavaObject)
			return ((ScmJavaObject)obj).getJavaObject();
		else if(obj instanceof ScmString)
			return ((ScmString)obj).getValue();
		else if(obj instanceof ScmBoolean)
			return ((ScmBoolean)obj).isTrue();
		else
			return obj;
	}
	public static ScmObject toScheme(Object obj){
		if(obj instanceof ScmObject)
			return (ScmObject)obj;
		else if(obj instanceof String)
			return new ScmString((String)obj);
		else if(obj instanceof Boolean)
			return ScmBoolean.valueOf((Boolean)obj);
		else
			return new ScmJavaObject(obj);
	}
}
