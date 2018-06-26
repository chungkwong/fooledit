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
package cc.fooledit.editor.chm;
import cc.fooledit.*;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.util.*;
import java.util.function.*;
import javafx.scene.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ChmEditor implements DataEditor<ChmObject>{
	public static final ChmEditor INSTANCE=new ChmEditor();
	private final MenuRegistry menuRegistry=Registry.ROOT.registerMenu(ChmModule.NAME);
	private final RegistryNode<String,Command> commandRegistry=Registry.ROOT.registerCommand(ChmModule.NAME);
	private final NavigableRegistryNode<String,String> keymapRegistry=Registry.ROOT.registerKeymap(ChmModule.NAME);
	private ChmEditor(){
		addCommand("current-scale",Collections.emptyList(),(args,viewer)->viewer.getScale());
		addCommand("zoom",Arrays.asList(new Argument("SCALE")),(args,viewer)->{
			viewer.setScale(((Number)args[0]).floatValue());
			return null;
		});
	}
	@Override
	public Node edit(ChmObject data,Object remark,RegistryNode<String,Object> meta){
		return new ChmViewer(data.getDocument());
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("CHM_EDITOR",ChmModule.NAME);
	}
	private void addCommand(String name,Consumer<ChmViewer> action){
		commandRegistry.put(name,new Command(name,()->action.accept((ChmViewer)Main.INSTANCE.getCurrentNode()),ChmModule.NAME));
	}
	private void addCommand(String name,List<Argument> parameters,BiFunction<Object[],ChmViewer,Object> action){
		commandRegistry.put(name,new Command(name,parameters,(args)->action.apply(args,(ChmViewer)Main.INSTANCE.getCurrentDataEditor()),ChmModule.NAME));
	}
	@Override
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
	@Override
	public RegistryNode<String,Command> getCommandRegistry(){
		return commandRegistry;
	}
	@Override
	public NavigableRegistryNode<String,String> getKeymapRegistry(){
		return keymapRegistry;
	}
}
