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
package cc.fooledit.editor.email;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.util.function.*;
import java.util.logging.*;
import javafx.scene.*;
import javax.mail.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MailBoxEditor implements DataEditor<MailBoxObject>{
	public static final MailBoxEditor INSTANCE=new MailBoxEditor();
	private final MenuRegistry menuRegistry=Registry.ROOT.registerMenu(Activator.class);
	private final RegistryNode<String,Command> commandRegistry=Registry.ROOT.registerCommand(Activator.NAME);
	private final NavigableRegistryNode<String,String> keymapRegistry=Registry.ROOT.registerKeymap(Activator.class);
	private MailBoxEditor(){
	}
	private void addCommand(String name,Consumer<MailBoxViewer> action){
		commandRegistry.put(name,new Command(name,()->action.accept((MailBoxViewer)Main.INSTANCE.getCurrentNode()),Activator.class));
	}
	@Override
	public Node edit(MailBoxObject data,Object remark,RegistryNode<String,Object> meta){
		try{
			return new MailBoxViewer(data.getSession());
		}catch(MessagingException ex){
			Logger.getLogger(MailBoxEditor.class.getName()).log(Level.SEVERE,null,ex);
			return null;
		}
	}
	@Override
	public Object getRemark(Node node){
		return null;
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("BROWSER",Activator.class);
	}
	@Override
	public RegistryNode<String,Command> getCommandRegistry(){
		return commandRegistry;
	}
	@Override
	public NavigableRegistryNode<String,String> getKeymapRegistry(){
		return keymapRegistry;
	}
	@Override
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
}
