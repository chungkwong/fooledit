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
import cc.fooledit.core.*;
import cc.fooledit.editor.text.Activator;
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import java.util.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.fxmisc.richtext.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class FindToolBox implements ToolBox{
	public static final FindToolBox INSTANCE=new FindToolBox();
	private FindToolBox(){
	}
	@Override
	public String getName(){
		return "FIND";
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("FIND",Activator.class);
	}
	@Override
	public Node createInstance(Node viewer,Object remark,RegistryNode<String,Object> meta){
		return new FindPanel((CodeEditor)viewer);
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public Side[] getPerferedSides(){
		return new Side[]{Side.BOTTOM,Side.TOP};
	}
	private static class FindPanel extends VBox{
		private final CodeEditor area;
		private final ToggleButton regex=new ToggleButton(MessageRegistry.getString("REGEX",Activator.class));
		private final ToggleButton word=new ToggleButton(MessageRegistry.getString("WORD",Activator.class));
		private final ToggleButton ignoreCase=new ToggleButton(MessageRegistry.getString("IGNORE_CASE",Activator.class));
		private final TextArea findString=new TextArea();
		private final TextArea replaceString=new TextArea();
		private final SelectionGroup found;
		private final RealTimeTask<String> task;
		private FindPanel(CodeEditor area){
			this.area=area;
			this.found=area.createSelectionGroup("found");
			HBox searchBar=new HBox();
			searchBar.getChildren().add(new Label(MessageRegistry.getString("FIND",Activator.class)));
			findString.textProperty().addListener((e,o,n)->find(n));
			searchBar.getChildren().add(findString);
			HBox.setHgrow(findString,Priority.ALWAYS);
			Button previous=new Button(MessageRegistry.getString("PREVIOUS",Activator.class));
			previous.setOnAction((e)->findPrevious());
			Button next=new Button(MessageRegistry.getString("NEXT",Activator.class));
			next.setOnAction((e)->findNext());
			searchBar.getChildren().addAll(next,previous,ignoreCase,word,regex);
			HBox replaceBar=new HBox();
			replaceBar.getChildren().add(new Label(MessageRegistry.getString("REPLACE_WITH",Activator.class)));
			replaceBar.getChildren().add(replaceString);
			HBox.setHgrow(replaceBar,Priority.ALWAYS);
			Button current=new Button(MessageRegistry.getString("CURRENT",Activator.class));
			current.setOnAction((e)->replaceCurrent());
			Button all=new Button(MessageRegistry.getString("ALL",Activator.class));
			all.setOnAction((e)->replaceAll());
			replaceBar.getChildren().addAll(current,all);
			getChildren().setAll(searchBar,replaceBar);
			task=new RealTimeTask<>((key)->{
				String text=area.getArea().getText();
			});
		}
		private void find(String str){
			task.summit(str);
		}
		private void findNext(){
			int caret=area.getArea().getCaretPosition();
			found.getSelections().stream().filter((s)->s.getStartPosition()>=caret).findFirst().ifPresent((s)->area.getArea().selectRange(s.getStartPosition(),s.getEndPosition()));
		}
		private void findPrevious(){
			int caret=area.getArea().getCaretPosition();
			ListIterator<Selection<Collection<String>,String,Collection<String>>> iterator=found.getSelections().listIterator(found.getSelections().size());
			while(iterator.hasPrevious()){
				Selection<Collection<String>,String,Collection<String>> prev=iterator.previous();
				if(prev.getEndPosition()<caret){
					area.getArea().selectRange(prev.getStartPosition(),prev.getEndPosition());
					return;
				}
			}
		}
		private void replaceCurrent(){
			area.getArea().replaceSelection(replaceString.getText());
		}
		private void replaceAll(){
			ListIterator<Selection<Collection<String>,String,Collection<String>>> iterator=found.getSelections().listIterator(found.getSelections().size());
			while(iterator.hasPrevious()){
				Selection<Collection<String>,String,Collection<String>> prev=iterator.previous();
				area.getArea().replaceText(prev.getStartPosition(),prev.getEndPosition(),getReplaceString(area.getArea().getText(prev.getStartPosition(),prev.getEndPosition())));
			}
		}
		private String getReplaceString(String input){
			return replaceString.getText();
		}
	}
}
