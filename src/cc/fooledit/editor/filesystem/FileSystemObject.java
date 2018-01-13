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
package cc.fooledit.editor.filesystem;
import cc.fooledit.core.*;
import com.sun.javafx.collections.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import javafx.collections.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FileSystemObject implements DataObject<FileSystemObject>{
	private Consumer<Collection<Path>> action;
	private ObservableList<Path> paths=new ObservableListWrapper<>(new ArrayList<>());
	public FileSystemObject(){
	}
	public FileSystemObject(Consumer<Collection<Path>> action){
		this.action=action;
	}
	public void setAction(Consumer<Collection<Path>> action){
		this.action=action;
	}
	public ObservableList<Path> getPaths(){
		return paths;
	}
	public Consumer<Collection<Path>> getAction(){
		return action;
	}
	@Override
	public DataObjectType<FileSystemObject> getDataObjectType(){
		return FileSystemObjectType.INSTANCE;
	}
}
