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
package cc.fooledit.util;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class UndoManager{
	private final LinkedList<UndoableAction> actions=new LinkedList<>();
	private int curr=0;
	public UndoManager(){

	}
	public void record(UndoableAction action){
		actions.add(action);
		curr=actions.size();
	}
	public int getAllowedUndoCount(){
		return curr;
	}
	public void undo(int count){
		UndoableAction action;
		if(count==1){
			action=actions.get(curr-1);
		}else{
			action=UndoableAction.compose(actions.subList(curr-count,curr));
		}
		action.invert();
		actions.add(action.inverse());
		curr-=count;
	}
	public int getAllowedRedoCount(){
		return actions.size()-curr;
	}
	public void redo(int count){
		UndoableAction action;
		if(count==1){
			action=actions.get(curr);
		}else{
			action=UndoableAction.compose(actions.subList(curr,curr+count));
		}
		actions.add(action);
		curr+=count;
	}
}
