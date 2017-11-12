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
package cc.fooledit.example.text;
import cc.fooledit.api.*;
import cc.fooledit.model.*;
import cc.fooledit.util.*;
import javafx.scene.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PlainTextEditor implements DataEditor<TextObject>{
	private final MenuRegistry menuRegistry=new MenuRegistry();
	public PlainTextEditor(){
		menuRegistry.getMenuBar().getMenus().add(new Menu("Text"));
	}
	@Override
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
	public Node edit(TextObject obj){
		TextArea textArea=new TextArea();
		textArea.textProperty().bindBidirectional(obj.getText());
		return textArea;
	}
	@Override
	public CommandRegistry getCommandRegistry(){
		return COMMANDS.get();
	}
	private static final Cache<CommandRegistry> COMMANDS=new Cache<>(()->{
		CommandRegistry registry=new CommandRegistry();
		return registry;
	});
	@Override
	public String getName(){
		return MessageRegistry.getString("TEXT_EDITOR");
	}
}
