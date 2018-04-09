/*
 * Copyright (C) 2018 Chan Chung Kwong <1m02math@126.com>
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
package cc.fooledit.control;
import java.util.function.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ListViewWrapper<E> extends BorderPane{
	private final ListView<E> listView;
	private final Supplier<E> factory;
	public ListViewWrapper(Supplier<E> factory){
		this(new ListView<>(),factory);
	}
	public ListViewWrapper(ObservableList<E> items,Supplier<E> factory){
		this(new ListView<>(items),factory);
	}
	public ListViewWrapper(ListView<E> listView,Supplier<E> factory){
		this.listView=listView;
		this.factory=factory;
		setCenter(listView);
		setBottom(getToolBar());
	}
	private Node getToolBar(){
		Button add=new Button("+");
		add.setOnAction((e)->{
			int index=listView.getSelectionModel().getSelectedIndex();
			listView.getItems().add(index+1,factory.get());
		});
		Button delete=new Button("-");
		delete.setOnAction((e)->{
			int index=listView.getSelectionModel().getSelectedIndex();
			listView.getItems().remove(index);
		});
		Button up=new Button("/\\");
		up.setOnAction((e)->{
			int index=listView.getSelectionModel().getSelectedIndex();
			if(index>0)
				listView.getItems().add(index-1,listView.getItems().remove(index));
		});
		Button down=new Button("\\/");
		down.setOnAction((e)->{
			int index=listView.getSelectionModel().getSelectedIndex();
			if(index+1<listView.getItems().size())
				listView.getItems().add(index+1,listView.getItems().remove(index));
		});
		return new FlowPane(add,delete,up,down);
	}
	public ListView<E> getListView(){
		return listView;
	}
}
