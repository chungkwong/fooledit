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
package com.github.chungkwong.jtk.api;
import com.github.chungkwong.jtk.util.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MimeDetector{
	private static final List<CharsetDecoder> prefered=new ArrayList<>();
	public static List<CharsetDecoder> getPreferedCharsets(){
		return prefered;
	}
	public static MimeType probeMimeType(File file){
		try{
			return MimeType.fromString(Files.probeContentType(file.toPath()));
		}catch(IOException ex){
			return new MimeType("application","octet-stream",Collections.emptyMap());
		}
	}
	public static String probeEncoding(byte[] data){
		return probeEncoding(data,prefered);
	}
	public static String probeEncoding(byte[] data,List<CharsetDecoder> candidates){
		ByteBuffer buf=ByteBuffer.wrap(data);
		for(CharsetDecoder decoder:candidates){
			try{
				decoder.decode(buf);
				return decoder.charset().name();
			}catch(CharacterCodingException ex){

			}
		}
		return null;
	}
	public static Collection<String> probeEncodings(byte[] data){
		return probeEncodings(data,prefered);
	}
	public static Collection<String> probeEncodings(byte[] data,List<CharsetDecoder> candidates){
		ByteBuffer buf=ByteBuffer.wrap(data);
		return candidates.stream().filter((decoder)->{
			try{
				decoder.decode(buf);
				return true;
			}catch(CharacterCodingException ex){
				return false;
			}
		}).map(CharsetDecoder::toString).collect(Collectors.toList());
	}
	static{
		prefered.add(StandardCharsets.UTF_8.newDecoder());
		prefered.add(StandardCharsets.UTF_16.newDecoder());
		prefered.add(StandardCharsets.UTF_16LE.newDecoder());
		prefered.add(StandardCharsets.UTF_16BE.newDecoder());
		prefered.add(StandardCharsets.ISO_8859_1.newDecoder());
		prefered.add(Charset.defaultCharset().newDecoder());
	}
}
