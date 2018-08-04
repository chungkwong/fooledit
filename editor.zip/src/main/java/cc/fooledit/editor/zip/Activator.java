/*
 * Copyright (C) 2018 Chan Chung Kwong
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
import cc.fooledit.editor.zip.Activator;
import cc.fooledit.spi.*;
import java.util.*;
import org.osgi.framework.*;
import org.osgi.service.url.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class Activator implements BundleActivator{
	public static final String NAME=Activator.class.getPackage().getName();
	public static final String ARCHIVE_PROTOCOL_NAME="archive";
	public static final String COMPRESSED_PROTOCOL_NAME="compressed";
	@Override
	public void start(BundleContext bc) throws Exception{
		Registry.providesDataObjectType(ZipObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(ZipEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(ZipObject.class.getName(),NAME);
		Registry.providesProtocol(COMPRESSED_PROTOCOL_NAME,NAME);
		Registry.providesDataObjectType(ArchiveObjectType.class.getName(),NAME);
		Registry.providesDataObjectEditor(ArchiveEditor.class.getName(),NAME);
		Registry.providesTypeToEditor(ArchiveObject.class.getName(),NAME);
		Registry.providesProtocol(ARCHIVE_PROTOCOL_NAME,NAME);
		DataObjectTypeRegistry.addDataObjectType(ArchiveObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(ArchiveEditor.INSTANCE,ArchiveObject.class);
		DataObjectTypeRegistry.addDataObjectType(ZipObjectType.INSTANCE);
		DataObjectTypeRegistry.addDataEditor(ZipEditor.INSTANCE,ZipObject.class);
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-7z-compressed","cc.fooledit.editor.zip.ArchiveObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-archive","cc.fooledit.editor.zip.ArchiveObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-arj","cc.fooledit.editor.zip.ArchiveObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-bzip","cc.fooledit.editor.zip.ZipObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-bzip2","cc.fooledit.editor.zip.ZipObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-cpio","cc.fooledit.editor.zip.ArchiveObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-gtar","cc.fooledit.editor.zip.ArchiveObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/gzip","cc.fooledit.editor.zip.ZipObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-gzip","cc.fooledit.editor.zip.ZipObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/java-archive","cc.fooledit.editor.zip.ArchiveObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-java-archive","cc.fooledit.editor.zip.ArchiveObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-jar","cc.fooledit.editor.zip.ArchiveObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-java-pack200","cc.fooledit.editor.zip.ZipObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-lz4","cc.fooledit.editor.zip.ZipObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-lzma","cc.fooledit.editor.zip.ZipObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-tar","cc.fooledit.editor.zip.ArchiveObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-xz","cc.fooledit.editor.zip.ZipObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/zip","cc.fooledit.editor.zip.ArchiveObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/x-zip-compressed","cc.fooledit.editor.zip.ArchiveObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/zlib","cc.fooledit.editor.zip.ZipObjectType");
		CoreModule.CONTENT_TYPE_LOADER_REGISTRY.put("application/vnd.rar","cc.fooledit.editor.zip.ArchiveObjectType");
		MultiRegistryNode.addChildElement("7z","application/x-7z-compressed",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("ar","application/x-archive",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("arj","application/x-arj",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("apk","application/vnd.android.package-archive",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("bz2","application/x-bzip2",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("cpio","application/x-cpio",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("deb","application/vnd.debian.binary-package",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("rar","application/vnd.rar",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("ear","application/x-java-archive",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("gz","application/x-gzip",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("jar","application/x-java-archive",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("pack","application/x-java-pack200",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("lz4","application/x-lz4",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("lzma","application/x-lzma",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("tar","application/x-tar",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("war","application/x-java-archive",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("xz","application/x-xz",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("zip","application/x-zip-compressed",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("Z","application/zlib",CoreModule.SUFFIX_REGISTRY);
		MultiRegistryNode.addChildElement("zz","application/zlib",CoreModule.SUFFIX_REGISTRY);
		CoreModule.CONTENT_TYPE_SUPERCLASS_REGISTRY.put("application/vnd.android.package-archive","application/x-java-archive");
		CoreModule.CONTENT_TYPE_SUPERCLASS_REGISTRY.put("application/vnd.debian.binary-package","application/x-archive");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/java-archive","application/x-java-archive");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-jar","application/x-java-archive");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-gtar","application/x-tar");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-zip-compressed","application/zip");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-gzip","application/gzip");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-bzip2","application/x-bzip");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-deb","application/vnd.debian.binary-package");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-debian-package","application/vnd.debian.binary-package");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-rar","application/vnd.rar");
		CoreModule.CONTENT_TYPE_ALIAS_REGISTRY.put("application/x-rar-compressed","application/vnd.rar");
		Hashtable properties=new Hashtable();
		properties.put(URLConstants.URL_HANDLER_PROTOCOL,new String[]{"archive"});
		bc.registerService(URLStreamHandlerService.class.getName(),new ArchiveStreamHandler(),properties);
		properties=new Hashtable();
		properties.put(URLConstants.URL_HANDLER_PROTOCOL,new String[]{"compressed"});
		bc.registerService(URLStreamHandlerService.class.getName(),new ZipStreamHandler(),properties);
//TODO: DUMP,Brotli,DEFLATE,LZ77,LZW,Snappy
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
