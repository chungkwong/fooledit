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
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.beans.value.*;
import javafx.collections.*;
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
	private static final String DATA_EDITOR_NAME="editor";
	private static final String DATA_OBJECT_NAME="object";
	public static final String COMMANDS_NAME="commands";
	public static final String KEYMAP_NAME="keymap";
	private final RegistryNode<String,Object> registry;
	private final Supplier<Object> remarkSupplier=()->getDataEditor().getRemark(getNode());
	private final ListChangeListener<Tab> tabChanged=(e)->restoreRegistry();
	public WorkSheet(){
		WorkSheet child=getDefaultWorkSheet();
		registry=new SimpleRegistryNode<>();
		setCenter(new DraggableTabPane(new Tab(child.getName(),child)));
		restoreRegistry();
	}
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
		restoreRegistry();
	}
	private WorkSheet(RegistryNode<String,Object> data,DataEditor editor,Object remark,RegistryNode<String,Object> registry){
		this.registry=registry;
		setData(data,editor,remark);
		showToolBox((ListRegistryNode<RegistryNode<String,Object>>)registry.get(LEFT),Side.LEFT);
		showToolBox((ListRegistryNode<RegistryNode<String,Object>>)registry.get(RIGHT),Side.RIGHT);
		showToolBox((ListRegistryNode<RegistryNode<String,Object>>)registry.get(TOP),Side.TOP);
		showToolBox((ListRegistryNode<RegistryNode<String,Object>>)registry.get(BOTTOM),Side.BOTTOM);
		((SideBarPane)getCenter()).setRatios((ListRegistryNode<Number>)registry.get(DIVIDER));
		if((Boolean)registry.get(CURRENT)){
			Main.INSTANCE.setCurrentWorkSheet(this);
			EventManager.addEventListener(EventManager.SHOWN,(obj)->{
				WorkSheet node=this;
				WorkSheet parent=getParentWorkSheet();
				while(parent!=null){
					if(parent.isTabed()){
						TabPane tabPane=((TabPane)parent.getCenter());
						WorkSheet curr=node;
						tabPane.getSelectionModel().select(tabPane.getTabs().stream().filter((tab)->tab.getContent()==curr).findFirst().get());
					}
					node=parent;
					parent=parent.getParentWorkSheet();
				}
				requestFocus();
			});
		}
		restoreRegistry();
	}
	private void setData(RegistryNode<String,Object> data,DataEditor editor,Object remark){
		Node node=editor.edit((DataObject)data.get(DataObject.DATA),remark,data);
		SideBarPane nodeWithSideBar=new SideBarPane(node);
		nodeWithSideBar.getProperties().put(DATA_OBJECT_NAME,data);
		nodeWithSideBar.getProperties().put(DATA_EDITOR_NAME,editor);
		nodeWithSideBar.getProperties().put(COMMANDS_NAME,editor.getCommandRegistry());
		nodeWithSideBar.getProperties().put(KEYMAP_NAME,editor.getKeymapRegistry());
		setCenter(nodeWithSideBar);
	}
	private void restoreRegistry(){
		registry.clear();
		if(getCenter()instanceof SideBarPane){
			registry.put(REMARK,new LazyValue<>(remarkSupplier));
			registry.put(BUFFER,getDataObject());
			registry.put(EDITOR,getDataEditor().getClass().getName());
			registry.put(CURRENT,new LazyValue<>(()->Main.INSTANCE.getCurrentWorkSheet()==WorkSheet.this));
			registry.put(LEFT,new LazyValue<>(()->getSideBarRegistry(Side.LEFT)));
			registry.put(RIGHT,new LazyValue<>(()->getSideBarRegistry(Side.RIGHT)));
			registry.put(TOP,new LazyValue<>(()->getSideBarRegistry(Side.TOP)));
			registry.put(BOTTOM,new LazyValue<>(()->getSideBarRegistry(Side.BOTTOM)));
			registry.put(DIVIDER,new LazyValue<>(()->((SideBarPane)getCenter()).getRatios()));
		}else if(getCenter() instanceof TabPane){
			((TabPane)getCenter()).getTabs().removeListener(tabChanged);
			((TabPane)getCenter()).getTabs().addListener(tabChanged);
			ListRegistryNode<Object> children=new ListRegistryNode<>();
			if(!getTabs().findAny().isPresent()){
				WorkSheet parentWorkSheet=getParentWorkSheet();
				if(parentWorkSheet==null){
					addTab(getDefaultWorkSheet());
				}else if(parentWorkSheet.isTabed()){
					parentWorkSheet.removeTab(this);
					return;
				}else if(parentWorkSheet.isSplit()){
					parentWorkSheet.keepOnly(parentWorkSheet.getAnother(this));
					return;
				}
			}
			getTabs().forEach((w)->children.put(w.getRegistry()));
			registry.put(CHILDREN,children);
		}else{
			registry.put(DIRECTION,getOrientation().name());
			registry.put(DIVIDER,getDivider());
			ListRegistryNode<Object> children=new ListRegistryNode<>();
			children.put(getFirst().getRegistry());
			children.put(getLast().getRegistry());
			registry.put(CHILDREN,children);
		}
	}
	private ListRegistryNode<RegistryNode<String,Object>> getSideBarRegistry(Side side){
		return new ListRegistryNode<>(((SideBarPane)getCenter()).getSideBar(side).getTabs().stream().
				map((tab)->(RegistryNode<String,Object>)tab.getProperties().get(TOOLBOX)).collect(Collectors.toList()));
	}
	private static WorkSheet getDefaultWorkSheet(){
		RegistryNode<String,Object> object=DataObjectRegistry.create(CoreModule.DATA_OBJECT_TYPE_REGISTRY.get("cc.fooledit.editor.text.TextObjectType"));
		DataEditor editor=CoreModule.DATA_OBJECT_EDITOR_REGISTRY.get("cc.fooledit.editor.text.StructuredTextEditor");
		return new WorkSheet(object,editor,null);
	}
	@Override
	public void requestFocus(){
		getCenter().requestFocus();
	}
	public void split(WorkSheet subWorkSheet,Orientation orientation){
		Node first=getCenter();
		SplitPane splitPane=new SplitPane(wrap(new WorkSheet(first)),wrap(subWorkSheet));
		splitPane.setOrientation(orientation);
		splitPane.setDividerPositions(0.5);
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
	private WorkSheet wrap(WorkSheet sheet){
		if(sheet.isCompound())
			return sheet;
		else
			return new WorkSheet(new DraggableTabPane(new Tab(sheet.getName(),sheet)));
	}
	public void addTab(WorkSheet worksheet){
		Tab newTab=new Tab(worksheet.getName(),worksheet);
		if(isTabed()){
			((TabPane)getCenter()).getTabs().add(newTab);
			((TabPane)getCenter()).getSelectionModel().select(newTab);
		}else{
			Node first=getCenter();
			TabPane tabPane=new DraggableTabPane(new Tab(getName(),new WorkSheet(first)),newTab);
			setCenter(tabPane);
			restoreRegistry();
		}
	}
	public void removeTab(WorkSheet worksheet){
		if(isTabed()){
			List<Tab> toRemove=((TabPane)getCenter()).getTabs().stream().filter((tab)->tab.getContent()==worksheet).collect(Collectors.toList());
			toRemove.forEach((w)->((WorkSheet)w.getContent()).dispose(null));
			((TabPane)getCenter()).getTabs().removeAll(toRemove);
		}
	}
	public void keepOnly(WorkSheet sheet){
		dispose(sheet);
		if((!sheet.isCompound())&&(getParentWorkSheet()==null||!getParentWorkSheet().isTabed())){
			setCenter(new DraggableTabPane(new Tab(sheet.getName(),sheet)));
		}else{
			setCenter(sheet.getCenter());
		}
		restoreRegistry();
		getCenter().requestFocus();
		WorkSheet parent=getParentWorkSheet();
		if(parent!=null&&parent.isTabed()){
			((TabPane)parent.getCenter()).getSelectionModel().getSelectedItem().setText(getName());
		}
	}
	private void showToolBox(ListRegistryNode<RegistryNode<String,Object>> boxs,Side side){
		if(boxs!=null)
			boxs.values().forEach((box)->{
				try{
					ToolBox toolbox=CoreModule.TOOLBOX_REGISTRY.get(box.get(TOOLBOX));
					showToolBox(toolbox,box.get(REMARK),side);
				}catch(Exception ex){
					Logger.getGlobal().log(Level.INFO,null,ex);
				}
			});
	}
	public void showToolBox(ToolBox toolBox,Object remark){
		((SideBarPane)getCenter()).showToolBox(getToolTab(toolBox,remark),toolBox.getPerferedSides());
	}
	public void showToolBox(ToolBox toolBox,Object remark,Side side){
		((SideBarPane)getCenter()).showToolBox(getToolTab(toolBox,remark),side);
	}
	private Tab getToolTab(ToolBox toolBox,Object remark){
		Node box=toolBox.createInstance(getNode(),remark,getDataObject());
		Tab tab=new Tab(toolBox.getDisplayName(),box);
		tab.setGraphic(toolBox.getGraphic());
		SimpleRegistryNode<String,Object> toolboxRegistry=new SimpleRegistryNode<>();
		toolboxRegistry.put(TOOLBOX,toolBox.getClass().getName());
		toolboxRegistry.put(REMARK,new LazyValue<>(()->toolBox.getRemark(box)));
		tab.getProperties().put(TOOLBOX,toolboxRegistry);
		return tab;
	}
	public static final String DIRECTION="direction";
	public static final String DIVIDER="divider";
	public static final String CHILDREN="children";
	public static final String EDITOR="editor";
	public static final String BUFFER="buffer";
	public static final String CURRENT="current";
	public static final String REMARK="remark";
	public static final String TOOLBOX="toolbox";
	public static final String LEFT="left";
	public static final String RIGHT="right";
	public static final String TOP="top";
	public static final String BOTTOM="bottom";
	public static WorkSheet fromJSON(RegistryNode<String,Object> json){
		if(json.containsKey(DIRECTION)){
			SplitPane pane=new SplitPane();
			pane.setOrientation(Orientation.valueOf((String)json.get(DIRECTION)));
			ListRegistryNode<RegistryNode<String,Object>> children=(ListRegistryNode<RegistryNode<String,Object>>)json.get(CHILDREN);
			pane.getItems().setAll(fromJSON(children.get(0)),fromJSON(children.get(1)));
			pane.setDividerPositions(((Number)json.get(DIVIDER)).doubleValue());
			return new WorkSheet(pane,json);
		}else if(json.containsKey(CHILDREN)){
			TabPane pane=new DraggableTabPane();
			ListRegistryNode<RegistryNode<String,Object>> children=(ListRegistryNode<RegistryNode<String,Object>>)json.get(CHILDREN);
			pane.getTabs().setAll(children.getChildren().stream().map((child)->fromJSON(child)).map((child)->new Tab(child.getName(),child)).collect(Collectors.toList()));
			return new WorkSheet(pane,json);
		}else{
			RegistryNode<String,Object> buffer=DataObjectRegistry.get((RegistryNode<String,Object>)json.get(BUFFER));
			String editorName=(String)json.get(EDITOR);
			DataEditor editor=CoreModule.DATA_OBJECT_EDITOR_REGISTRY.get(editorName);
			json.put(BUFFER,buffer);
			return new WorkSheet(buffer,editor,json.get(REMARK),json);
		}
	}
	public boolean isSplit(){
		return getCenter() instanceof SplitPane&&!(getCenter() instanceof SideBarPane);
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
	public Node getNode(){
		return ((SideBarPane)getCenter()).centerProperty().getValue();
	}
	public WorkSheet getAnother(WorkSheet workSheet){
		WorkSheet first=getFirst();
		return first==workSheet?getLast():first;
	}
	public double getDivider(){
		return ((SplitPane)getCenter()).getDividerPositions()[0];
	}
	public boolean isTabed(){
		return getCenter() instanceof TabPane;
	}
	public boolean isCompound(){
		return !(getCenter()instanceof SideBarPane);
	}
	public Stream<WorkSheet> getTabs(){
		return ((TabPane)getCenter()).getTabs().stream().map((tab)->(WorkSheet)tab.getContent());
	}
	public RegistryNode<String,Object> getDataObject(){
		return (RegistryNode<String,Object>)getCenter().getProperties().get(DATA_OBJECT_NAME);
	}
	public DataEditor getDataEditor(){
		return (DataEditor)getCenter().getProperties().get(DATA_EDITOR_NAME);
	}
	public RegistryNode<String,Command> getCommandRegistry(){
		return (RegistryNode<String,Command>)getCenter().getProperties().get(COMMANDS_NAME);
	}
	public NavigableRegistryNode<String,String> getKeymapRegistry(){
		return (NavigableRegistryNode<String,String>)getCenter().getProperties().get(KEYMAP_NAME);
	}
	public String getName(){
		if(isTabed()){
			return getTabs().findFirst().map((s)->s.getName()).orElse("...");
		}else if(isSplit()){
			return getFirst().getName()+"...";
		}else{
			return Objects.toString(getDataObject().get(DataObject.BUFFER_NAME));
		}
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
	public void dispose(WorkSheet except){
		if(this==except)
			return;
		if(isTabed()){
			getTabs().forEach((tab)->tab.dispose(except));
		}else if(isSplit()){
			getFirst().dispose(except);
			getLast().dispose(except);
		}else{
			getDataEditor().dispose(getNode());
		}
	}
}
