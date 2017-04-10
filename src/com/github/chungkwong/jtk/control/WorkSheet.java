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
import java.util.*;
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
	public WorkSheet(Node node){
		super(node);
	}
	public void splitVertically(Node second){
		split(second,Orientation.VERTICAL);
	}
	public void splitHorizontally(Node second){
		split(second,Orientation.HORIZONTAL);
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
	public void keepOnly(Node node){
		setCenter(node);
		node.requestFocus();
	}
	public JSONStuff toJSON(){
		Node center=getCenter();
		if(center instanceof SplitPane){
			HashMap<JSONStuff,JSONStuff> map=new HashMap<>();
			map.put(new JSONString("direction"),new JSONString(((SplitPane)center).getOrientation().name()));
			map.put(new JSONString("dividers"),JSONConvertor.toJSONStuff(((SplitPane)center).getDividerPositions()));
			map.put(new JSONString("children"),new JSONArray(((SplitPane)center).getItems().stream().map((c)->toJSON(c)).collect(Collectors.toList())));
			return new JSONObject(map);
		}else{
			return toJSON(center);
		}
	}
	private static JSONStuff toJSON(Node node){
		return null;
	}
	public static WorkSheet fromJSON(JSONStuff json){
		return null;
	}

}
