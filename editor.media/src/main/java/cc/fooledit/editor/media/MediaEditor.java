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
package cc.fooledit.editor.media;
import cc.fooledit.*;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.util.function.*;
import java.util.logging.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.media.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MediaEditor implements DataEditor<MediaObject>{
	public static final MediaEditor INSTANCE=new MediaEditor();
	private final MenuRegistry menuRegistry=Registry.ROOT.registerMenu(MediaEditorModule.NAME);
	private final RegistryNode<String,Command> commandRegistry=Registry.ROOT.registerCommand(MediaEditorModule.NAME);
	private final NavigableRegistryNode<String,String> keymapRegistry=Registry.ROOT.registerKeymap(MediaEditorModule.NAME);
	private MediaEditor(){
		addCommand("play",(player)->player.play());
		addCommand("pause",(player)->player.pause());
	}
	@Override
	public Node edit(MediaObject data,Object remark,RegistryNode<String,Object> meta){
		MediaViewer editor;
		try{
			editor=new FxMediaViewer(new MediaPlayer(new Media(data.getProperty().getValue())));
		}catch(MediaException ex){
			Logger.getGlobal().log(Level.INFO,"",ex);
		}
		editor=new VlcMediaViewer(data.getProperty().getValue());
		editor.statusProperty().addListener((e,o,n)->Main.INSTANCE.getNotifier().notify(MessageRegistry.getString(n.toString(),MediaEditorModule.NAME)));
		editor.play();
		return new ScrollPane((Node)editor);
	}
	@Override
	public void dispose(Node node){
		((MediaViewer)((ScrollPane)node).getContent()).dispose();
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("MEDIA_PLAYER",MediaEditorModule.NAME);
	}
	private void addCommand(String name,Consumer<MediaViewer> action){
		commandRegistry.put(name,new Command(name,()->action.accept((MediaViewer)((ScrollPane)Main.INSTANCE.getCurrentNode()).getContent()),MediaEditorModule.NAME));
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
