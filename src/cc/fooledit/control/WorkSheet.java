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
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import java.util.function.*;
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
	private final RegistryNode<String,Object> registry;
	private final Function<String,Object> remarkSupplier=(key)->getDataEditor().getRemark(getCenter());
	public WorkSheet(RegistryNode<String,Object> data,DataEditor editor,Object remark){
		registry=new SimpleRegistryNode<>();
		setData(data,editor,remark);
		restoreRegistry();
	}
	private WorkSheet(Node node){
		registry=new SimpleRegistryNode<>();
		setCenter(node);
		restoreRegistry();
	}
	private WorkSheet(Node node,RegistryNode<String,Object> registry){
		this.registry=registry;
		setCenter(node);
	}
	private WorkSheet(RegistryNode<String,Object> data,DataEditor editor,Object remark,RegistryNode<String,Object> registry){
		this.registry=registry;
		setData(data,editor,remark);
	}
	private void setData(RegistryNode<String,Object> data,DataEditor editor,Object remark){
		Node node=editor.edit((DataObject)data.get(DataObject.DATA),remark,data);
		node.setUserData(new Pair<>(data,editor));
		setCenter(node);
	}
	private void restoreRegistry(){
		if(getCenter()instanceof SplitPane){
			registry.remove(BUFFER);
			registry.remove(EDITOR);
			registry.remove(REMARK);
			registry.remove(CURRENT);
			registry.put(DIRECTION,getOrientation().name());
			registry.put(DIVIDER,getDivider());
			ListRegistryNode<Object> children=new ListRegistryNode<>();
			children.put(getFirst().getRegistry());
			children.put(getLast().getRegistry());
			registry.put(CHILDREN,children);
		}else if(getCenter() instanceof TabPane){
			registry.remove(BUFFER);
			registry.remove(EDITOR);
			registry.remove(REMARK);
			registry.remove(CURRENT);
			registry.remove(DIRECTION);
			registry.remove(DIVIDER);
			ListRegistryNode<Object> children=new ListRegistryNode<>();
			getTabs().forEach((w)->children.put(w.getRegistry()));
			registry.put(CHILDREN,children);
		}else{
			registry.remove(DIRECTION);
			registry.remove(DIVIDER);
			registry.remove(CHILDREN);
			registry.remove(REMARK);
			registry.put(BUFFER,getDataObject());
			registry.put(EDITOR,getDataEditor().getClass().getName());
			registry.put(CURRENT,true);
		}
	}
	@Override
	public void requestFocus(){
		getCenter().requestFocus();
	}
	public void splitVertically(RegistryNode<String,Object> data,DataEditor editor,Object remark){
		split(data,editor,remark,Orientation.VERTICAL);
	}
	public void splitHorizontally(RegistryNode<String,Object> data,DataEditor editor,Object remark){
		split(data,editor,remark,Orientation.HORIZONTAL);
	}
	private void split(RegistryNode<String,Object> data,DataEditor editor,Object remark,Orientation orientation){
		Node first=getCenter();
		SplitPane splitPane=new SplitPane(new WorkSheet(first),new WorkSheet(data,editor,remark));
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
		restoreRegistry();
		splitPane.getDividers().get(0).positionProperty().addListener((e,o,n)->registry.put(DIVIDER,n));
	}
	public void tab(RegistryNode<String,Object> data,DataEditor editor,Object remark){
		Tab newTab=new Tab((String)data.get(DataObject.BUFFER_NAME),new WorkSheet(data,editor,remark));
		if(isTabed()){
			((TabPane)getCenter()).getTabs().add(newTab);
			((TabPane)getCenter()).getSelectionModel().select(newTab);
		}else{
			Node first=getCenter();
			TabPane splitPane=new TabPane(new Tab("",new WorkSheet(first)),newTab);
			setCenter(splitPane);
			restoreRegistry();
		}
	}
	public void keepOnly(RegistryNode<String,Object> data,DataEditor editor,Object remark){
		setData(data,editor,remark);
		restoreRegistry();
		getCenter().requestFocus();
		WorkSheet parent=getParentWorkSheet();
		if(parent!=null&&parent.isTabed()){
			((TabPane)parent.getCenter()).getSelectionModel().getSelectedItem().setText((String)data.get(DataObject.BUFFER_NAME));
		}
	}
	public static final String DIRECTION="direction";
	public static final String DIVIDER="divider";
	public static final String CHILDREN="children";
	public static final String EDITOR="editor";
	public static final String BUFFER="buffer";
	public static final String CURRENT="current";
	public static final String REMARK="remark";
	public static WorkSheet fromJSON(RegistryNode<String,Object> json){
		if(json.containsKey(DIRECTION)){
			SplitPane pane=new SplitPane();
			pane.setOrientation(Orientation.valueOf((String)json.get(DIRECTION)));
			ListRegistryNode<RegistryNode<String,Object>> children=(ListRegistryNode<RegistryNode<String,Object>>)json.get(CHILDREN);
			pane.getItems().setAll(fromJSON(children.get(0)),fromJSON(children.get(1)));
			pane.setDividerPositions(((Number)json.get(DIVIDER)).doubleValue());
			return new WorkSheet(pane,json);
		}else if(json.containsKey(CHILDREN)){
			TabPane pane=new TabPane();
			ListRegistryNode<RegistryNode<String,Object>> children=(ListRegistryNode<RegistryNode<String,Object>>)json.get(CHILDREN);
			pane.getTabs().setAll(children.getChildren().stream().map((child)->new Tab((String)child.get(DataObject.BUFFER_NAME),fromJSON(child))).collect(Collectors.toList()));
			return new WorkSheet(pane,json);
		}else{
			RegistryNode<String,Object> buffer=DataObjectRegistry.get((RegistryNode<String,Object>)json.get(BUFFER));
			String editorName=(String)json.get(EDITOR);
			DataEditor editor=CoreModule.DATA_OBJECT_EDITOR_REGISTRY.get(editorName);
			WorkSheet workSheet=new WorkSheet(buffer,editor,json.get(REMARK),json);
			json.remove(REMARK);
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
	public WorkSheet getFirst(){
		return (WorkSheet)((SplitPane)getCenter()).getItems().get(0);
	}
	public WorkSheet getLast(){
		return (WorkSheet)((SplitPane)getCenter()).getItems().get(1);
	}
	public double getDivider(){
		return ((SplitPane)getCenter()).getDividerPositions()[0];
	}
	public boolean isTabed(){
		return getCenter() instanceof TabPane;
	}
	public boolean isCompound(){
		return isTabed()||isSplit();
	}
	public Stream<WorkSheet> getTabs(){
		return ((TabPane)getCenter()).getTabs().stream().map((tab)->(WorkSheet)tab.getContent());
	}
	public RegistryNode<String,Object> getDataObject(){
		return ((Pair<RegistryNode<String,Object>,DataEditor>)getCenter().getUserData()).getKey();
	}
	public DataEditor getDataEditor(){
		return ((Pair<RegistryNode<String,Object>,DataEditor>)getCenter().getUserData()).getValue();
	}
	public RegistryNode<String,Object> getRegistry(){
		return registry;
	}
	public WorkSheet getParentWorkSheet(){
		Node parent=this;
		parent=parent.getParent();
		while(parent!=null&&!(parent instanceof WorkSheet))
			parent=parent.getParent();
		return (WorkSheet)parent;
	}
}
