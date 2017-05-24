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
package com.github.chungkwong.jtk.example.tool;
import com.github.chungkwong.jtk.model.*;
import java.io.*;
import java.nio.charset.*;
import java.util.stream.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BrowserDataType implements DataObjectType<BrowserData>{
	public static final BrowserDataType INSTANCE=new BrowserDataType();
	private static final String MIME="text/html";
	private BrowserDataType(){
	}
	@Override
	public boolean canRead(){
		return true;
	}
	@Override
	public boolean canWrite(){
		return false;
	}
	@Override
	public void writeTo(BrowserData data,OutputStream out) throws Exception{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public BrowserData readFrom(InputStream in) throws Exception{
		String content=new BufferedReader(new InputStreamReader(in,StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		BrowserData data=new BrowserData();
		data.getEngine().loadContent(content);
		return data;
	}
	@Override
	public BrowserData create(){
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean canCreate(){
		return false;
	}
}
