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
package cc.fooledit.example.zip;
import cc.fooledit.api.*;
import cc.fooledit.model.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.activation.*;
import org.apache.commons.compress.compressors.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ZipDataType implements DataObjectType<ZipData>{
	public static final ZipDataType INSTANCE=new ZipDataType();
	private ZipDataType(){
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
	public ZipData create(){
		return new ZipData(null);
	}
	@Override
	public void writeTo(ZipData data,OutputStream out) throws Exception{
		CompressorOutputStream compress=CompressorStreamFactory.getSingleton().createCompressorOutputStream(null,out);
		data.getContent().getDataObjectType().writeTo(data,compress);
	}
	@Override
	public ZipData readFrom(InputStream in) throws Exception{
		CompressorInputStream decompressed=CompressorStreamFactory.getSingleton().createCompressorInputStream(in);
		List<String> geuss=FiletypeRegistry.guess(in);
		if(!geuss.isEmpty()){
			List<DataObjectType> contentType=DataObjectTypeRegistry.getPreferedDataObjectType(new MimeType(geuss.get(0)));
			if(!contentType.isEmpty())
				return new ZipData(contentType.get(0).readFrom(decompressed));
		}
		throw new Exception();
	}
	@Override
	public ZipData readFrom(URLConnection connection) throws Exception{
		CompressorInputStream decompressed=CompressorStreamFactory.getSingleton().createCompressorInputStream(connection.getInputStream());
		URL url=connection.getURL();
		String file=url.getFile();
		if(file.contains("."))
			file=file.substring(0,file.lastIndexOf('.'));
		url=new URL(url.getProtocol(),url.getHost(),url.getPort(),file);
		List<String> geuss=FiletypeRegistry.geuss(url);
		if(!geuss.isEmpty()){
			List<DataObjectType> contentType=DataObjectTypeRegistry.getPreferedDataObjectType(new MimeType(geuss.get(0)));
			if(!contentType.isEmpty())
				return new ZipData(contentType.get(0).readFrom(decompressed));
		}
		throw new Exception();
	}
	@Override
	public String getName(){
		return "zip";
	}
}
