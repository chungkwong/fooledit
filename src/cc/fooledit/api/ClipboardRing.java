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
package cc.fooledit.api;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import javafx.scene.input.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ClipboardRing extends HistoryRing<Map<DataFormat,Object>>{
	private final Clipboard clipBoard;
	public ClipboardRing(Clipboard clipBoard){
		this.clipBoard=clipBoard;
	}
	public ClipboardRing(Clipboard clipBoard,int limit){
		super(limit);
		this.clipBoard=clipBoard;
	}
	@Override
	public void add(Map<DataFormat,Object> obj){
		super.add(obj);
		clipBoard.setContent(obj);
	}
	private void syncClipboard(){
		Map<DataFormat,Object> curr=getCurrentMap();
		if(!curr.isEmpty()&&(size()==0||!curr.equals(get(size()-1)))){
			super.add(curr);
		}
	}
	private Map<DataFormat,Object> getCurrentMap(){
		return clipBoard.getContentTypes().stream().collect(Collectors.toMap(Function.identity(),(format)->clipBoard.getContent(format)));
	}
}