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
package com.github.chungkwong.fooledit.example.browser;
import com.github.chungkwong.fooledit.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BrowserViewer extends BorderPane{
	private final Button reload=new Button("↺");
	private final Button back=new Button("<");
	private final Button forward=new Button(">");
	private final WebEngine engine;
	private final TextField loc=new TextField();
	public BrowserViewer(WebView data){
		engine=data.getEngine();
		engine.getLoadWorker().messageProperty().addListener((e,o,n)->{if(n!=null)Main.INSTANCE.getNotifier().notify(n);});
		reload.setOnAction((e)->refresh());
		WebHistory history=engine.getHistory();
		back.setOnAction((e)->backward());
		back.disableProperty().bind(history.currentIndexProperty().lessThanOrEqualTo(0));
		forward.setOnAction((e)->forward());
		updateForward();
		history.currentIndexProperty().addListener((e,o,n)->updateForward());
		history.getEntries().addListener((ListChangeListener.Change<? extends WebHistory.Entry> c)->updateForward());
		loc.setEditable(true);
		loc.setOnAction((e)->engine.load(loc.getText()));
		engine.locationProperty().addListener((e,o,n)->loc.setText(n));
		HBox.setHgrow(loc,Priority.ALWAYS);
		setCenter(data);
		setTop(new HBox(back,forward,loc,reload));
	}
	@Override
	public void requestFocus(){
		super.requestFocus();
		loc.requestFocus();
	}
	private void updateForward(){
		forward.setDisable(engine.getHistory().getCurrentIndex()+1>=engine.getHistory().getEntries().size());
	}
	void forward(){
		engine.getHistory().go(1);
	}
	void backward(){
		engine.getHistory().go(-1);
	}
	void locate(){
		loc.requestFocus();
	}
	void refresh(){
		engine.reload();
	}
	static{
	}
}
