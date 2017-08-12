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
package cc.fooledit.editor;
import cc.fooledit.api.*;
import cc.fooledit.editor.lex.*;
import cc.fooledit.editor.parser.*;
import cc.fooledit.util.*;
import com.github.chungkwong.json.*;
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
import org.antlr.v4.tool.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Demo extends Application{
	@Override
	public void start(Stage stage) throws Exception{
		//CodeEditor editor=new CodeEditor(null,getExampleLex());
		CodeEditor2 editor=new CodeEditor2(Grammar.load("/home/kwong/projects/grammars-v4/java8/Java8.g4"));
		//CodeEditor editor=new CodeEditor(new LR1Parser(getExampleGrammar()),getExampleLex());
		//editor.syntaxTree().addListener((e,o,n)->System.out.println(n));
		/*editor.setAutoCompleteProvider(AutoCompleteProvider.createSimple(Arrays.asList(
				AutoCompleteHint.create("c","c","doc: c"),
				AutoCompleteHint.create("cd","cd","doc: cd")
		)));*/
		Popup popup=new Popup();
		popup.getContent().add(new Label("hello"));
		Scene scene=new Scene(editor);
		scene.getStylesheets().add(Demo.class.getResource("highlight.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args){
		launch(args);
	}
	private static MetaLexer getExampleLex(){
		MetaLexer lex=new NaiveLexer();
		try{
			LexBuilders.fromJSON(Helper.readText(new InputStreamReader(Demo.class.getResourceAsStream("lex.json"),StandardCharsets.UTF_8)),lex);
		}catch(IOException|SyntaxException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}
		return lex;
	}
	private static ContextFreeGrammar getExampleGrammar(){
		String start="properties";
		List<ProductionRule> rules=new ArrayList<>();
		rules.add(new ProductionRule("property",new String[]{"key","value"},(o)->new Pair<>(o[0],o[1])));
		rules.add(new ProductionRule("properties",new String[]{},(o)->Collections.emptyList()));
		rules.add(new ProductionRule("properties",new String[]{"property","properties"},(o)->{
			Collection<Object> o0=(Collection<Object>)o[1];
			ArrayList<Object> list=new ArrayList<>(o0.size()+1);
			list.add(o[0]);
			list.addAll(o0);
			return list;
		}));
		Map<String,Function<String,Object>> terminals=new HashMap<>();
		terminals.put("key",(s)->s.trim());
		terminals.put("value",(s)->s);
		return new ContextFreeGrammar(start,rules,terminals);
	}
	private static final int INIT=MetaLexer.INIT;
}