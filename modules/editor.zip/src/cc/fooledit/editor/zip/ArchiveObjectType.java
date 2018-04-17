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
package cc.fooledit.editor.zip;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.commons.compress.archivers.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ArchiveObjectType implements DataObjectType<ArchiveObject>{
	public static final ArchiveObjectType INSTANCE=new ArchiveObjectType();
	private ArchiveObjectType(){
	}
	@Override
	public boolean canRead(){
		return true;
	}
	@Override
	public boolean canWrite(){
		return false;//TODO
	}
	@Override
	public boolean canCreate(){
		return true;
	}
	@Override
	public ArchiveObject create(){
		return new ArchiveObject(Collections.emptyList(),null);
	}
	@Override
	public ArchiveObject readFrom(URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		URL url=connection.getURL();
		try(ArchiveInputStream archive=openStream(connection)){
			ArchiveEntry entry;
			List<ArchiveEntry> entries=new ArrayList<>();
			while((entry=archive.getNextEntry())!=null){
				entries.add(entry);
			}
			return new ArchiveObject(entries,url);
		}
	}
	private static ArchiveInputStream openStream(URLConnection connection) throws ArchiveException, IOException{
		String mime=ContentTypeHelper.guess(connection).get(0);
		if(mime.equals("application/vnd.rar"))
			return new RarInputStream(connection.getInputStream());
		else
			return new ArchiveStreamFactory().createArchiveInputStream(getArchiver(mime),connection.getInputStream());
	}
	@Override
	public String getDisplayName(){
		return "archive";
	}
	private static String getArchiver(String mime){
		return mime2archive.get(mime);
	}
	private static final Map<String,String> mime2archive=new HashMap<>();
	static{
		mime2archive.put("application/x-7z-compressed","7Z");
		mime2archive.put("application/x-archive","AR");
		mime2archive.put("application/x-arj","ARJ");
		mime2archive.put("application/x-cpio","CPIO");
		mime2archive.put("application/x-gtar","TAR");
		mime2archive.put("application/java-archive","JAR");
		mime2archive.put("application/x-java-archive","JAR");
		mime2archive.put("application/x-jar","JAR");
		mime2archive.put("application/x-tar","TAR");
		mime2archive.put("application/zip","ZIP");
		mime2archive.put("application/x-zip-compressed","ZIP");
		mime2archive.put("application/vnd.rar","RAR");
	}
	@Override
	public void writeTo(ArchiveObject data,URLConnection connection,RegistryNode<String,Object> meta) throws Exception{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
