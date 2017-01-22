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
package com.github.chungkwong.jtk.example.tool;
import com.github.chungkwong.jtk.model.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Browser implements DataEditor<BrowserData>{
	@Override
	public Node edit(BrowserData data){
		WebView browser=new WebView();
		Button reload=new Button("↺");
		reload.setOnAction((e)->browser.getEngine().reload());
		Button back=new Button("<");
		Button forward=new Button(">");
		WebHistory history=browser.getEngine().getHistory();
		back.setOnAction((e)->{
			history.go(-1);
			forward.setDisable(false);
		});
		back.disableProperty().bind(history.currentIndexProperty().lessThanOrEqualTo(0));
		forward.setOnAction((e)->{
			history.go(1);
			forward.setDisable(history.getCurrentIndex()+1>=history.getEntries().size());
		});
		forward.setDisable(true);
		MenuItem noback=new MenuItem("Nowhere to back");
		ContextMenu backMenu=new ContextMenu(noback);
		backMenu.setOnShowing((e)->{
			System.out.println("hello");
			ObservableList<MenuItem> items=backMenu.getItems();
			ObservableList<WebHistory.Entry> entries=history.getEntries();
			items.clear();
			int curr=history.getCurrentIndex();
			for(int i=curr-1;i>=0;i--){
				MenuItem item=new MenuItem(entries.get(i).getTitle());
				item.setOnAction((event)->{
					history.go(-items.indexOf(item)-1);
					forward.setDisable(false);
				});
				items.add(item);
			}
			if(items.isEmpty()){
				items.add(noback);
			}
		});
		back.setContextMenu(backMenu);
		MenuItem noforward=new MenuItem("Nowhere to forward");
		ContextMenu forwardMenu=new ContextMenu(noforward);
		forwardMenu.setOnShowing((e)->{
			ObservableList<MenuItem> items=forwardMenu.getItems();
			ObservableList<WebHistory.Entry> entries=history.getEntries();
			items.clear();
			int curr=history.getCurrentIndex();
			for(int i=curr+1;i<entries.size();i++){
				MenuItem item=new MenuItem(entries.get(i).getTitle());
				item.setOnAction((event)->{
					history.go(items.indexOf(item)+1);
					forward.setDisable(history.getCurrentIndex()+1>=history.getEntries().size());
				});
				items.add(item);
			}
			if(items.isEmpty()){
				items.add(noforward);
			}
		});
		forward.setContextMenu(forwardMenu);

		TextField loc=new TextField();
		loc.setEditable(true);
		loc.setOnAction((e)->browser.getEngine().load(loc.getText()));
		browser.getEngine().locationProperty().addListener((e,o,n)->loc.setText(n));
		HBox.setHgrow(loc,Priority.ALWAYS);
		HBox bar=new HBox(back,forward,loc,reload);
		return new BorderPane(browser,bar,null,null,null);
	}

}
