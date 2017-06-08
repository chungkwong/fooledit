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
import com.github.chungkwong.json.*;
import com.github.chungkwong.jtk.*;
import com.github.chungkwong.jtk.api.*;
import com.github.chungkwong.jtk.editor.*;
import com.github.chungkwong.jtk.editor.lex.*;
import com.github.chungkwong.jtk.model.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import javafx.scene.*;
import javafx.scene.control.*;
import org.fxmisc.richtext.model.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class StructuredTextEditor implements DataEditor<TextObject>{
	private final MenuRegistry menuRegistry=new MenuRegistry();
	private final CommandRegistry commandRegistry=new CommandRegistry();
	private final KeymapRegistry keymapRegistry=new KeymapRegistry();
	private final Map<String,String> highlightFiles=new HashMap<>();
	public StructuredTextEditor(){
		menuRegistry.getMenuBar().getMenus().add(new Menu("Code"));
		addCommand("undo",(area)->area.getArea().undo());
		addCommand("redo",(area)->area.getArea().redo());
		addCommand("cut",(area)->area.getArea().cut());
		addCommand("copy",(area)->area.getArea().copy());
		addCommand("paste",(area)->area.getArea().paste());
		addCommand("select-all",(area)->area.getArea().selectAll());
		addCommand("select-word",(area)->area.getArea().selectWord());
		addCommand("select-line",(area)->area.getArea().selectLine());
		addCommand("select-paragraph",(area)->area.getArea().selectParagraph());
		addCommand("delete-next-character",(area)->area.getArea().deleteNextChar());
		addCommand("delete-previous-character",(area)->area.getArea().deletePreviousChar());
		addCommand("new-line",(area)->area.newline());
		addCommand("next-word",(area)->area.nextWord(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("previous-word",(area)->area.previousWord(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("next-character",(area)->area.getArea().nextChar(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("previous-character",(area)->area.getArea().previousChar(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("next-page",(area)->area.getArea().nextPage(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("previous-page",(area)->area.getArea().prevPage(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("end-line",(area)->area.getArea().lineEnd(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("begin-line",(area)->area.getArea().lineStart(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("end-paragraph",(area)->area.getArea().paragraphEnd(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("begin-paragraph",(area)->area.getArea().paragraphStart(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("end-file",(area)->area.getArea().end(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("begin-file",(area)->area.getArea().start(NavigationActions.SelectionPolicy.CLEAR));

		addCommand("select-next-word",(area)->area.nextWord(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-previous-word",(area)->area.previousWord(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-next-character",(area)->area.getArea().nextChar(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-previous-character",(area)->area.getArea().previousChar(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-next-page",(area)->area.getArea().nextPage(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-previous-page",(area)->area.getArea().prevPage(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-end-line",(area)->area.getArea().lineEnd(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-begin-line",(area)->area.getArea().lineStart(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-end-paragraph",(area)->area.getArea().paragraphEnd(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-begin-paragraph",(area)->area.getArea().paragraphStart(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-end-file",(area)->area.getArea().end(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-begin-file",(area)->area.getArea().start(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("deselect",(area)->area.getArea().deselect());

		keymapRegistry.registerKeys((Map<String,String>)(Object)Main.loadJSON("keymaps/code-editor/default.json"));

		Map<String,List<String>> json=(Map<String,List<String>>)(Object)Main.loadJSON("highlight.json");
		json.forEach((file,mimes)->mimes.stream().forEach((mime)->highlightFiles.put(mime,file)));
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
		Lex lex=null;
		String highlightFile=highlightFiles.get(DataObjectRegistry.getMIME(data));
		if(highlightFile!=null){
			lex=new NaiveLex();
			File file=new File(Main.getSystemPath(),"data/modes/"+highlightFile);
			try{
				LexBuilder.fromJSON(Helper.readText(file),lex);
			}catch(NullPointerException|IOException|SyntaxException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				lex=null;
			}
		}
		CodeEditor codeEditor=new CodeEditor(null,lex);
		codeEditor.textProperty().bindBidirectional(data.getText());
		return codeEditor;
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("CODE_EDITOR");
	}
}