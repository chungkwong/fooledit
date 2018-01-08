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
import static cc.fooledit.core.CoreModule.CONTENT_TYPE_ALIAS_REGISTRY;
import static cc.fooledit.core.CoreModule.CONTENT_TYPE_SUPERCLASS_REGISTRY;
import static cc.fooledit.core.CoreModule.PROTOCOL_REGISTRY;
import static cc.fooledit.core.CoreModule.SUFFIX_REGISTRY;
import cc.fooledit.core.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ZipModule{
	public static final String NAME="editor.zip";
	public static final String ARCHIVE_PROTOCOL_NAME="archive";
	public static final String COMPRESSED_PROTOCOL_NAME="compressed";
	public static void onLoad(){
		DataObjectTypeRegistry.addDataObjectType(ArchiveObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(()->ArchiveEditor.INSTANCE,ArchiveObject.class);
		DataObjectTypeRegistry.addDataObjectType(ZipObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(()->ZipEditor.INSTANCE,ZipObject.class);
		PROTOCOL_REGISTRY.addChild("archive",new ArchiveStreamHandler());
		PROTOCOL_REGISTRY.addChild("compressed",new ZipStreamHandler());
	}
	public static void onUnLoad(){

	}
	public static void onInstall(){
		CONTENT_TYPE_SUPERCLASS_REGISTRY.addChild("application/vnd.android.package-archive","application/x-java-archive");
		CONTENT_TYPE_SUPERCLASS_REGISTRY.addChild("application/vnd.debian.binary-package","application/x-archive");
		CONTENT_TYPE_ALIAS_REGISTRY.addChild("application/java-archive","application/x-java-archive");
		CONTENT_TYPE_ALIAS_REGISTRY.addChild("application/x-jar","application/x-java-archive");
		CONTENT_TYPE_ALIAS_REGISTRY.addChild("application/x-gtar","application/x-tar");
		CONTENT_TYPE_ALIAS_REGISTRY.addChild("application/x-zip-compressed","application/zip");
		CONTENT_TYPE_ALIAS_REGISTRY.addChild("application/x-gzip","application/gzip");
		CONTENT_TYPE_ALIAS_REGISTRY.addChild("application/x-bzip2","application/x-bzip");
		CONTENT_TYPE_ALIAS_REGISTRY.addChild("application/x-deb","application/vnd.debian.binary-package");
		CONTENT_TYPE_ALIAS_REGISTRY.addChild("application/x-debian-package","application/vnd.debian.binary-package");
		SUFFIX_REGISTRY.addChild("7z","application/x-7z-compressed");
		SUFFIX_REGISTRY.addChild("ar","application/x-archive");
		SUFFIX_REGISTRY.addChild("arj","application/x-arj");
		SUFFIX_REGISTRY.addChild("apk","application/vnd.android.package-archive");
		SUFFIX_REGISTRY.addChild("bz2","application/x-bzip2");
		SUFFIX_REGISTRY.addChild("cpio","application/x-cpio");
		SUFFIX_REGISTRY.addChild("deb","application/vnd.debian.binary-package");
		SUFFIX_REGISTRY.addChild("ear","application/x-java-archive");
		SUFFIX_REGISTRY.addChild("gz","application/x-gzip");
		SUFFIX_REGISTRY.addChild("jar","application/x-java-archive");
		SUFFIX_REGISTRY.addChild("pack","application/x-java-pack200");
		SUFFIX_REGISTRY.addChild("lz4","application/x-lz4");
		SUFFIX_REGISTRY.addChild("lzma","application/x-lzma");
		SUFFIX_REGISTRY.addChild("tar","application/x-tar");
		SUFFIX_REGISTRY.addChild("war","application/x-java-archive");
		SUFFIX_REGISTRY.addChild("xz","application/x-xz");
		SUFFIX_REGISTRY.addChild("zip","application/x-zip-compressed");
		SUFFIX_REGISTRY.addChild("Z","application/zlib");
		SUFFIX_REGISTRY.addChild("zz","application/zlib");
		Registry.providesDataObjectType(ZipObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(ZipObject.class.getName(),NAME);
		Registry.providesProtocol(COMPRESSED_PROTOCOL_NAME,NAME);
		Registry.providesDataObjectType(ArchiveObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(ArchiveObject.class.getName(),NAME);
		Registry.providesProtocol(ARCHIVE_PROTOCOL_NAME,NAME);
	}
}
