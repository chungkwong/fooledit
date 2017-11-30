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
import cc.fooledit.*;
import cc.fooledit.api.*;
import cc.fooledit.control.*;
import cc.fooledit.editor.lex.*;
import cc.fooledit.editor.parser.*;
import cc.fooledit.model.*;
import cc.fooledit.setting.*;
import cc.fooledit.util.*;
import com.github.chungkwong.jschememin.type.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javax.activation.*;
import org.antlr.v4.runtime.*;
import org.fxmisc.richtext.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class StructuredTextEditor implements DataEditor<TextObject>{
	private final MenuRegistry menuRegistry=new MenuRegistry(TextEditorModule.NAME);
	private final CommandRegistry commandRegistry=new CommandRegistry();
	private final KeymapRegistry keymapRegistry=new KeymapRegistry();
	private final Map<String,Language> languages=new HashMap<>();
	private final HistoryRing<String> clips=new HistoryRing<>();
	public StructuredTextEditor(){
		menuRegistry.registerDynamicMenu("reload",(items)->{
			DataObject curr=Main.getCurrentDataObject();
			String url=(String)curr.getProperties().get(DataObject.URI);
			String currCharset=(String)curr.getProperties().getOrDefault("CHARSET","UTF-8");
			if(url!=null){
				ToggleGroup group=new ToggleGroup();
				Consumer<Charset> reload=(set)->{
					try{
						MimeType mime=new MimeType((String)curr.getProperties().get(DataObject.MIME));
						mime.setParameter("charset",set.name());
						Main.show(DataObjectRegistry.readFrom(new URL(url),TextObjectType.INSTANCE,mime));
					}catch(Exception ex){
						Logger.getLogger(TextEditorModule.class.getName()).log(Level.SEVERE,null,ex);
					}
				};
				try(InputStream in=FoolURLConnection.open(new URL(url)).getInputStream()){
					items.setAll(CharsetDetector.probeCharsets(in).stream()
							.map((set)->createCharsetItem(set,reload,group,currCharset)).collect(Collectors.toList()));
				}catch(IOException ex){
					Logger.getGlobal().log(Level.INFO,null,ex);
					items.setAll(Charset.availableCharsets().values().stream()
							.map((set)->createCharsetItem(set,reload,group,currCharset)).collect(Collectors.toList()));
				}
			}
		});
		menuRegistry.registerDynamicMenu("charset",(items)->{
			TextObject curr=(TextObject)Main.getCurrentDataObject();
			String currCharset=(String)curr.getProperties().getOrDefault("CHARSET","UTF-8");
			ToggleGroup group=new ToggleGroup();
			items.setAll(Charset.availableCharsets().values().stream()
					.map((set)->createCharsetItem(set,(s)->{
						curr.getProperties().put("CHARSET",s.name());
					},group,currCharset)).collect(Collectors.toList()));
		});

		addCommand("undo",(area)->area.getArea().undo());
		addCommand("redo",(area)->area.getArea().redo());
		addCommand("cut",(area)->area.getArea().cut());
		addCommand("copy",(area)->area.getArea().copy());
		addCommand("paste",(area)->area.getArea().paste());
		addCommand("swap-anchor-and-caret",(area)->area.swapAnchorAndCaret());
		addCommand("select-all",(area)->area.getArea().selectAll());
		addCommand("select-word",(area)->area.selectWord());
		addCommand("select-line",(area)->area.getArea().selectLine());
		addCommand("select-paragraph",(area)->area.getArea().selectParagraph());
		addCommand("delete-selection-or-next-character",(area)->area.delete());
		addCommand("delete-selection-or-previous-character",(area)->area.backspace());
		addCommand("delete-next-character",(area)->area.deleteNextChar());
		addCommand("delete-previous-character",(area)->area.deletePreviousChar());
		addCommand("delete-next-word",(area)->area.deleteNextWord());
		addCommand("delete-previous-word",(area)->area.deletePreviousWord());
		addCommand("delete-line",(area)->area.deleteLine());
		addCommand("clear",(area)->area.getArea().clear());
		addCommand("new-line",(area)->area.newline());
		addCommand("new-line-no-indent",(area)->area.getArea().replaceSelection("\n"));
		addCommand("indent",(area)->area.getArea().replaceSelection("\t"));
		addCommand("move-to-next-word",(area)->area.nextWord(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("move-to-previous-word",(area)->area.previousWord(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("move-to-next-character",(area)->area.getArea().nextChar(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("move-to-previous-character",(area)->area.getArea().previousChar(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("move-to-next-line",(area)->area.nextLine(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("move-to-previous-line",(area)->area.previousLine(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("move-to-next-page",(area)->area.getArea().nextPage(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("move-to-previous-page",(area)->area.getArea().prevPage(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("move-to-line-end",(area)->area.getArea().lineEnd(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("move-to-line-begin",(area)->area.getArea().lineStart(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("move-to-paragraph-end",(area)->area.getArea().paragraphEnd(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("move-to-paragraph-begin",(area)->area.getArea().paragraphStart(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("move-to-file-end",(area)->area.getArea().end(NavigationActions.SelectionPolicy.CLEAR));
		addCommand("move-to-file-begin",(area)->area.getArea().start(NavigationActions.SelectionPolicy.CLEAR));

		addCommand("select-to-next-word",(area)->area.nextWord(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-to-previous-word",(area)->area.previousWord(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-to-next-character",(area)->area.getArea().nextChar(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-to-previous-character",(area)->area.getArea().previousChar(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-to-next-line",(area)->area.nextLine(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-to-previous-line",(area)->area.previousLine(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-to-next-page",(area)->area.getArea().nextPage(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-to-previous-page",(area)->area.getArea().prevPage(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-to-line-end",(area)->area.getArea().lineEnd(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-to-line-begin",(area)->area.getArea().lineStart(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-to-paragraph-end",(area)->area.getArea().paragraphEnd(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-to-paragraph-begin",(area)->area.getArea().paragraphStart(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-to-file-end",(area)->area.getArea().end(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("select-to-file-begin",(area)->area.getArea().start(NavigationActions.SelectionPolicy.ADJUST));
		addCommand("deselect",(area)->area.getArea().deselect());
		addCommand("reverse-selection",(area)->area.reverseSelection());
		addCommand("next-selection",(area)->area.nextSelection());
		addCommand("previous-selection",(area)->area.previousSelection());
		addCommand("select-node",(area)->area.selectNode());
		addCommand("select-parent-node",(area)->area.selectParentNode());
		addCommand("select-child-node",(area)->area.selectChildNode());
		addCommand("select-next-node",(area)->area.selectNextNode());
		addCommand("select-previous-node",(area)->area.selectPreviousNode());
		addCommand("select-first-node",(area)->area.selectFirstNode());
		addCommand("select-last-node",(area)->area.selectLastNode());

		addCommand("to-lowercase",(area)->area.transform(String::toLowerCase));
		addCommand("to-uppercase",(area)->area.transform(String::toUpperCase));
		addCommand("encode-url",(area)->area.transform(StructuredTextEditor::encodeURL));
		addCommand("decode-url",(area)->area.transform(StructuredTextEditor::decodeURL));
		addCommand("scroll-to-top",(area)->area.getArea().showParagraphAtTop(area.getArea().getCurrentParagraph()));
		addCommand("scroll-to-bottom",(area)->area.getArea().showParagraphAtBottom(area.getArea().getCurrentParagraph()));
		addCommand("move-to-paragraph",Collections.singletonList("line"),(args,area)->{
				int index=SchemeConverter.toInteger(ScmList.first(args));
				area.getArea().moveTo(index,Math.min(area.getArea().getCaretColumn(),area.getArea().getParagraphLength(index)));
				area.getArea().showParagraphInViewport(index);
				return null;
		});
		addCommand("move-to-column",Collections.singletonList("line"),(args,area)->{
				area.getArea().moveTo(area.getArea().getCurrentParagraph(),SchemeConverter.toInteger(ScmList.first(args)));
				return null;
		});
		addCommand("move-to-position",Collections.singletonList("line"),(args,area)->{
				area.getArea().moveTo(SchemeConverter.toInteger(ScmList.first(args)));
				area.getArea().showParagraphInViewport(area.getArea().getCurrentParagraph());
				return null;
		});

		addCommand("current-paragraph",Collections.emptyList(),(args,area)->{
				return ScmInteger.valueOf(area.getArea().getCurrentParagraph());
		});
		addCommand("current-column",Collections.emptyList(),(args,area)->{
				return ScmInteger.valueOf(area.getArea().getCaretColumn());
		});
		addCommand("current-caret",Collections.emptyList(),(args,area)->{
				return ScmInteger.valueOf(area.getArea().getCaretPosition());
		});
		addCommand("current-anchor",Collections.emptyList(),(args,area)->{
				return ScmInteger.valueOf(area.getArea().getAnchor());
		});
		addCommand("->position",Arrays.asList("line","column"),(args,area)->{
				return ScmInteger.valueOf(area.getArea().getAbsolutePosition(SchemeConverter.toInteger(ScmList.first(args)),SchemeConverter.toInteger(ScmList.second(args))));
		});
		addCommand("length",Collections.emptyList(),(args,area)->{
				return ScmInteger.valueOf(area.getArea().getLength());
		});
		addCommand("text",Collections.emptyList(),(args,area)->{
				int argc=ScmList.getLength(args);
				int start=argc>=1?SchemeConverter.toInteger(ScmList.first(args)):0;
				int end=argc>=2?SchemeConverter.toInteger(ScmList.second(args)):area.getArea().getLength();
				return new ScmString(area.getArea().getText(start,end));
		});
		clips.registerComamnds("clip",()->getCurrentEditor().getArea().getSelectedText(),(clip)->getCurrentEditor().getArea().replaceSelection(clip),commandRegistry);
		addCommand("clips",(area)->area.setAutoCompleteProvider(AutoCompleteProvider.createFixed(
				clips.stream().map((c)->AutoCompleteHint.create(c,c,c)).collect(Collectors.toList())),true));
		addCommand("highlight",(area)->area.selections().add(area.createSelection(area.getArea().getSelection())));
		addCommand("unhighlight",(area)->area.unhighlight());
		addCommand("find-string",Collections.singletonList("target"),(args,area)->{
				return ScmInteger.valueOf(area.find(SchemeConverter.toString(ScmList.first(args))));
		});
		addCommand("find-regex",Collections.singletonList("target"),(args,area)->{
				return ScmInteger.valueOf(area.findRegex(SchemeConverter.toString(ScmList.first(args))));
		});
		addCommand("replace-string",Collections.singletonList("replacement"),(args,area)->{
				String replacement=SchemeConverter.toString(ScmList.first(args));
				area.replace((t)->replacement);
				return null;
		});
		addCommand("replace",Collections.singletonList("function"),(args,area)->{
				ScmProcedure function=(ScmProcedure)(ScmList.first(args));
				area.replace((t)->((ScmString)function.call(ScmList.toList(new ScmString(t)))).getValue());
				return null;
		});
		addCommand("syntax-tree",Collections.emptyList(),(args,area)->{
				OptionDialog.showDialog(new ParseTreeViewer((ParserRuleContext)area.syntaxTree()));
				return null;
//return new ScmJavaObject(area.syntaxTree());
		});
		keymapRegistry.registerKeys((Map<String,String>)(Object)Main.loadJSON((File)SettingManager.getOrCreate(TextEditorModule.NAME).get("keymap-file",null)));

		try{
			//scene.setUserAgentStylesheet("com/github/chungkwong/jtk/dark.css");
			Main.getScene().getStylesheets().add(((File)SettingManager.getOrCreate(TextEditorModule.NAME).get("stylesheet-file",null)).toURI().toURL().toString());
		}catch(MalformedURLException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
		}

		List<Map<String,Object>> json=((Map<String,List<Map<String,Object>>>)(Object)Main.loadJSON(new File(Main.getModulePath(TextEditorModule.NAME),"modes.json"))).get("languages");
		json.stream().map((m)->Language.fromJSON(m)).forEach((l)->Arrays.stream(l.getMimeTypes()).forEach((mime)->languages.put(mime,l)));
	}
	private void addCommand(String name,Consumer<CodeEditor> action){
		commandRegistry.put(name,()->action.accept(getCurrentEditor()),TextEditorModule.NAME);
	}
	private void addCommand(String name,List<String> parameters,BiFunction<ScmPairOrNil,CodeEditor,ScmObject> action){
		commandRegistry.put(name,new Command(name,parameters,(args)->action.apply(args,getCurrentEditor()),TextEditorModule.NAME));
	}
	private CodeEditor getCurrentEditor(){
		return (CodeEditor)Main.INSTANCE.getCurrentNode();
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
	public Object getRemark(Node node){
		CodeEditor editor=(CodeEditor)node;
		List<Integer> remark=new ArrayList<>(editor.selections().size()*2+2);
		remark.add(editor.getArea().getAnchor());
		remark.add(editor.getArea().getCaretPosition());
		editor.selections().forEach((range)->{
			remark.add(range.getKey().getOffset());
			remark.add(range.getValue().getOffset());
		});
		return remark;
	}
	@Override
	public KeymapRegistry getKeymapRegistry(){
		return keymapRegistry;
	}
	@Override
	public Node edit(TextObject data,Object remark){
		CodeEditor editor=(CodeEditor)edit(data);
		if(remark instanceof List){
			List<Number> pair=(List<Number>)remark;
			editor.getArea().selectRange(pair.get(0).intValue(),pair.get(1).intValue());
			for(int i=2;i<pair.size();i+=2)
				editor.selections().add(editor.createSelection(pair.get(i).intValue(),pair.get(i+1).intValue()));
		}
		return editor;
	}
	@Override
	public Node edit(TextObject data){
		MetaLexer lex=null;
		Language language;
		try{
			language=languages.get(new MimeType(data.getProperties().getOrDefault(DataObject.MIME,"text/plain")).getBaseType());
		}catch(MimeTypeParseException ex){
			language=null;
		}
		Highlighter highlighter=language!=null?language.getTokenHighlighter():null;
		ParserBuilder parserBuilder=language!=null?language.getParserBuilder():null;
		CodeEditor codeEditor=new CodeEditor(parserBuilder,highlighter);
		codeEditor.textProperty().bindBidirectional(data.getText());
		return codeEditor;
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("CODE_EDITOR",TextEditorModule.NAME);
	}
	private static String encodeURL(String url){
		try{
			return URLEncoder.encode(url,"UTF-8");
		}catch(UnsupportedEncodingException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return url;
		}
	}
	private static String decodeURL(String url){
		try{
			return URLDecoder.decode(url,"UTF-8");
		}catch(UnsupportedEncodingException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return url;
		}
	}
	private static MenuItem createCharsetItem(Charset charset,Consumer<Charset> action,ToggleGroup group,String def){
		RadioMenuItem radioMenuItem=new RadioMenuItem(charset.displayName());
		radioMenuItem.setToggleGroup(group);
		if(charset.name().equalsIgnoreCase(def))
			group.selectToggle(radioMenuItem);
		radioMenuItem.setOnAction((e)->action.accept(charset));
		return radioMenuItem;
	}
}