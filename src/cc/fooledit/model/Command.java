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
package cc.fooledit.model;
import cc.fooledit.api.*;
import com.github.chungkwong.jschememin.type.*;
import java.util.logging.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Command{
	private final String name;
	private final ThrowableConsumer<ScmPairOrNil> action;
	public Command(String name,ThrowableConsumer<ScmPairOrNil> action){
		this.action=action;
		this.name=name;
	}
	public String getDisplayName(){
		return name;
	}
	public void accept(ScmPairOrNil t){
		try{
			action.accept(t);
		}catch(Exception ex){
			Logger.getGlobal().log(Level.SEVERE,name+MessageRegistry.getString("FAILED"),ex);
		}
	}
}