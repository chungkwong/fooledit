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
package com.github.chungkwong.jtk.example.text;
import com.github.chungkwong.jtk.*;
import com.github.chungkwong.jtk.api.*;
import com.github.chungkwong.jtk.editor.*;
import com.github.chungkwong.jtk.editor.lex.*;
import com.github.chungkwong.jtk.model.*;
import java.util.function.*;
import javafx.scene.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class StructuredTextEditor implements DataEditor<TextObject>{
	private final MenuRegistry menuRegistry=new MenuRegistry();
	private final CommandRegistry commandRegistry=new CommandRegistry();
	private final KeymapRegistry keymapRegistry=new KeymapRegistry();
	private final Lex lex;
	public StructuredTextEditor(Lex lex){
		this.lex=lex;
		menuRegistry.getMenuBar().getMenus().add(new Menu("Code"));
		addCommand("undo",(area)->area.getArea().undo());
		addCommand("redo",(area)->area.getArea().redo());
		addCommand("cut",(area)->area.getArea().cut());
		addCommand("copy",(area)->area.getArea().copy());
		addCommand("paste",(area)->area.getArea().paste());
		addCommand("select-all",(area)->area.getArea().selectAll());
		addCommand("select-physical-line",(area)->area.getArea().selectLine());
		addCommand("select-logical-line",(area)->area.getArea().selectParagraph());
		addCommand("delete-next-character",(area)->area.getArea().deleteNextChar());
		addCommand("select-previous-character",(area)->area.getArea().deletePreviousChar());
		keymapRegistry.registerKey("C-L","select-line");
	}
	private void addCommand(String name,Consumer<CodeEditor> action){
		commandRegistry.put(name,()->action.accept((CodeEditor)Main.INSTANCE.getCurrentNode()));
	}
	@Override
	public MenuRegistry getMenuRegistry(){
		return menuRegistry;
	}
	@Override
	public CommandRegistry getCommandRegistry(){
		return commandRegistry;
	}
	@Override
	public KeymapRegistry getKeymapRegistry(){
		return keymapRegistry;
	}
	@Override
	public Node edit(TextObject data){
		CodeEditor codeEditor=new CodeEditor(null,lex);
		data.getText().bindBidirectional(codeEditor.textProperty());
		return codeEditor;
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("CODE_EDITOR");
	}
}
