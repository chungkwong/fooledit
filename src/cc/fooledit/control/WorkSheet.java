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
package cc.fooledit.control;
import cc.fooledit.*;
import cc.fooledit.api.*;
import cc.fooledit.model.*;
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import com.github.chungkwong.json.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.application.*;
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
	public WorkSheet(RegistryNode<String,Object,String> data,DataEditor editor,Object remark){
		super(pack(data,editor,remark));
	}
	@Override
	public void requestFocus(){
		super.requestFocus();
		getCenter().requestFocus();
	}
	public void splitVertically(RegistryNode<String,Object,String> data,DataEditor editor,Object remark){
		split(pack(data,editor,remark),Orientation.VERTICAL);
	}
	public void splitHorizontally(RegistryNode<String,Object,String> data,DataEditor editor,Object remark){
		split(pack(data,editor,remark),Orientation.HORIZONTAL);
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
	private static Node pack(RegistryNode<String,Object,String> data,DataEditor editor,Object remark){
		Node node=editor.edit((DataObject)data.getChild(DataObject.DATA),remark,data);
		node.setUserData(new Pair<>(data,editor));
		return node;
	}
	public void keepOnly(RegistryNode<String,Object,String> data,DataEditor editor,Object remark){
		Node node=pack(data,editor,remark);
		setCenter(node);
		node.requestFocus();
	}
	private static final String DIRECTION="direction";
	private static final String DIVIDERS="dividers";
	private static final String CHILDREN="children";
	private static final String EDITOR="editor";
	private static final String BUFFER="buffer";
	private static final String CURRENT="current";
	private static final String REMARK="remark";
	public Map<Object,Object> toJSON(){
		Node center=getCenter();
		HashMap<Object,Object> map=new HashMap<>();
		if(isSplit()){
			map.put(DIRECTION,((SplitPane)center).getOrientation().name());
			map.put(DIVIDERS,Arrays.stream(((SplitPane)center).getDividerPositions()).boxed().collect(Collectors.toList()));
			map.put(CHILDREN,((SplitPane)center).getItems().stream().map((c)->((WorkSheet)c).toJSON()).collect(Collectors.toList()));
		}else{
			map.put(CURRENT,Main.INSTANCE.getCurrentWorkSheet()==this);
			map.put(EDITOR,getDataEditor().getClass().getName());
			map.put(REMARK,getDataEditor().getRemark(center));
			map.put(BUFFER,getDataObject().toMap());
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
			RegistryNode<String,Object,String> buffer=DataObjectRegistry.get(json.get(BUFFER));
			String editorName=(String)json.get(EDITOR);
			DataEditor editor=DataObjectTypeRegistry.getDataEditors((Class<? extends DataObject>)buffer.getChild(DataObject.DATA).getClass()).stream().
					filter((e)->e.getClass().getName().equals(editorName)).findFirst().get();
			WorkSheet workSheet=new WorkSheet(buffer,editor,json.get(REMARK));
			if((Boolean)json.get(CURRENT)){
				Main.INSTANCE.setCurrentWorkSheet(workSheet);
				Platform.runLater(()->workSheet.requestFocus());
			}
			return workSheet;
		}
	}
	public boolean isSplit(){
		return getCenter() instanceof SplitPane;
	}
	public Orientation getOrientation(){
		return ((SplitPane)getCenter()).getOrientation();
	}
	public RegistryNode<String,Object,String> getDataObject(){
		return ((Pair<RegistryNode<String,Object,String>,DataEditor>)getCenter().getUserData()).getKey();
	}
	public DataEditor getDataEditor(){
		return ((Pair<RegistryNode<String,Object,String>,DataEditor>)getCenter().getUserData()).getValue();
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
