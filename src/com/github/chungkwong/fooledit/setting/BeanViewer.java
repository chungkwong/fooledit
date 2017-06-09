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
package com.github.chungkwong.fooledit.setting;
import com.github.chungkwong.fooledit.control.LazyTreeItem;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.application.*;
import static javafx.application.Application.launch;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class BeanViewer extends Application{
	private Object obj;
	@Override
	public void start(Stage stage) throws Exception{
		TreeTableView<FieldBean> tree=new TreeTableView<>(createTreeNode(new FieldBean(stage,null)));
		tree.setEditable(true);
		TreeTableColumn<FieldBean,String> key=new TreeTableColumn<>("KEY");
		key.prefWidthProperty().bind(tree.widthProperty().multiply(0.5));
		key.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FieldBean,String>,ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FieldBean,String> p){
				return new ReadOnlyStringWrapper(p.getValue().getValue().getKey());
			}
		});
		TreeTableColumn<FieldBean,String> value=new TreeTableColumn<>("VALUE");
		value.setEditable(true);
		value.prefWidthProperty().bind(tree.widthProperty().multiply(0.5));
		value.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FieldBean, String>,ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FieldBean,String> p){
				return new ReadOnlyStringWrapper(p.getValue().getValue().toString());
			}
		});
		tree.getColumns().addAll(key,value);
		stage.setScene(new Scene(new BorderPane(tree)));
		stage.setMaximized(true);
		stage.show();
	}
	private static TreeItem<FieldBean> createTreeNode(FieldBean bean){
		Object obj=bean.getValue();
		return new LazyTreeItem<>(()->findChild(obj),bean);
	}
	private static List<TreeItem<FieldBean>> findChild(Object obj){
		if(obj==null){
			return Collections.emptyList();
		}
		return getGetter(obj.getClass()).map((get)->createTreeNode(new FieldBean(obj,get))).collect(Collectors.toList());
	}
	private static Stream<Method> getGetter(Class cls){
		return Arrays.stream(cls.getMethods()).filter((m)->m.getName().startsWith("get")&&m.getParameterCount()==0);
	}
	public static void main(String[] args){
		launch(args);
	}
	private static class FieldBean{
		private final Object obj;
		private final Method m;
		public FieldBean(Object obj,Method m){
			this.obj=obj;
			this.m=m;
		}
		public String getKey(){
			return m==null?"":m.getName().substring(3);
		}
		public Object getValue(){
			if(m==null)
				return obj;
			try{
				return m.invoke(obj);
			}catch(ReflectiveOperationException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				return null;
			}
		}
		@Override
		public String toString(){
			return Objects.toString(getValue());
		}

	}
}
