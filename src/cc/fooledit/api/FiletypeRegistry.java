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
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FiletypeRegistry{
	private static final List<MimeGeusser> GEUSSERS=new ArrayList<>();
	private static final MimeGeusser.URLPatternGeusser URL_GEUSSER=new MimeGeusser.URLPatternGeusser();
	private static final MimeGeusser SYSTEM_GEUSSERS=new MimeGeusser.SystemGeusser();
	public static List<MimeGeusser> getGEUSSERS(){
		return GEUSSERS;
	}
	public static MimeGeusser.URLPatternGeusser getURL_GEUSSER(){
		return URL_GEUSSER;
	}
	public static List<String> guess(byte[] beginning){
		for(MimeGeusser geusser:GEUSSERS){
			List<String> geuss=geusser.geuss(beginning);
			if(!geuss.isEmpty()){
				return geuss;
			}
		}
		return Collections.singletonList("application/octet-stream");
	}
	public static List<String> guess(InputStream in){
		if(in.markSupported()){
			try{
				int size=Math.min(in.available(),4096);
				in.mark(size);
				byte[] buf=new byte[size];
				return guess(buf);
			}catch(IOException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
			}
		}
		return Collections.singletonList("application/octet-stream");
	}
	public static List<String> geuss(URL url){
		for(MimeGeusser geusser:GEUSSERS){
			List<String> geuss=geusser.geuss(url);
			if(!geuss.isEmpty()){
				return geuss;
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
