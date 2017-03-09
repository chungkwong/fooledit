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
package com.github.chungkwong.jtk.control;
import com.github.chungkwong.jtk.api.*;
import com.github.chungkwong.jtk.util.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.web.*;
import javafx.stage.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class AutoCompleteService extends Application{
	private final TextInputControl comp;
	private final AutoCompleteProvider hints;
	private static final PopupHint popupHint=new PopupHint();
	private static final RealTimeTask<HintContext> task=new RealTimeTask<>((o)->{
		Stream<AutoCompleteHint> hint=o.provider.checkForHints(o.component.getText(),o.position);
		Platform.runLater(()->popupHint.showHints(o.component,o.position,hint));
	});
	public AutoCompleteService(TextInputControl comp,AutoCompleteProvider hints){
		this.hints=hints;
		this.comp=comp;
		comp.addEventFilter(KeyEvent.KEY_PRESSED,(e)->{
			if(popupHint.isShowing())
				switch(e.getCode()){
					case UP:popupHint.selectPrevious();comp.requestFocus();e.consume();break;
					case DOWN:popupHint.selectNext();comp.requestFocus();e.consume();break;
					case ENTER:if(popupHint.isShowing()){popupHint.choose();comp.requestFocus();e.consume();}break;
					case ESCAPE:popupHint.hideHints();comp.requestFocus();e.consume();break;
				}
		});
		comp.focusedProperty().addListener((e,o,n)->{
			if(n)
				updateHint(comp.getCaretPosition());
			else
				popupHint.hideHints();
		});
		comp.caretPositionProperty().addListener((e,o,n)->updateHint(n.intValue()));
	}
	public void updateHint(int pos){
		task.summit(new HintContext(hints,comp,pos));
	}
	@Override
	public void start(Stage stage) throws Exception{
		TextField field=new TextField();
		new AutoCompleteService(field,AutoCompleteProvider.createSimple(Arrays.asList(
				AutoCompleteHint.create("c","c","doc: c"),
				AutoCompleteHint.create("cd","cd","doc: cd")
		)));
		Scene scene=new Scene(field);
		stage.setScene(scene);
		stage.show();
	}
	public static void main(String[] args){
		launch(args);
	}
	static class HintContext{
		final AutoCompleteProvider provider;
		final TextInputControl component;
		final int position;
		public HintContext(AutoCompleteProvider provider,TextInputControl component,int position){
			this.provider=provider;
			this.component=component;
			this.position=position;
		}
	}
}
class PopupHint extends BorderPane{
	private final WebView note=new WebView();
	private final ListView<AutoCompleteHint> loc=new ListView<>();
	private final MultipleSelectionModel<AutoCompleteHint> model=loc.getSelectionModel();
	private TextInputControl doc;
	private int pos;
	private Popup popup;
	public PopupHint(){
		model.selectedItemProperty().addListener((e,o,n)->{
			if(n!=null)
				note.getEngine().loadContent(Helper.readText(n.getDocument()));
		});
		/*loc.setOnMouseClicked((e)->{
			if(e.getClickCount()==2)
				choose(mod.get(loc.locationToIndex(e.getPoint())).getInputText());
		});
		*/
		loc.setOpacity(0.8);
		loc.setFocusTraversable(false);
		loc.setCellFactory((p)->new HintCell());
		setLeft(loc);
		note.setPrefSize(400,300);
		note.setOpacity(0.8);
		setRight(note);
	}
	public void showHints(TextInputControl comp,int pos,Stream<AutoCompleteHint> choices){
		hideHints();
		loc.getItems().setAll(choices.collect(Collectors.toList()));
		if(loc.getItems().isEmpty())
			return;
		this.pos=pos;
		this.doc=comp;
		model.selectFirst();
		popup=new Popup();
		//popup.setOpacity(0.8);
		popup.getContent().add(this);
		Point2D location=comp.localToScreen(0,comp.getHeight());
		popup.show(comp,location.getX(),location.getY());
		comp.requestFocus();
	}
	public void hideHints(){
		loc.getItems().clear();
		if(popup!=null)
			popup.hide();
		popup=null;
		doc=null;
	}
	public boolean isShowing(){
		return popup!=null;
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