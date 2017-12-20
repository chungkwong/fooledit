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
import cc.fooledit.spi.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MessageRegistry{
	public static String getString(String key,String module){
		RegistryNode<String,RegistryNode,String> mod=(RegistryNode<String,RegistryNode,String>)Registry.ROOT.getOrCreateChild(module);
		if(!mod.hasChild(CoreModule.MESSAGE_REGISTRY_NAME)){
			Registry.ROOT.registerMessage(module);
		}
		return ((RegistryNode<String,String,String>)mod.getOrCreateChild(CoreModule.MESSAGE_REGISTRY_NAME)).getChildOrDefault(key,key);
	}
	public static void addBundle(String module){
		Registry.ROOT.registerMessage(module);
	}
}
