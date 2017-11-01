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
import cc.fooledit.model.*;
import java.net.*;
import java.util.*;
import org.apache.commons.compress.archivers.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ArchiveData implements DataObject<ArchiveData>{
	private final List<ArchiveEntry> entries;
	private final URL url;
	public ArchiveData(List<ArchiveEntry> entries,URL url){
		this.entries=entries;
		this.url=url;
	}
	public List<ArchiveEntry> getEntries(){
		return entries;
	}
	public URL getUrl(){
		return url;
	}
	@Override
	public DataObjectType<ArchiveData> getDataObjectType(){
		return ArchiveDataType.INSTANCE;
	}
}
