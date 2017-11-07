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
import cc.fooledit.spi.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ZipModule{
	public static final String NAME="editor.zip";
	public static void onLoad(){
		DataObjectTypeRegistry.addDataObjectType(ArchiveDataType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(()->ArchiveEditor.INSTANCE,ArchiveData.class);
		DataObjectTypeRegistry.addDataObjectType(ZipDataType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(()->ZipEditor.INSTANCE,ZipData.class);
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.7z$","application/x-7z-compressed");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.ar$","application/x-archive");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.arj$","application/x-arj");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.bz2$","application/x-bzip2");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.cpio$","application/x-cpio");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.gz$","application/x-gzip");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.jar$","application/x-jar");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.pack$","application/x-java-pack200");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.lz4$","application/x-lz4");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.lzma$","application/x-lzma");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.tar$","application/x-tar");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.xz$","application/x-xz");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.zip$","application/x-zip-compressed");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.Z$","application/zlib");
		FiletypeRegistry.getURL_GEUSSER().registerPathPattern(".*\\.zz$","application/zlib");
		URLProtocolRegistry.register("archive",()->new ArchiveStreamHandler());
		URLProtocolRegistry.register("compressed",()->new ZipStreamHandler());
	}
	public static void onUnLoad(){

	}
}
