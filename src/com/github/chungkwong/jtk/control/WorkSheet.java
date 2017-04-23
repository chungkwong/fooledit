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
import com.github.chungkwong.json.*;
import com.github.chungkwong.jtk.api.*;
import com.github.chungkwong.jtk.model.*;
import com.github.chungkwong.jtk.util.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.beans.value.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class WorkSheet extends BorderPane{
	private WorkSheet(Node node){
		super(node);
	}
	public WorkSheet(DataObject data,DataEditor editor){
		super(pack(data,editor));
	}
	public void splitVertically(DataObject data,DataEditor editor){
		split(pack(data,editor),Orientation.VERTICAL);
	}
	public void splitHorizontally(DataObject data,DataEditor editor){
		split(pack(data,editor),Orientation.HORIZONTAL);
	}
	private void split(Node second,Orientation orientation){
		Node first=getCenter();
		SplitPane splitPane=new SplitPane(new WorkSheet(first),new WorkSheet(second));
		splitPane.setOrientation(orientation);
		splitPane.setDividerPositions(0.5,0.5);
		setCenter(splitPane);
		ChangeListener<Scene> listener=new ChangeListener<Scene>(){
			@Override
			public void changed(ObservableValue ov,Scene t,Scene t1){
				if(t1!=null){
					first.requestFocus();
					first.sceneProperty().removeListener(this);
				}
			}
		};
		first.sceneProperty().addListener(listener);
		first.requestFocus();
	}
	private static Node pack(DataObject data,DataEditor editor){
		Node node=editor.edit(data);
		node.setUserData(new Pair<>(data,editor));
		return node;
	}
	public void keepOnly(DataObject data,DataEditor editor){
		Node node=pack(data,editor);
		setCenter(node);
		node.requestFocus();
	}
	private static final String DIRECTION="direction";
	private static final String DIVIDERS="dividers";
	private static final String CHILDREN="children";
	private static final String EDITOR="editor";
	private static final String BUFFER="buffer";
	public Map<Object,Object> toJSON(){
		Node center=getCenter();
		HashMap<Object,Object> map=new HashMap<>();
		if(isSplit()){
			map.put(DIRECTION,((SplitPane)center).getOrientation().name());
			map.put(DIVIDERS,Arrays.stream(((SplitPane)center).getDividerPositions()).boxed().collect(Collectors.toList()));
			map.put(CHILDREN,((SplitPane)center).getItems().stream().map((c)->((WorkSheet)c).toJSON()).collect(Collectors.toList()));
		}else{
			map.put(EDITOR,getDataEditor().getClass().getName());
			map.put(BUFFER,DataObjectRegistry.getProperties(getDataObject()));
		}
		return map;
	}
	public static WorkSheet fromJSON(Map<Object,Object> json){
		if(json.containsKey(DIRECTION)){
			SplitPane pane=new SplitPane();
			pane.setOrientation(Orientation.valueOf((String)json.get(DIRECTION)));
			pane.getItems().setAll(((List<Map<Object,Object>>)json.get(CHILDREN)).stream().map((o)->fromJSON(o)).toArray(Node[]::new));
			pane.setDividerPositions(((List<Number>)json.get(DIVIDERS)).stream().mapToDouble((o)->o.doubleValue()).toArray());
			return new WorkSheet(pane);
		}else{
			DataObject buffer=DataObjectRegistry.get(json.get(BUFFER));
			String editorName=(String)json.get(EDITOR);
			DataEditor editor=DataObjectTypeRegistry.getDataEditors(buffer.getClass()).stream().
					filter((e)->e.getClass().getName().equals(editorName)).findFirst().get();
			return new WorkSheet(buffer,editor);
		}
	}
	public boolean isSplit(){
		return getCenter() instanceof SplitPane;
	}
	public Orientation getOrientation(){
		return ((SplitPane)getCenter()).getOrientation();
	}
	public DataObject getDataObject(){
		return ((Pair<DataObject,DataEditor>)getCenter().getUserData()).getKey();
	}
	public DataEditor getDataEditor(){
		return ((Pair<DataObject,DataEditor>)getCenter().getUserData()).getValue();
	}
	public static final javafx.util.StringConverter<WorkSheet> CONVERTOR=new javafx.util.StringConverter<WorkSheet>(){
		@Override
		public String toString(WorkSheet t){
			return JSONEncoder.encode(t.toJSON());
		}
		@Override
		public WorkSheet fromString(String string){
			try{
				return fromJSON((Map<Object,Object>)JSONDecoder.decode(string));
			}catch(IOException|SyntaxException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				return new WorkSheet(null);
			}
		}
	};
}
