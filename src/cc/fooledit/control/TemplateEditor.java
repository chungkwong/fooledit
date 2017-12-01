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
import static cc.fooledit.Main.loadJSON;
import cc.fooledit.api.*;
import cc.fooledit.example.text.*;
import cc.fooledit.model.*;
import cc.fooledit.setting.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TemplateEditor extends Prompt{
	private static final Map<String,Function<Map<Object,Object>,Template>> templateTypes=new HashMap<>();
	//private static final List<Map<Object,Object>> recent=(List<Map<Object,Object>>)PersistenceStatusManager.getOrDefault("template",()->Collections.emptyList());
	private final TemplateChooser chooser=new TemplateChooser();
	public static final TemplateEditor INSTANCE=new TemplateEditor();
	public TemplateEditor(){

	}
	@Override
	public javafx.scene.Node edit(Prompt data){
		return chooser;
	}
	@Override
	public KeymapRegistry getKeymapRegistry(){
		KeymapRegistry keymap=new KeymapRegistry();
		keymap.registerKey("Enter","create");
		return keymap;
	}
	@Override
	public CommandRegistry getCommandRegistry(){
		CommandRegistry commands=new CommandRegistry();
		commands.put("create",()->((TemplateChooser)Main.INSTANCE.getCurrentNode()).choose(),CoreModule.NAME);//FIXME
		return commands;
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("TEMPLATES",CoreModule.NAME);
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("TEMPLATES",CoreModule.NAME);
	}
	public static void registerTemplateType(String key,Function<Map<Object,Object>,Template> type){
		templateTypes.put(key,type);
	}
	private class TemplateChooser extends BorderPane{
		private final TreeView templates;
		private final TextField filename=new TextField();
		public TemplateChooser(){
			templates=new TreeView(buildTree(loadJSON((File)SettingManager.getOrCreate(TextEditorModule.NAME).get("template-index",null))));
			templates.setOnMouseClicked((e)->{
				if(e.getClickCount()==2){
					choose();
				}
			});
			templates.setShowRoot(false);
			templates.setCellFactory((p)->new TemplateCell());
			setCenter(templates);
		}
		private TreeItem buildTree(Map<Object,Object> obj){
			TreeItem item;
			if(obj.containsKey("children")){
				item=new TreeItem(MessageRegistry.getString((String)obj.get("name"),CoreModule.NAME));//FIXME
				List<Map<Object,Object>> children=(List<Map<Object,Object>>)obj.get("children");
				item.getChildren().setAll(children.stream().map(this::buildTree).collect(Collectors.toList()));
			}else if(templateTypes.containsKey((String)obj.get("type"))){
				item=new TreeItem(templateTypes.get((String)obj.get("type")).apply(obj));
			}else{
				item=new TreeItem();
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
				obj.getProperties().put(DataObject.TYPE,obj.getDataObjectType().getClass().getName());
				obj.getProperties().put(DataObject.MIME,template.getMimeType());
				obj.getProperties().put(DataObject.DEFAULT_NAME,((Template)item).getName());
				Main.show(obj);
				DataObjectRegistry.removeDataObject(TemplateEditor.this);
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
				setText(MessageRegistry.getString(((Template)item).getName(),CoreModule.NAME));//FIXME
			}else if(item instanceof String){
				setText((String)item);
			}
		}
	}
}
