/*
 * Copyright (C) 2018 Chan Chung Kwong <1m02math@126.com>
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
import cc.fooledit.core.*;
import java.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class LoaderValue{
	private final String module;
	private LoaderValue(String module){
		this.module=module;
	}
	public void loadValue(){
		ModuleRegistry.ensureLoaded(module);
	}
	public static LoaderValue create(String module){
		return new LoaderValue(module);
	}
	@Override
	public boolean equals(Object obj){
		return obj instanceof LoaderValue&&((LoaderValue)obj).module.equals(module);
	}
	@Override
	public int hashCode(){
		int hash=5;
		hash=53*hash+Objects.hashCode(this.module);
		return hash;
	}
}
