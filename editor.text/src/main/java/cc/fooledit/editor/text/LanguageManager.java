/*
 * Copyright (C) 2018 Chan Chung Kwong
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
package cc.fooledit.editor.text;
import cc.fooledit.editor.text.parser.*;
import cc.fooledit.util.*;
import java.util.*;
import javafx.scene.control.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import org.fxmisc.richtext.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class LanguageManager{
	private ParserBuilder parserBuilder;
	private Highlighter highlighter;
	private final CodeEditor editor;
	public LanguageManager(CodeEditor editor){
		this.editor=editor;
		RealTimeTask<String> task=new RealTimeTask<>((text)->{
			this.syntaxTree=null;
			if(highlighter!=null){
				highlighter.highlight(editor);
			}
		});
		editor.getArea().textProperty().addListener((e,o,n)->task.summit(n));
	}
	public Highlighter getHighlighter(){
		return highlighter;
	}
	public void setHighlighter(Highlighter highlighter){
		this.highlighter=highlighter;
	}
	public ParserBuilder getParserBuilder(){
		return parserBuilder;
	}
	public void setParserBuilder(ParserBuilder parserBuilder){
		this.parserBuilder=parserBuilder;
	}
	private List<? extends org.antlr.v4.runtime.Token> tokens;
	void cache(List<? extends org.antlr.v4.runtime.Token> tokens){
		this.tokens=tokens;
	}
	private Object syntaxTree;
	public Object syntaxTree(){
		if(syntaxTree==null&&parserBuilder!=null){
			syntaxTree=parserBuilder.parse(tokens);
		}
		return syntaxTree;
	}
	public void selectNode(){
		IndexRange oldselection=editor.getArea().getSelection();
		ParseTree node=getSurroundingNode(oldselection.getStart(),oldselection.getEnd());
		Interval newselection=node.getSourceInterval();
		editor.getArea().selectRange(tokens.get(newselection.a).getStartIndex(),tokens.get(newselection.b).getStopIndex()+1);
	}
	public void selectParentNode(){
		IndexRange oldselection=editor.getArea().getSelection();
		ParseTree node=getOuterNode(getSurroundingNode(oldselection.getStart(),oldselection.getEnd()));
		if(node!=null){
			Interval newselection=node.getSourceInterval();
			editor.getArea().selectRange(tokens.get(newselection.a).getStartIndex(),tokens.get(newselection.b).getStopIndex()+1);
		}
	}
	public void selectChildNode(){
		ParseTree node=getSurroundingNode(editor.getArea().getSelection().getStart(),editor.getArea().getSelection().getEnd());
		Interval oldInterval=node.getSourceInterval();
		while(node.getChildCount()>0){
			node=node.getChild(0);
			Interval newInterval=node.getSourceInterval();
			if(newInterval.a!=oldInterval.a||newInterval.b!=oldInterval.b){
				editor.getArea().selectRange(tokens.get(newInterval.a).getStartIndex(),tokens.get(newInterval.b).getStopIndex()+1);
				return;
			}
		}
	}
	public void selectPreviousNode(){
		IndexRange oldselection=editor.getArea().getSelection();
		ParseTree node=getSurroundingNode(oldselection.getStart(),oldselection.getEnd());
		ParseTree parent=getOuterNode(node);
		if(node!=null){
			for(int i=0;i<parent.getChildCount();i++){
				if(parent.getChild(i).getSourceInterval().equals(node.getSourceInterval())&&i>0){
					Interval newselection=parent.getChild(i-1).getSourceInterval();
					editor.getArea().selectRange(tokens.get(newselection.a).getStartIndex(),tokens.get(newselection.b).getStopIndex()+1);
				}
			}
		}
	}
	public void selectNextNode(){
		IndexRange oldselection=editor.getArea().getSelection();
		ParseTree node=getSurroundingNode(oldselection.getStart(),oldselection.getEnd());
		ParseTree parent=getOuterNode(node);
		if(parent!=null){
			for(int i=0;i<parent.getChildCount();i++){
				if(parent.getChild(i).getSourceInterval().equals(node.getSourceInterval())&&i+1<parent.getChildCount()){
					Interval newselection=parent.getChild(i+1).getSourceInterval();
					editor.getArea().selectRange(tokens.get(newselection.a).getStartIndex(),tokens.get(newselection.b).getStopIndex()+1);
				}
			}
		}
	}
	public void selectFirstNode(){
		IndexRange oldselection=editor.getArea().getSelection();
		ParseTree node=getSurroundingNode(oldselection.getStart(),oldselection.getEnd());
		ParseTree parent=getOuterNode(node);
		if(node!=null){
			Interval newselection=parent.getChild(0).getSourceInterval();
			editor.getArea().selectRange(tokens.get(newselection.a).getStartIndex(),tokens.get(newselection.b).getStopIndex()+1);
		}
	}
	public void selectLastNode(){
		IndexRange oldselection=editor.getArea().getSelection();
		ParseTree node=getSurroundingNode(oldselection.getStart(),oldselection.getEnd());
		ParseTree parent=getOuterNode(node);
		if(node!=null){
			Interval newselection=parent.getChild(parent.getChildCount()-1).getSourceInterval();
			editor.getArea().selectRange(tokens.get(newselection.a).getStartIndex(),tokens.get(newselection.b).getStopIndex()+1);
		}
	}
	private ParseTree getOuterNode(ParseTree node){
		Interval oldInterval=node.getSourceInterval();
		while(node.getParent()!=null){
			node=node.getParent();
			Interval newInterval=node.getSourceInterval();
			if(newInterval.a!=oldInterval.a||newInterval.b!=oldInterval.b){
				return node;
			}
		}
		return null;
	}
	public ParseTree getSurroundingNode(int start,int end){
		ParseTree tree=(ParseTree)syntaxTree();
		while(true){
			int childCount=tree.getChildCount();
			int index=0;
			while(index<childCount&&tokens.get(tree.getChild(index).getSourceInterval().a).getStartIndex()<=start){
				++index;
			}
			if(index==0||tokens.get(tree.getChild(index-1).getSourceInterval().b).getStopIndex()+1<end){
				return tree;
			}else{
				tree=tree.getChild(index-1);
			}
		}
	}
	public void nextWord(NavigationActions.SelectionPolicy policy){
		int from=editor.getArea().getCaretPosition();
		int to=tokens.stream().mapToInt((t)->t.getStartIndex()).filter((i)->i>from).findFirst().orElse(editor.getArea().getLength());//TODO: bsearch is better
		editor.getArea().moveTo(to,policy);
	}
	public void previousWord(NavigationActions.SelectionPolicy policy){
		int from=editor.getArea().getCaretPosition();
		int to=tokens.stream().mapToInt((t)->t.getStartIndex()).filter((i)->i<from).max().orElse(0);//TODO: bsearch is better
		editor.getArea().moveTo(to,policy);
	}
	public void selectWord(){
		int from=editor.getArea().getCaretPosition();
		Optional<? extends Token> token=tokens.stream().filter((t)->t.getStopIndex()>from).findFirst();
		if(token.isPresent()){
			editor.getArea().selectRange(token.get().getStartIndex(),token.get().getStopIndex()+1);
		}
	}
	public void deleteNextWord(){
		nextWord(NavigationActions.SelectionPolicy.EXTEND);
		editor.getArea().replaceSelection("");
	}
	public void deletePreviousWord(){
		previousWord(NavigationActions.SelectionPolicy.EXTEND);
		editor.getArea().replaceSelection("");
	}
}
