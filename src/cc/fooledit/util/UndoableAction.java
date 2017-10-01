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
public class UndoableAction{
	private final Runnable forward;
	private final Runnable backward;
	public UndoableAction(Runnable forward,Runnable backward){
		this.forward=forward;
		this.backward=backward;
	}
	public void invert(){
		backward.run();
	}
	public void execute(){
		forward.run();
	}
	public UndoableAction inverse(){
		return new UndoableAction(backward,forward);
	}
	public static UndoableAction compose(List<UndoableAction> actions){
		return new UndoableAction(()->actions.forEach((a)->a.execute()),()->{
			for(ListIterator<UndoableAction> iter=actions.listIterator(actions.size());iter.hasPrevious();){
				iter.previous().invert();
			}
		});
	}
}
