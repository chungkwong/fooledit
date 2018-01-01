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
package cc.fooledit.spi;
import cc.fooledit.core.DataObjectTypeRegistry;
import cc.fooledit.core.CoreModule;
import cc.fooledit.core.DataObject;
import cc.fooledit.core.DataObjectType;
import cc.fooledit.core.DataEditor;
import java.io.*;
import java.net.*;
import java.util.function.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ApplicationRegistry extends URLStreamHandler{
	public static void register(String path,String baseType,DataObjectType factory,Class<? extends DataObject> cls,Supplier<DataEditor> editorSupplier){
		CoreModule.APPLICATION_REGISTRY.addChild(path,baseType);
		DataObjectTypeRegistry.addDataObjectType(factory);
		DataObjectTypeRegistry.addDataEditor(editorSupplier,cls);
		DataObjectTypeRegistry.registerMime(baseType,factory.getDisplayName());
	}
	@Override
	protected URLConnection openConnection(URL u) throws IOException{
		return new ApplicationURLConnection(u);
	}
	private class ApplicationURLConnection extends URLConnection{
		public ApplicationURLConnection(URL url){
			super(url);
		}
		@Override
		public void connect() throws IOException{

		}
		@Override
		public String getContentType(){
			return CoreModule.APPLICATION_REGISTRY.getChild(getURL().getPath());
		}
	}
}
