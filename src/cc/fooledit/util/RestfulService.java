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
package cc.fooledit.util;
import com.github.chungkwong.json.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.security.*;
import java.util.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class RestfulService{
	private static Object get(String url,Map<String,Object> parameters)throws Exception{
		HttpURLConnection conn=(HttpURLConnection)new URL(url+"?"+makeQuery(parameters)).openConnection();
		conn.setRequestMethod("GET");
		return JSONDecoder.decode(new InputStreamReader(conn.getInputStream(),StandardCharsets.UTF_8));
	}
	private static Object post(String url,Map<String,Object> parameters)throws Exception{
		URLConnection conn=new URL(url).openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(makeQuery(parameters));
		out.flush();
		out.close();
		return JSONDecoder.decode(new InputStreamReader(conn.getInputStream(),StandardCharsets.UTF_8));
	}
	private static String makeQuery(Map<String,Object> parameters){
		StringBuilder query=new StringBuilder();
		parameters.forEach((k,v)->{
			query.append('&').append(k);
			if(v!=null)
				try{
					query.append('=').append(URLEncoder.encode(v.toString(),"UTF-8"));
				}catch(UnsupportedEncodingException ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
				}
		});
		return query.length()>0?query.substring(1):"";
	}
	public static String md5(String input){
		if(input==null){
			return null;
		}
		try{
			MessageDigest messageDigest=MessageDigest.getInstance("MD5");
			byte[] inputByteArray=input.getBytes("UTF-8");
			messageDigest.update(inputByteArray);
			byte[] resultByteArray=messageDigest.digest();
			return byteArrayToHex(resultByteArray);
		}catch(NoSuchAlgorithmException|UnsupportedEncodingException e){
			return null;
		}
	}
	private static String byteArrayToHex(byte[] byteArray){
		char[] resultCharArray=new char[byteArray.length*2];
		int index=0;
		for(byte b:byteArray){
			resultCharArray[index++]=hexDigits[b>>>4&0xf];
			resultCharArray[index++]=hexDigits[b&0xf];
		}
		return new String(resultCharArray);
	}
	private static final char[] hexDigits={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
}
