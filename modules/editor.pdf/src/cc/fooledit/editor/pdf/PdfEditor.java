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
package cc.fooledit.editor.pdf;
import cc.fooledit.*;
import cc.fooledit.core.*;
import cc.fooledit.editor.media.*;
import cc.fooledit.spi.*;
import java.util.function.*;
import javafx.scene.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PdfEditor implements DataEditor<PdfObject>{
	public static final PdfEditor INSTANCE=new PdfEditor();
	private final MenuRegistry menuRegistry=Registry.ROOT.registerMenu(PdfModule.NAME);
	private final RegistryNode<String,Command,String> commandRegistry=Registry.ROOT.registerCommand(PdfModule.NAME);
	private final NavigableRegistryNode<String,String,String> keymapRegistry=Registry.ROOT.registerKeymap(PdfModule.NAME);
	private PdfEditor(){

	}
	@Override
	public Node edit(PdfObject data,Object remark,RegistryNode<String,Object,String> meta){
		return new PdfViewer(data.getDocument());
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("PDF_EDITOR",PdfModule.NAME);
	}
	private void addCommand(String name,Consumer<PdfViewer> action){
		commandRegistry.addChild(name,new Command(name,()->action.accept((PdfViewer)Main.INSTANCE.getCurrentNode()),MediaEditorModule.NAME));
	}
	@Override
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
	@Override
	public RegistryNode<String,Command,String> getCommandRegistry(){
		return commandRegistry;
	}
	@Override
	public NavigableRegistryNode<String,String,String> getKeymapRegistry(){
		return keymapRegistry;
	}
}
