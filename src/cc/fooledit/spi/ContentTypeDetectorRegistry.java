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
import cc.fooledit.model.*;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ContentTypeDetectorRegistry{
	private static final List<ContentTypeDetector> GEUSSERS=new ArrayList<>();
	private static final ContentTypeDetector.URLPatternGeusser URL_GEUSSER=new ContentTypeDetector.URLPatternGeusser();
	private static final ContentTypeDetector SYSTEM_GEUSSERS=new ContentTypeDetector.SystemGeusser();
	public static List<ContentTypeDetector> getGEUSSERS(){
		return GEUSSERS;
	}
	public static ContentTypeDetector.URLPatternGeusser getURL_GEUSSER(){
		return URL_GEUSSER;
	}
	public static List<String> geuss(URLConnection connection){
		for(ContentTypeDetector guesser:GEUSSERS){
			List<String> guess=guesser.listAllPossible(connection);
			if(!guess.isEmpty()){
				return guess;
			}
		}
		return Collections.singletonList("application/octet-stream");
	}
	public static void main(String[] args) throws IOException{
		//System.out.println(new String(new byte[]{0,5,0}).length());
		//System.out.println(new URL("file:///home/kwong/icon.png").openConnection().getContentType());
		//System.out.println(new URL("file:///home/kwong/NetBeansProjects/JSchemeMin/doc/overview.pdf").openConnection().getContentType());
	}
	static{
		GEUSSERS.add(URL_GEUSSER);
		GEUSSERS.add(SYSTEM_GEUSSERS);
	}
}
