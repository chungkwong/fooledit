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
public class CoreModule{
	public static final String NAME="core";
	public static final String CLIP_REGISTRY_NAME="clip";
	public static final String COMMAND_REGISTRY_NAME="command";
	public static final String DATA_OBJECT_REGISTRY_NAME="data_object";
	public static final String DATA_OBJECT_TYPE_REGISTRY_NAME="data_object_type";
	public static final String EVENT_REGISTRY_NAME="event";
	public static final String HISTORY_REGISTRY_NAME="history";
	public static final String MODULE_REGISTRY_NAME="module";
	public static final String KEYMAP_REGISTRY_NAME="keymap";
	public static final String LOCALE_REGISTRY_NAME="locale";
	public static final String MENU_REGISTRY_NAME="menu";
	public static final RegistryNode<Object> REGISTRY=new SimpleRegistryNode<>(NAME,Registry.ROOT);
	public static final RegistryNode<Module> CLIP_REGISTRY=new SimpleRegistryNode<>(CLIP_REGISTRY_NAME,REGISTRY);
	public static final RegistryNode<Module> COMMAND_REGISTRY=new SimpleRegistryNode<>(COMMAND_REGISTRY_NAME,REGISTRY);
	public static final RegistryNode<Module> DATA_OBJECT_REGISTRY=new SimpleRegistryNode<>(DATA_OBJECT_REGISTRY_NAME,REGISTRY);
	public static final RegistryNode<Module> DATA_OBJECT_TYPE_REGISTRY=new SimpleRegistryNode<>(DATA_OBJECT_TYPE_REGISTRY_NAME,REGISTRY);
	public static final RegistryNode<Module> EVENT_REGISTRY=new SimpleRegistryNode<>(EVENT_REGISTRY_NAME,REGISTRY);
	public static final RegistryNode<Module> HISTORY_REGISTRY=new SimpleRegistryNode<>(HISTORY_REGISTRY_NAME,REGISTRY);
	public static final RegistryNode<Module> MODULE_REGISTRY=new SimpleRegistryNode<>(MODULE_REGISTRY_NAME,REGISTRY);
	public static final RegistryNode<Module> KEYMAP_REGISTRY=new SimpleRegistryNode<>(KEYMAP_REGISTRY_NAME,REGISTRY);
	public static final RegistryNode<Module> LOCALE_REGISTRY=new SimpleRegistryNode<>(LOCALE_REGISTRY_NAME,REGISTRY);
	public static final RegistryNode<Module> MENU_REGISTRY=new SimpleRegistryNode<>(MENU_REGISTRY_NAME,REGISTRY);
	public static void onLoad(){
		Registry.ROOT.addChild(REGISTRY);
		REGISTRY.addChild(CLIP_REGISTRY);
		REGISTRY.addChild(COMMAND_REGISTRY);
		REGISTRY.addChild(DATA_OBJECT_REGISTRY);
		REGISTRY.addChild(DATA_OBJECT_TYPE_REGISTRY);
		REGISTRY.addChild(EVENT_REGISTRY);
		REGISTRY.addChild(HISTORY_REGISTRY);
		REGISTRY.addChild(MODULE_REGISTRY);
		REGISTRY.addChild(KEYMAP_REGISTRY);
		REGISTRY.addChild(LOCALE_REGISTRY);
		REGISTRY.addChild(MENU_REGISTRY);
	}
	public static void onUnLoad(){

	}
}
