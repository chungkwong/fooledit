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
	private static final JSONString DIRECTION=new JSONString("direction");
	private static final JSONString DIVIDERS=new JSONString("dividers");
	private static final JSONString CHILDREN=new JSONString("children");
	private static final JSONString EDITOR=new JSONString("editor");
	private static final JSONString BUFFER=new JSONString("buffer");
	public JSONStuff toJSON(){
		Node center=getCenter();
		HashMap<JSONStuff,JSONStuff> map=new HashMap<>();
		if(isSplit()){
			map.put(DIRECTION,new JSONString(((SplitPane)center).getOrientation().name()));
			map.put(DIVIDERS,JSONConvertor.toJSONStuff(Arrays.stream(((SplitPane)center).getDividerPositions()).boxed().collect(Collectors.toList())));
			map.put(CHILDREN,new JSONArray(((SplitPane)center).getItems().stream().map((c)->((WorkSheet)c).toJSON()).collect(Collectors.toList())));
		}else{
			map.put(EDITOR,new JSONString(getDataEditor().getClass().getName()));
			map.put(BUFFER,JSONConvertor.toJSONStuff(DataObjectRegistry.getProperties(getDataObject())));
		}
		return new JSONObject(map);
	}
	public static WorkSheet fromJSON(JSONStuff json){
		return null;
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
			return t.toJSON().toString();
		}
		@Override
		public WorkSheet fromString(String string){
			try{
				return fromJSON(JSONParser.parse(string));
			}catch(IOException|SyntaxException ex){
				Logger.getGlobal().log(Level.SEVERE,null,ex);
				return new WorkSheet(null);
			}
		}
	};
}
