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
import static cc.fooledit.core.CoreModule.PROTOCOL_REGISTRY;
import cc.fooledit.core.*;
import org.osgi.framework.*;
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
		PROTOCOL_REGISTRY.put("archive",new ArchiveStreamHandler());
		PROTOCOL_REGISTRY.put("compressed",new ZipStreamHandler());
	}
	@Override
	public void stop(BundleContext bc) throws Exception{
	}
}
