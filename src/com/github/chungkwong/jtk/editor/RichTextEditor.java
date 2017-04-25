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
package com.github.chungkwong.jtk.editor;
import com.github.chungkwong.json.*;
import com.github.chungkwong.jtk.api.*;
import com.github.chungkwong.jtk.control.*;
import com.github.chungkwong.jtk.editor.lex.*;
import com.github.chungkwong.jtk.editor.parser.*;
import com.github.chungkwong.jtk.util.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import javafx.application.*;
import static javafx.application.Application.launch;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import org.fxmisc.flowless.*;
import org.fxmisc.richtext.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class RichTextEditor extends Application{
	@Override
	public void start(Stage stage) throws Exception{
		CodeArea editor=new CodeArea();
		editor.setParagraphGraphicFactory(LineNumberFactory.get(editor));
		Lex lex=getExampleLex();
		new SyntaxHighlightSupport(lex).apply(editor);
		new CompleteSupport(AutoCompleteProvider.createSimple(Arrays.asList(
				AutoCompleteHint.create("c","c","doc: c"),
				AutoCompleteHint.create("cd","cd","doc: cd")
		))).apply(editor);
		Popup popup=new Popup();
		popup.getContent().add(new Label("hello"));
		Scene scene=new Scene(new VirtualizedScrollPane(editor));
		//NaiveParser parser=new NaiveParser(getExampleGrammar());
		scene.getStylesheets().add(RichTextEditor.class.getResource("highlight.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
		editor.requestFocus();
		/*stage.focusedProperty().addListener((e,o,n)->{
			if(n==false){
				System.out.println(parser.parse(lex.split(editor.getText())));
			}
		});*/
	}
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	private static Lex getExampleLex(){
		NaiveLex lex=new NaiveLex();
		try{
			LexBuilder.fromJSON(Helper.readText(new InputStreamReader(RichTextEditor.class.getResourceAsStream("lex.json"),StandardCharsets.UTF_8)),lex);
		}catch(IOException|SyntaxException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
		return lex;
	}
	private static ContextFreeGrammar getExampleGrammar(){
		String start="root";
		List<ProductionRule> rules=new ArrayList<>();
		rules.add(new ProductionRule("property",new String[]{"key","value"},(o)->new Pair<>(o[0],o[1])));
		rules.add(new ProductionRule("properties",new String[]{},(o)->new LinkedList<Object>()));
		rules.add(new ProductionRule("properties",new String[]{"properties","property"},(o)->{
			((LinkedList)o[0]).add(o[1]);
			return o[0];
		}));
		Map<String,Function<String,Object>> terminals=new HashMap<>();
		terminals.put("key",(s)->s.trim());
		terminals.put("value",(s)->s);
		return new ContextFreeGrammar(start,rules,terminals);
	}
	private static final int INIT=Lex.INIT;
}