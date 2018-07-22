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
package cc.fooledit.editor.ps;
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
public class PsEditor implements DataEditor<PsObject>{
	public static final PsEditor INSTANCE=new PsEditor();
	private final MenuRegistry menuRegistry=Registry.ROOT.registerMenu(Activator.NAME);
	private final RegistryNode<String,Command> commandRegistry=Registry.ROOT.registerCommand(Activator.NAME);
	private final NavigableRegistryNode<String,String> keymapRegistry=Registry.ROOT.registerKeymap(Activator.NAME);
	private PsEditor(){
	}
	@Override
	public Node edit(PsObject data,Object remark,RegistryNode<String,Object> meta){
		return new PsViewer(data.getDocument());
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("PS_EDITOR",Activator.NAME);
	}
	private void addCommand(String name,Consumer<PsViewer> action){
		commandRegistry.put(name,new Command(name,()->action.accept((PsViewer)Main.INSTANCE.getCurrentNode()),Activator.NAME));
	}
	private void addCommand(String name,List<Argument> parameters,BiFunction<Object[],PsViewer,Object> action){
		commandRegistry.put(name,new Command(name,parameters,(args)->action.apply(args,(PsViewer)Main.INSTANCE.getCurrentDataEditor()),Activator.NAME));
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
