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
package cc.fooledit.editor.djvu;
import cc.fooledit.*;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import com.github.chungkwong.jschememin.type.*;
import java.util.*;
import java.util.function.*;
import javafx.scene.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DjvuEditor implements DataEditor<DjvuObject>{
	public static final DjvuEditor INSTANCE=new DjvuEditor();
	private final MenuRegistry menuRegistry=Registry.ROOT.registerMenu(DjvuModule.NAME);
	private final RegistryNode<String,Command> commandRegistry=Registry.ROOT.registerCommand(DjvuModule.NAME);
	private final NavigableRegistryNode<String,String> keymapRegistry=Registry.ROOT.registerKeymap(DjvuModule.NAME);
	private DjvuEditor(){
		addCommand("current-scale",Collections.emptyList(),(args,viewer)->ScmFloatingPointNumber.valueOf(viewer.getScale()));
		addCommand("zoom",Arrays.asList(new Argument("SCALE")),(args,viewer)->{
			viewer.setScale((float)((ScmComplex)SchemeConverter.toScheme(ScmList.first(args))).toScmReal().toDouble());
			return null;
		});
		addCommand("rotate",Arrays.asList(new Argument("DEGREE")),(args,viewer)->{
			viewer.setRotate(((ScmComplex)SchemeConverter.toScheme(ScmList.first(args))).toScmReal().toDouble());
			return null;
		});
	}
	@Override
	public Node edit(DjvuObject data,Object remark,RegistryNode<String,Object> meta){
		return new DjvuViewer(data.getDocument());
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("DVI_EDITOR",DjvuModule.NAME);
	}
	private void addCommand(String name,Consumer<DjvuViewer> action){
		commandRegistry.put(name,new Command(name,()->action.accept((DjvuViewer)Main.INSTANCE.getCurrentNode()),DjvuModule.NAME));
	}
	private void addCommand(String name,List<Argument> parameters,BiFunction<ScmPairOrNil,DjvuViewer,ScmObject> action){
		commandRegistry.put(name,new Command(name,parameters,(args)->action.apply(args,(DjvuViewer)Main.INSTANCE.getCurrentDataEditor()),DjvuModule.NAME));
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
