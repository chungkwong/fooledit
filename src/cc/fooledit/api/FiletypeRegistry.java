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
package cc.fooledit.api;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FiletypeRegistry{
	private static final List<MimeGeusser> GEUSSERS=new ArrayList<>();
	private static final MimeGeusser URL_GEUSSER=new MimeGeusser.URLPatternGeusser();
	private static final MimeGeusser SYSTEM_GEUSSERS=new MimeGeusser.URLPatternGeusser();
	public static List<MimeGeusser> getGEUSSERS(){
		return GEUSSERS;
	}
	public static MimeGeusser getURL_GEUSSER(){
		return URL_GEUSSER;
	}
	public static List<String> geuss(byte[] beginning){
		for(MimeGeusser geusser:GEUSSERS){
			List<String> geuss=geusser.geuss(beginning);
			if(!geuss.isEmpty()){
				return geuss;
			}
		}
		return Collections.singletonList("application/octet-stream");
	}
}
