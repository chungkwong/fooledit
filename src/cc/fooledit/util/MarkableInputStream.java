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
import java.io.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MarkableInputStream extends InputStream{
	private final InputStream base;
	private int readPosition=-1;
	private int writePosition=-1;
	private int readLimit=-1;
	private byte[] buf;
	public MarkableInputStream(InputStream base){
		this.base=base;
	}
	@Override
	public boolean markSupported(){
		return true;
	}
	@Override
	public synchronized void mark(int readlimit){
		if(this.readLimit!=-1){
			writePosition-=readPosition;
			byte[] tmp=new byte[Math.max(writePosition,readlimit)];
			System.arraycopy(buf,readPosition,tmp,0,writePosition);
			buf=tmp;
		}else{
			buf=new byte[readlimit];
			writePosition=0;
		}
		this.readLimit=readlimit;
		this.readPosition=0;
	}
	@Override
	public synchronized void reset() throws IOException{
		this.readLimit=-1;
		this.readPosition=0;
	}
	@Override
	public long skip(long n) throws IOException{
		return base.skip(n);
	}
	@Override
	public int available() throws IOException{
		return base.available();
	}
	@Override
	public void close() throws IOException{
		base.close();
	}
	@Override
	public int read(byte[] b,int off,int len) throws IOException{
		return super.read(b,off,len); //TODO
	}
	@Override
	public int read() throws IOException{
		if(readLimit==-1){
			if(buf==null){
				return base.read();
			}else{
				int b=buf[readPosition++];
				if(readPosition==writePosition)
					buf=null;
				return b;
			}
		}else{
			if(readPosition==writePosition){
				int b=base.read();
				if(b>=0){
					buf[writePosition++]=(byte)b;
					readPosition++;
				}
				return b;
			}else{
				return buf[readPosition++];
			}
		}
	}
	public static InputStream wrap(InputStream in){
		if(in.markSupported())
			return in;
		else
			return new MarkableInputStream(in);
	}
}
