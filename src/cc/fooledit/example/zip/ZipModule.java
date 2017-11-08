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
		FileNamePatternRegistry.registerSuffix("7z$","application/x-7z-compressed");
		FileNamePatternRegistry.registerSuffix("ar$","application/x-archive");
		FileNamePatternRegistry.registerSuffix("arj$","application/x-arj");
		FileNamePatternRegistry.registerSuffix("bz2$","application/x-bzip2");
		FileNamePatternRegistry.registerSuffix("cpio$","application/x-cpio");
		FileNamePatternRegistry.registerSuffix("gz$","application/x-gzip");
		FileNamePatternRegistry.registerSuffix("jar$","application/x-jar");
		FileNamePatternRegistry.registerSuffix("pack$","application/x-java-pack200");
		FileNamePatternRegistry.registerSuffix("lz4$","application/x-lz4");
		FileNamePatternRegistry.registerSuffix("lzma$","application/x-lzma");
		FileNamePatternRegistry.registerSuffix("tar$","application/x-tar");
		FileNamePatternRegistry.registerSuffix("xz$","application/x-xz");
		FileNamePatternRegistry.registerSuffix("zip$","application/x-zip-compressed");
		FileNamePatternRegistry.registerSuffix("Z$","application/zlib");
		FileNamePatternRegistry.registerSuffix("zz$","application/zlib");
		URLProtocolRegistry.register("archive",()->new ArchiveStreamHandler());
		URLProtocolRegistry.register("compressed",()->new ZipStreamHandler());
	}
	public static void onUnLoad(){

	}
}
