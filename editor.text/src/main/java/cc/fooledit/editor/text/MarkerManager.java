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
import java.util.*;
import javafx.collections.*;
import org.fxmisc.richtext.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class MarkerManager{
	private final TreeSet<CaretNode> markers=new TreeSet<>(Comparator.comparing((n)->n.getPosition()));
	private final ObservableList<SelectionGroup> selections=FXCollections.observableArrayList();
	private int selectionId=0;
	private int markerId=0;
	private int currentSelectionGroup=0;
	private final CodeEditor editor;
	public MarkerManager(CodeEditor editor){
		this.editor=editor;
		selections.add(createSelectionGroup("selection"));
		Selection<Collection<String>,String,Collection<String>> currentBrace=createSelection(0,0,"brace");
		Selection<Collection<String>,String,Collection<String>> matchingBrace=createSelection(0,0,"brace");
		editor.getArea().caretPositionProperty().addListener((e,o,n)->{
			if(n>0){
				String ch=editor.getArea().getText(n-1,n);
				int match=-1;
				switch(ch.codePointAt(0)){
					case '(':
						match=findMatchingBraceForward(editor.getArea().getText(),n,'(',')');
						break;
					case '[':
						match=findMatchingBraceForward(editor.getArea().getText(),n,'[',']');
						break;
					case '{':
						match=findMatchingBraceForward(editor.getArea().getText(),n,'[','}');
						break;
					case ')':
						match=findMatchingBraceBackward(editor.getArea().getText(),n-2,'(',')');
						break;
					case ']':
						match=findMatchingBraceBackward(editor.getArea().getText(),n-2,'[',']');
						break;
					case '}':
						match=findMatchingBraceBackward(editor.getArea().getText(),n-2,'{','}');
						break;
				}
				if(match!=-1){
					currentBrace.selectRange(n-1,n);
					matchingBrace.selectRange(match,match+1);
				}else{
					currentBrace.deselect();
					matchingBrace.deselect();
				}
			}
		});
	}
	private static int findMatchingBraceForward(String text,int start,char left,char right){
		int level=1;
		while(start<text.length()){
			char c=text.charAt(start);
			if(c==left){
				++level;
			}
			if(c==right){
				--level;
				if(level==0){
					return start;
				}
			}
			++start;
		}
		return -1;
	}
	private static int findMatchingBraceBackward(String text,int start,char left,char right){
		int level=1;
		while(start>=0){
			char c=text.charAt(start);
			if(c==right){
				++level;
			}
			if(c==left){
				--level;
				if(level==0){
					return start;
				}
			}
			--start;
		}
		return -1;
	}
	Selection<Collection<String>,String,Collection<String>> createSelection(int start,int end,String cls){
		Selection<Collection<String>,String,Collection<String>> selection=new SelectionImpl<>(Integer.toString(++selectionId),editor.getArea(),(path)->path.getStyleClass().add(cls));
		editor.getArea().addSelection(selection);
		selection.selectRange(start,end);
		return selection;
	}
	public SelectionGroup createSelectionGroup(String cls){
		SelectionGroup selectionGroup=new SelectionGroup(cls,editor);
		selections.add(selectionGroup);
		return selectionGroup;
	}
	public void reverseSelection(){
		getCurrentSelectionGroup().reverse();
	}
	public ObservableList<SelectionGroup> selections(){
		return selections;
	}
	public SelectionGroup getCurrentSelectionGroup(){
		return selections.get(currentSelectionGroup);
	}
	public Selection getSelectionIndex(int position){
		return getCurrentSelectionGroup().getSelections().stream().filter((s)->s.getStartPosition()>=position&&s.getEndPosition()<position).findAny().orElse(null);
	}
	public void unhighlight(){
		getCurrentSelectionGroup().getSelections().clear();
	}
	public void mark(int offset){
		CaretNode caret=new CaretNode("caret"+(++markerId),editor.getArea(),offset);
		caret.getStyleClass().add("marker");
		editor.getArea().addCaret(caret);
		markers.add(caret);
	}
	public void unmark(int start,int end){
		markers.removeIf((marker)->{
			if(marker.getPosition()>=start&&marker.getPosition()<end){
				editor.getArea().removeCaret(marker);
				return true;
			}else{
				return false;
			}
		});
	}
	public TreeSet<CaretNode> getMarkers(){
		return markers;
	}
}
