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
package cc.fooledit.example.text;
import cc.fooledit.util.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CharsetDetector{
	private static final List<Charset> perferedEncodings=new ArrayList<>();
	public static Charset probeCharset(InputStream in) throws IOException{
		for(Charset set:perferedEncodings)
			if(isPossible(in,set))
				return set;
		List<Charset> cand=probeCharsets(in);
		return cand.isEmpty()?null:cand.get(0);
	}
	public static List<Charset> probeCharsets(InputStream in) throws IOException{
		return probeCharsets(in,true);
	}
	public static List<Charset> probeCharsets(InputStream in,boolean strict) throws IOException{
		SortedMap<String,Charset> charsets=Charset.availableCharsets();
		if(in.markSupported()){
			ByteBuffer buf=readBlock(in);
			List<Charset> cand=new ArrayList<>();
			Iterator<Map.Entry<String,Charset>> iterator=charsets.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<String,Charset> entry=iterator.next();
				if(isPossible(buf,entry.getValue(),strict)){
					cand.add(entry.getValue());
				}
			}
			return cand;
		}
		return new ArrayList<>(charsets.values());
	}
	public static boolean isPossible(InputStream in,Charset charset)throws IOException{
		return isPossible(in,charset,true);
	}
	public static boolean isPossible(InputStream in,Charset charset,boolean strict)throws IOException{
		if(in.markSupported()){
			return isPossible(readBlock(in),charset,strict);
		}
		return true;
	}
	private static ByteBuffer readBlock(InputStream in) throws IOException{
		byte[] data=new byte[4096];
		in.mark(4096);
		int size=in.read(data);
		in.reset();
		ByteBuffer buf=size>=0?ByteBuffer.wrap(data,0,size):ByteBuffer.allocate(0);
		buf.mark();
		return buf;
	}
	private static boolean isPossible(ByteBuffer buf,Charset charset,boolean strict){
		buf.reset();
		CharsetDecoder decoder=charset.newDecoder();
		decoder.onMalformedInput(CodingErrorAction.REPORT);
		decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
		CharBuffer text=CharBuffer.allocate((int)(1+buf.remaining()*decoder.maxCharsPerByte()));
		if(decoder.decode(buf,text,false).isError()){
			return false;
		}else{
			text.limit(text.position());
			text.position(0);
			return !strict||text.codePoints().allMatch((c)->isCommonCodepoint(c));
		}
	}
	public static List<Charset> getPerferedEncodings(){
		return perferedEncodings;
	}
	private static boolean isCommonCodepoint(int c){
		return Character.isValidCodePoint(c)&&!((c>=0x00&&c<0x09)||(c>=0x0E&&c<0x1C));
	}
	static{
		perferedEncodings.add(StandardCharsets.UTF_8);
		perferedEncodings.add(StandardCharsets.UTF_16);
	}
	public static void main(String[] args) throws IOException{
		System.out.println(probeCharsets(MarkableInputStream.wrap(new FileInputStream("/home/kwong/NetBeansProjects/jtk/test/cc/fooledit/example/text/GBK.txt"))));
		System.out.println(probeCharsets(MarkableInputStream.wrap(new FileInputStream("/home/kwong/NetBeansProjects/jtk/test/cc/fooledit/example/text/UTF-8.txt"))));
		System.out.println(probeCharsets(MarkableInputStream.wrap(new FileInputStream("/home/kwong/NetBeansProjects/jtk/test/cc/fooledit/example/text/UTF-16.txt"))));
		//System.out.println(probeCharsets(MarkableInputStream.wrap(new FileInputStream("/home/kwong/sysu_learning/政治课/中国近现代史纲要.xlsx"))));
	}
}
