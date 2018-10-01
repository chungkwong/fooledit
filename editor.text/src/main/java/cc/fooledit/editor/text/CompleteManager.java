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
import cc.fooledit.control.*;
import cc.fooledit.core.*;
import cc.fooledit.editor.text.PopupHint;
import cc.fooledit.util.*;
import java.util.logging.*;
import java.util.stream.*;
import static javafx.application.Application.launch;
import javafx.application.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.web.*;
import javafx.stage.*;
import org.fxmisc.richtext.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class CompleteManager{
	private final CodeEditor editor;
	private AutoCompleteProvider hints;
	private static final PopupHint popupHint=new PopupHint();
	private static final RealTimeTask<HintContext> task=new RealTimeTask<>((o)->{
		Stream<AutoCompleteHint> hint=o.provider.checkForHints(o.text,o.position);
		if(!Thread.interrupted()){
			Platform.runLater(()->popupHint.showHints(o.component,o.position,hint));
		}
	});
	public CompleteManager(CodeEditor editor){
		this.editor=editor;
		EventHandler<KeyEvent> keyHandler=(e)->{
			if(popupHint.isShowing()){
				switch(e.getCode()){
					case UP:
						popupHint.selectPrevious();
						editor.requestFocus();
						e.consume();
						break;
					case DOWN:
						popupHint.selectNext();
						editor.requestFocus();
						e.consume();
						break;
					case ENTER:
						if(popupHint.isShowing()){
							popupHint.choose();
							editor.requestFocus();
							e.consume();
						}
						break;
					case ESCAPE:
						popupHint.hideHints();
						editor.requestFocus();
						e.consume();
						break;
				}
			}
		};
		Main.INSTANCE.getScene().addEventFilter(KeyEvent.KEY_PRESSED,keyHandler);
		ChangeListener<Integer> caretListener=(e,o,n)->show(hints,n);
		editor.getArea().caretPositionProperty().addListener(caretListener);
	}
	public void show(AutoCompleteProvider hints){
		show(hints,editor.getArea().getCaretPosition());
	}
	public void show(AutoCompleteProvider hints,int position){
		if(hints!=null){
			task.summit(new HintContext(hints,editor.getArea().getText(),position,editor.getArea()));
		}
	}
	public void setCompleteProvider(AutoCompleteProvider hints){
		this.hints=hints;
	}
	public AutoCompleteProvider getCompleteProvider(){
		return hints;
	}
	public static void main(String[] args){
		launch(args);
	}
	static class HintContext{
		final AutoCompleteProvider provider;
		final String text;
		final int position;
		final CodeArea component;
		public HintContext(AutoCompleteProvider provider,String text,int position,CodeArea component){
			this.provider=provider;
			this.text=text;
			this.position=position;
			this.component=component;
		}
	}
}
class PopupHint{
	private final WebView note=new WebView();
	private final ListView<AutoCompleteHint> loc=new ListView<>();
	private final MultipleSelectionModel<AutoCompleteHint> model=loc.getSelectionModel();
	private CodeArea doc;
	private int pos;
	private Popup popup=new Popup();
	public PopupHint(){
		model.selectedItemProperty().addListener((e,o,n)->{
			if(n!=null){
				note.getEngine().loadContent(Helper.readText(n.getDocument()));
			}
		});
		loc.setOnMouseClicked((e)->{
			if(e.getClickCount()==2){
				choose();
			}
		});
		BorderPane pane=new BorderPane();
		loc.setOpacity(0.8);
		loc.setFocusTraversable(false);
		loc.setCellFactory((p)->new HintCell());
		pane.setLeft(loc);
		note.setPrefSize(400,300);
		note.setOpacity(0.8);
		pane.setRight(note);
		popup.setAutoHide(true);
		popup.getContent().add(pane);
	}
	public void showHints(CodeArea comp,int pos,Stream<AutoCompleteHint> choices){
		hideHints();
		loc.getItems().setAll(choices.collect(Collectors.toList()));
		if(loc.getItems().isEmpty()){
			return;
		}
		this.pos=pos;
		this.doc=comp;
		model.selectFirst();
		Bounds b=doc.caretBoundsProperty().getValue().get();
		popup.show(comp.getScene().getWindow(),b.getMaxX(),b.getMaxY());
		comp.requestFocus();
	}
	public void hideHints(){
		loc.getItems().clear();
		popup.hide();
		doc=null;
	}
	public boolean isShowing(){
		return popup.isShowing();
	}
	void selectPrevious(){
		model.selectPrevious();
	}
	void selectNext(){
		model.selectNext();
	}
	void choose(){
		choose(model.getSelectedItem().getInputText());
	}
	private void choose(String inputText){
		try{
			doc.insertText(pos,inputText);
		}catch(Exception ex){
			Logger.getGlobal().log(Level.FINER,inputText,ex);
		}
		hideHints();
	}
	static class HintCell extends ListCell<AutoCompleteHint>{
		@Override
		protected void updateItem(AutoCompleteHint item,boolean empty){
			super.updateItem(item,empty);
			if(empty||item==null){
				setText(null);
				setGraphic(null);
			}else{
				setText(item.getDisplayText());
			}
		}
	}
}
