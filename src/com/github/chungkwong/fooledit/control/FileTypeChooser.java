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
package com.github.chungkwong.fooledit.control;
import com.github.chungkwong.fooledit.*;
import static com.github.chungkwong.fooledit.Main.loadJSON;
import com.github.chungkwong.fooledit.api.*;
import com.github.chungkwong.fooledit.example.image.*;
import com.github.chungkwong.fooledit.example.text.*;
import com.github.chungkwong.fooledit.model.*;
import com.github.chungkwong.fooledit.setting.*;
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
public class FileTypeChooser extends Prompt{
	public static final FileTypeChooser INSTANCE=new FileTypeChooser();
	private static final Map<String,Function<Map<Object,Object>,Template>> templateTypes=new HashMap<>();
	//private static final List<Map<Object,Object>> recent=(List<Map<Object,Object>>)PersistenceStatusManager.getOrDefault("template",()->Collections.emptyList());
	static{
		registerTemplateType("text",(obj)->new TextTemplate((String)obj.get("name"),(String)obj.get("description"),(String)obj.get("file"),(String)obj.get("mime")));
		registerTemplateType("image",(obj)->new ImageTemplate((String)obj.get("name"),(String)obj.get("description"),(String)obj.get("file"),(String)obj.get("mime")));
		//PersistenceStatusManager.registerConvertor("template",);
	}
	private FileTypeChooser(){

	}
	@Override
	public javafx.scene.Node edit(Prompt data){
		BorderPane pane=new BorderPane();
		pane.setLeft(new FileSystemViewer());
		TreeView templates=new TreeView(buildTree(loadJSON((File)SettingManager.getOrCreate("code-editor").get("template-index",null))));
		templates.setOnMouseClicked((e)->{
			if(e.getClickCount()==2){
				choose(templates);
			}
		});
		templates.setShowRoot(false);
		templates.setCellFactory((p)->new TemplateCell());

		pane.setCenter(templates);
		return pane;
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
		commands.put("create",(area)->choose((TreeView)((BorderPane)Main.INSTANCE.getCurrentNode()).getCenter()));
		return commands;
	}
	private void choose(TreeView templates){
		Object item=((TreeItem)templates.getSelectionModel().getSelectedItem()).getValue();
		if(item instanceof Template){
			Template template=(Template)item;
			DataObject obj=template.apply(null);
			System.out.println(template.getMimeType());
			Main.addAndShow(obj,Helper.hashMap(DataObjectRegistry.TYPE,obj.getDataObjectType(),
					DataObjectRegistry.MIME,template.getMimeType()));
		}
	}
	private TreeItem buildTree(Map<Object,Object> obj){
		TreeItem item;
		if(obj.containsKey("children")){
			item=new TreeItem(MessageRegistry.getString((String)obj.get("name")));
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
	public String getName(){
		return "TEMPLATES";
	}
	public static void registerTemplateType(String key,Function<Map<Object,Object>,Template> type){
		templateTypes.put(key,type);
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
				setText(MessageRegistry.getString(((Template)item).getName()));
			}else if(item instanceof String){
				setText((String)item);
			}
		}
	}
	public static void main(String[] args){
		System.out.println(System.getProperties());
	}
}
