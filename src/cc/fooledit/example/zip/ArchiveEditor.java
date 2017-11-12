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
import cc.fooledit.*;
import cc.fooledit.api.*;
import cc.fooledit.model.*;
import java.net.*;
import java.util.logging.*;
import javafx.scene.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ArchiveEditor implements DataEditor<ArchiveData>{
	public static final ArchiveEditor INSTANCE=new ArchiveEditor();
	private ArchiveEditor(){
	}
	@Override
	public Node edit(ArchiveData data){
		ArchiveViewer viewer=new ArchiveViewer(data.getEntries());
		viewer.setAction((entries)->{
			entries.forEach((entry)->{
				URL url=null;
				try{
					url=new URL("archive","",data.getUrl().getFile()+'!'+entry.getName());
					Main.show(DataObjectRegistry.readFrom(url));
				}catch(Exception ex){
					Logger.getGlobal().log(Level.SEVERE,null,ex);
				}

			});
		});
		return viewer;
	}
	@Override
	public String getName(){
		return "Archive";
	}
}
