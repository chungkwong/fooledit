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
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CharsetDetector{
	private static final List<CharsetDecoder> prefered=new ArrayList<>();
	public static List<CharsetDecoder> getPreferedCharsets(){
		return prefered;
	}
	public static String probeEncoding(byte[] data,int offset,int length){
		return probeEncoding(data,offset,length,prefered);
	}
	public static String probeEncoding(byte[] data,int offset,int length,List<CharsetDecoder> candidates){
		ByteBuffer buf=ByteBuffer.wrap(data,offset,length);
		CharBuffer chars=CharBuffer.allocate(data.length);
		for(CharsetDecoder decoder:candidates){
			if(isPrefered(buf,chars,decoder))
				return decoder.charset().name();
		}
		return null;
	}
	public static Collection<String> probeEncodings(byte[] data,int offset,int length){
		return probeEncodings(data,offset,length,prefered);
	}
	public static Collection<String> probeEncodings(byte[] data,int offset,int length,List<CharsetDecoder> candidates){
		ByteBuffer buf=ByteBuffer.wrap(data,offset,length);
		CharBuffer chars=CharBuffer.allocate(data.length);
		return candidates.stream().filter((decoder)->isPrefered(buf,chars,decoder)).
				map((decoder)->decoder.charset().name()).collect(Collectors.toList());
	}
	private static boolean isPrefered(ByteBuffer buf,CharBuffer chars,CharsetDecoder decoder){
		buf.rewind();
		chars.rewind();
		CoderResult result=decoder.decode(buf,chars,false);
		return buf.position()>buf.limit()-4;
	}
	public static List<CharsetDecoder> getAllDecoders(){
		return Charset.availableCharsets().values().stream().map((set)->set.newDecoder()).collect(Collectors.toList());
	}
	public static void main(String[] args) throws IOException{
		Scanner in=new Scanner(System.in);
		List<CharsetDecoder> candidates=getAllDecoders();
		System.out.println(candidates.size());
		byte[] buf=new byte[4096];
		while(in.hasNextLine()){
			String next=in.nextLine();
			if(next.isEmpty())
				break;
			int len=new FileInputStream(new File(next)).read(buf);
			System.out.println(probeEncodings(buf,0,len,candidates));
		}
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
