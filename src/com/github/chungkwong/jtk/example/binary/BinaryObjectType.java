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
package com.github.chungkwong.jtk.example.binary;
import com.github.chungkwong.jtk.model.*;
import java.io.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BinaryObjectType implements DataObjectType<BinaryObject>{
	public static final BinaryObjectType INSTANCE=new BinaryObjectType();
	private BinaryObjectType(){
	}
	@Override
	public boolean canRead(){
		return true;
	}
	@Override
	public boolean canWrite(){
		return true;
	}
	@Override
	public boolean canCreate(){
		return true;
	}
	@Override
	public BinaryObject create(){
		return new BinaryObject(new byte[0]);
	}
	@Override
	public void writeTo(BinaryObject data,OutputStream out) throws Exception{
		out.write(data.dataProperty().getValue());
	}
	@Override
	public BinaryObject readFrom(InputStream in) throws Exception{
		List<byte[]> bufs=new ArrayList<>();
		List<Integer> lens=new ArrayList<>();
		byte[] buf=new byte[4096];
		int c;
		int total=0;
		while((c=in.read(buf))!=-1){
			bufs.add(buf);
			lens.add(c);
			buf=new byte[4096];
			total+=c;
		}
		byte[] data=new byte[total];
		for(int i=0,j=0;i<lens.size();i++){
			int len=lens.get(i);
			System.arraycopy(bufs.get(i),0,data,j,len);
			j+=len;
		}
		return new BinaryObject(data);
	}

}
