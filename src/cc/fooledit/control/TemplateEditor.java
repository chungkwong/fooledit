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
import java.util.stream.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TemplateEditor extends Prompt{
	//private static final List<Map<Object,Object>> recent=(List<Map<Object,Object>>)PersistenceStatusManager.getOrDefault("template",()->Collections.emptyList());
	public static final TemplateEditor INSTANCE=new TemplateEditor();
	public TemplateEditor(){

	}
	@Override
	public javafx.scene.Node edit(Prompt data,Object remark,RegistryNode<String,Object,String> meta){
		return new TemplateChooser();
	}
	@Override
	public NavigableRegistryNode<String,String,String> getKeymapRegistry(){
		NavigableRegistryNode<String,String,String> keymap=new NavigableRegistryNode<>();
		keymap.addChild("Enter","create");
		return keymap;
	}
	@Override
	public RegistryNode<String,Command,String> getCommandRegistry(){
		RegistryNode<String,Command,String> commands=new SimpleRegistryNode<>();
		commands.addChild("create",new Command("create",()->((TemplateChooser)Main.INSTANCE.getCurrentNode()).choose(),CoreModule.NAME));//FIXME
		return commands;
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("TEMPLATES",CoreModule.NAME);
	}
	@Override
	public String getName(){
		return "template";
	}
	private static class TemplateChooser extends BorderPane{
		private final TreeView templates;
		private final TextField filename=new TextField();
		public TemplateChooser(){
			templates=new TreeView(buildTree(CoreModule.TEMPLATE_REGISTRY));
			templates.setOnMouseClicked((e)->{
				if(e.getClickCount()==2){
					choose();
				}
			});
			templates.setShowRoot(false);
			templates.setCellFactory((p)->new TemplateCell());
			setCenter(templates);
		}
		private TreeItem buildTree(RegistryNode obj){
			TreeItem item;
			if(obj.hasChild("children")){
				item=new TreeItem(MessageRegistry.getString((String)obj.getChild("name"),(String)obj.getChild("module")));
				ListRegistryNode<RegistryNode,Object> children=(ListRegistryNode<RegistryNode,Object>)obj.getChild("children");
				item.getChildren().setAll(children.toMap().values().stream().map(this::buildTree).collect(Collectors.toList()));
			}else if(CoreModule.TEMPLATE_TYPE_REGISTRY.hasChild((String)obj.getChild("type"))){
				item=new TreeItem(CoreModule.TEMPLATE_TYPE_REGISTRY.getChild((String)obj.getChild("type")).apply(obj.toMap()));
			}else{
				item=new TreeItem("");
			}
			return item;
		}
		@Override
		public void requestFocus(){
			super.requestFocus();
			templates.requestFocus();
		}
		private void choose(){
			Object item=((TreeItem)templates.getSelectionModel().getSelectedItem()).getValue();
			if(item instanceof Template){
				Template template=(Template)item;
				System.out.println(template.getParameters());
				Properties props=new Properties();
				props.put("package","xyz.beold");
				props.put("name","Name");
				props.put("project",Helper.hashMap("licensePath","../headers/GPL-3"));
				props.put("date","2017-6-29");
				props.put("user","kwong");
				DataObject obj=template.apply(props);
				SimpleRegistryNode<String,Object,String> registry=new SimpleRegistryNode<>();
				registry.addChild(DataObject.TYPE,obj.getDataObjectType().getClass().getName());
				registry.addChild(DataObject.MIME,template.getMimeType());
				registry.addChild(DataObject.DEFAULT_NAME,((Template)item).getName());
				registry.addChild(DataObject.DATA,obj);
				Main.INSTANCE.showOnCurrentTab(registry);
				DataObjectRegistry.removeDataObject(Main.INSTANCE.getCurrentDataObject());
			}
		}
	}
	private static class TemplateCell extends TreeCell<Object>{
		public TemplateCell(){
		}
		@Override
		protected void updateItem(Object item,boolean empty){
			super.updateItem(item,empty);
			if(empty||item==null){
				setText(null);
				setGraphic(null);
			}else if(item instanceof Template){
				setText(MessageRegistry.getString(((Template)item).getName(),((Template)item).getModule()));
			}else if(item instanceof String){
				setText((String)item);
			}
		}
	}
}
