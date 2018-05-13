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
package cc.fooledit.core;
import cc.fooledit.util.*;
import com.github.chungkwong.jschememin.type.*;
import java.util.*;
import java.util.function.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class UndoableCommand extends Command{
	private final Function<ScmPairOrNil,UndoableAction> backup;
	public UndoableCommand(String name,ThrowableFunction<ScmPairOrNil,ScmObject> action,Function<ScmPairOrNil,UndoableAction> backup,String module){
		super(name,action,module);
		this.backup=backup;
	}
	public UndoableCommand(String name,List<Argument> parameters,ThrowableFunction<ScmPairOrNil,ScmObject> action,Function<ScmPairOrNil,UndoableAction> backup,String module){
		super(name,parameters,action,module);
		this.backup=backup;
	}
	public UndoableAction getUndoAction(ScmPairOrNil args){
		return backup.apply(args);
	}
}
