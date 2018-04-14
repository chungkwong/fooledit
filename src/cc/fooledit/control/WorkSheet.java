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
import java.util.*;
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
	private final LazyRegistryNode<String,Object,String> registry;
	private final Function<String,Object> remarkSupplier=(key)->getDataEditor().getRemark(getCenter());
	public WorkSheet(RegistryNode<String,Object,String> data,DataEditor editor,Object remark){
		registry=new LazyRegistryNode<>(remarkSupplier,Collections.emptySet(),false);
		setData(data,editor,remark);
		restoreRegistry();
	}
	private WorkSheet(Node node){
		registry=new LazyRegistryNode<>(remarkSupplier,Collections.emptySet(),false);
		setCenter(node);
		restoreRegistry();
	}
	private WorkSheet(Node node,LazyRegistryNode<String,Object,String> registry){
		this.registry=registry;
		registry.setSupplier(remarkSupplier);
		setCenter(node);
	}
	private WorkSheet(RegistryNode<String,Object,String> data,DataEditor editor,Object remark,LazyRegistryNode<String,Object,String> registry){
		this.registry=registry;
		registry.setSupplier(remarkSupplier);
		setData(data,editor,remark);
	}
	private void setData(RegistryNode<String,Object,String> data,DataEditor editor,Object remark){
		Node node=editor.edit((DataObject)data.getChild(DataObject.DATA),remark,data);
		node.setUserData(new Pair<>(data,editor));
		registry.setKeys(Collections.singleton(REMARK));
		setCenter(node);
	}
	private void restoreRegistry(){
		if(getCenter()instanceof SplitPane){
			registry.removeChild(BUFFER);
			registry.removeChild(EDITOR);
			registry.removeChild(REMARK);
			registry.removeChild(CURRENT);
			registry.addChild(DIRECTION,getOrientation().name());
			registry.addChild(DIVIDER,getDivider());
			ListRegistryNode<Object,String> children=new ListRegistryNode<>();
			children.addChild(getFirst().getRegistry());
			children.addChild(getLast().getRegistry());
			registry.addChild(CHILDREN,children);
		}else if(getCenter() instanceof TabPane){
			registry.removeChild(BUFFER);
			registry.removeChild(EDITOR);
			registry.removeChild(REMARK);
			registry.removeChild(CURRENT);
			registry.removeChild(DIRECTION);
			registry.removeChild(DIVIDER);
			ListRegistryNode<Object,String> children=new ListRegistryNode<>();
			getTabs().forEach((w)->children.addChild(w.getRegistry()));
			registry.addChild(CHILDREN,children);
		}else{
			registry.removeChild(DIRECTION);
			registry.removeChild(DIVIDER);
			registry.removeChild(CHILDREN);
			registry.removeChild(REMARK);
			registry.addChild(BUFFER,new AliasRegistryNode<>(getDataObject()));
			registry.addChild(EDITOR,getDataEditor().getClass().getName());
			registry.addChild(CURRENT,true);
		}
	}
	@Override
	public void requestFocus(){
		getCenter().requestFocus();
	}
	public void splitVertically(RegistryNode<String,Object,String> data,DataEditor editor,Object remark){
		split(data,editor,remark,Orientation.VERTICAL);
	}
	public void splitHorizontally(RegistryNode<String,Object,String> data,DataEditor editor,Object remark){
		split(data,editor,remark,Orientation.HORIZONTAL);
	}
	private void split(RegistryNode<String,Object,String> data,DataEditor editor,Object remark,Orientation orientation){
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
		registry.setKeys(Collections.emptySet());
		splitPane.getDividers().get(0).positionProperty().addListener((e,o,n)->registry.addChild(DIVIDER,n));
	}
	private void tab(RegistryNode<String,Object,String> data,DataEditor editor,Object remark){
		Tab newTab=new Tab((String)data.getChild(DataObject.BUFFER_NAME),new WorkSheet(data,editor,remark));
		if(isTabed()){
			((TabPane)getCenter()).getTabs().add(newTab);
		}else{
			Node first=getCenter();
			TabPane splitPane=new TabPane(new Tab("",new WorkSheet(first)),newTab);
			setCenter(splitPane);
			restoreRegistry();
			registry.setKeys(Collections.emptySet());
		}
	}
	public void keepOnly(RegistryNode<String,Object,String> data,DataEditor editor,Object remark){
		setData(data,editor,remark);
		restoreRegistry();
		getCenter().requestFocus();
	}
	public static final String DIRECTION="direction";
	public static final String DIVIDER="divider";
	public static final String CHILDREN="children";
	public static final String EDITOR="editor";
	public static final String BUFFER="buffer";
	public static final String CURRENT="current";
	public static final String REMARK="remark";
	public static WorkSheet fromJSON(SimpleRegistryNode<String,Object,String> json){
		return fromJSON(toLazy(json));
	}
	private static LazyRegistryNode<String,Object,String> toLazy(SimpleRegistryNode<String,Object,String> json){
		LazyRegistryNode<String,Object,String> reg=new LazyRegistryNode<>(null,Collections.emptySet(),false);
		json.getChildNames().forEach((key)->{
			Object child=json.getChild(key);
			if(child instanceof SimpleRegistryNode)
				reg.addChild(key,toLazy((SimpleRegistryNode<String,Object,String>)child));
			else
				reg.addChild(key,child);
		});
		return reg;
	}
	private static WorkSheet fromJSON(LazyRegistryNode<String,Object,String> json){
		if(json.hasChild(DIRECTION)){
			SplitPane pane=new SplitPane();
			pane.setOrientation(Orientation.valueOf((String)json.getChild(DIRECTION)));
			ListRegistryNode<LazyRegistryNode<String,Object,String>,String> children=(ListRegistryNode<LazyRegistryNode<String,Object,String>,String>)json.getChild(CHILDREN);
			pane.getItems().setAll(fromJSON(children.getChild(0)),fromJSON(children.getChild(1)));
			pane.setDividerPositions(((Number)json.getChild(DIVIDER)).doubleValue());
			return new WorkSheet(pane,json);
		}else if(json.hasChild(CHILDREN)){
			TabPane pane=new TabPane();
			ListRegistryNode<LazyRegistryNode<String,Object,String>,String> children=(ListRegistryNode<LazyRegistryNode<String,Object,String>,String>)json.getChild(CHILDREN);
			pane.getTabs().setAll(children.getChildren().stream().map((child)->new Tab((String)child.getChild(DataObject.BUFFER_NAME),fromJSON(child))).collect(Collectors.toList()));
			return new WorkSheet(pane,json);
		}else{
			RegistryNode<String,Object,String> buffer=DataObjectRegistry.get((RegistryNode<String,Object,String>)json.getChild(BUFFER));
			String editorName=(String)json.getChild(EDITOR);
			DataEditor editor=CoreModule.DATA_OBJECT_EDITOR_REGISTRY.getChild(editorName);
			WorkSheet workSheet=new WorkSheet(buffer,editor,json.getChild(REMARK),json);
			json.removeChild(REMARK);
			if((Boolean)json.getChild(CURRENT)){
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
	public Stream<WorkSheet> getTabs(){
		return ((TabPane)getCenter()).getTabs().stream().map((tab)->(WorkSheet)tab.getContent());
	}
	public RegistryNode<String,Object,String> getDataObject(){
		return ((Pair<RegistryNode<String,Object,String>,DataEditor>)getCenter().getUserData()).getKey();
	}
	public DataEditor getDataEditor(){
		return ((Pair<RegistryNode<String,Object,String>,DataEditor>)getCenter().getUserData()).getValue();
	}
	public SimpleRegistryNode<String,Object,String> getRegistry(){
		return registry;
	}
}
