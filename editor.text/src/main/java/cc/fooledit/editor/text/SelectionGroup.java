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
public class SelectionGroup{
	private final ObservableList<Selection<Collection<String>,String,Collection<String>>> selections;
	private final CodeEditor area;
	private final String cls;
	private int count=0;
	public SelectionGroup(String cls,CodeEditor area){
		this.selections=FXCollections.observableArrayList();
		this.area=area;
		this.cls=cls;
		selections.addListener((ListChangeListener.Change<? extends Selection<Collection<String>,String,Collection<String>>> c)->{
			while(c.next()){
				if(c.wasRemoved()){
					c.getRemoved().forEach((s)->area.getArea().removeSelection(s));
				}
			}
		});
	}
	public void select(int start,int end){
		selections.add(createSelection(start,end));
	}
	private Selection<Collection<String>,String,Collection<String>> createSelection(int start,int end){
		return area.createSelection(start,end,cls);
	}
	public ObservableList<Selection<Collection<String>,String,Collection<String>>> getSelections(){
		return selections;
	}
	public void reverse(){
		sortSelection();
		ListIterator<Selection<Collection<String>,String,Collection<String>>> iter=selections.listIterator();
		if(iter.hasNext()){
			Selection<Collection<String>,String,Collection<String>> prev=iter.next();
			if(prev.getStartPosition()!=0){
				iter.remove();
				iter.add(createSelection(0,prev.getStartPosition()));
			}
			while(iter.hasNext()){
				Selection<Collection<String>,String,Collection<String>> range=iter.next();
				iter.remove();
				selections.add(createSelection(prev.getEndPosition(),range.getStartPosition()));
				prev=range;
			}
			if(prev.getEndPosition()!=area.getArea().getLength()){
				selections.add(createSelection(prev.getEndPosition(),area.getArea().getLength()));
			}
		}else{
			selections.add(createSelection(0,area.getArea().getLength()));
		}
	}
	private void sortSelection(){
		FXCollections.sort(selections,(x,y)->Integer.compare(x.getStartPosition(),y.getStartPosition()));
	}
	public String getStyleClass(){
		return cls;
	}
}
