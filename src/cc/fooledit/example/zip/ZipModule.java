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
		ContentTypeRegistry.registerSubclass("application/vnd.android.package-archive","application/x-java-archive");
		ContentTypeRegistry.registerSubclass("application/vnd.debian.binary-package","application/x-archive");
		ContentTypeRegistry.registerAlias("application/java-archive","application/x-java-archive");
		ContentTypeRegistry.registerAlias("application/x-jar","application/x-java-archive");
		ContentTypeRegistry.registerAlias("application/x-gtar","application/x-tar");
		ContentTypeRegistry.registerAlias("application/x-zip-compressed","application/zip");
		ContentTypeRegistry.registerAlias("application/x-gzip","application/gzip");
		ContentTypeRegistry.registerAlias("application/x-bzip2","application/x-bzip");
		ContentTypeRegistry.registerAlias("application/x-deb","application/vnd.debian.binary-package");
		ContentTypeRegistry.registerAlias("application/x-debian-package","application/vnd.debian.binary-package");
		ContentTypeDetector.SuffixGuesser guesser=ContentTypeDetectorRegistry.getSUFFIX_GUESSER();
		guesser.registerSuffix("7z","application/x-7z-compressed");
		guesser.registerSuffix("ar","application/x-archive");
		guesser.registerSuffix("arj","application/x-arj");
		guesser.registerSuffix("apk","application/vnd.android.package-archive");
		guesser.registerSuffix("bz2","application/x-bzip2");
		guesser.registerSuffix("cpio","application/x-cpio");
		guesser.registerSuffix("deb","application/vnd.debian.binary-package");
		guesser.registerSuffix("ear","application/x-java-archive");
		guesser.registerSuffix("gz","application/x-gzip");
		guesser.registerSuffix("jar","application/x-java-archive");
		guesser.registerSuffix("pack","application/x-java-pack200");
		guesser.registerSuffix("lz4","application/x-lz4");
		guesser.registerSuffix("lzma","application/x-lzma");
		guesser.registerSuffix("tar","application/x-tar");
		guesser.registerSuffix("war","application/x-java-archive");
		guesser.registerSuffix("xz","application/x-xz");
		guesser.registerSuffix("zip","application/x-zip-compressed");
		guesser.registerSuffix("Z","application/zlib");
		guesser.registerSuffix("zz","application/zlib");
		CoreModule.PROTOCOL_REGISTRY.addChild("archive",new ArchiveStreamHandler());
		CoreModule.PROTOCOL_REGISTRY.addChild("compressed",new ZipStreamHandler());
	}
	public static void onUnLoad(){

	}
}
