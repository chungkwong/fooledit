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
import java.util.*;
import javafx.scene.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ZipEditor implements DataEditor<ZipData>{
	public static final ZipEditor INSTANCE=new ZipEditor();
	private ZipEditor(){
	}
	@Override
	public Node edit(ZipData data,Object remark,RegistryNode<String,Object,String> meta){
		List<DataEditor> editors=DataObjectTypeRegistry.getDataEditors(data.getContent().getClass());
		if(!editors.isEmpty())
			return editors.get(0).edit(data.getContent(),remark,meta);
		return null;
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("COMPRESSED_DATA_EDITOR",ZipModule.NAME);
	}
}
