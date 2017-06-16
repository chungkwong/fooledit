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
import com.github.chungkwong.fooledit.example.text.*;
import com.github.chungkwong.fooledit.model.*;
import java.util.*;
import java.util.stream.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class FileTypeChooser extends BorderPane{
	private final TreeView templates;
	public FileTypeChooser(Map<Object,Object> obj){
		templates=new TreeView(buildTree(obj));
		templates.setShowRoot(false);
		templates.setCellFactory((p)->new TemplateCell());
		setCenter(templates);
	}
	private TreeItem buildTree(Map<Object,Object> obj){
		TreeItem item;
		if(obj.containsKey("children")){
			item=new TreeItem(obj.get("name"));
			List<Map<Object,Object>> children=(List<Map<Object,Object>>)obj.get("children");
			item.getChildren().setAll(children.stream().map(this::buildTree).collect(Collectors.toList()));
		}else if(obj.get("type").equals("text")){
			item=new TreeItem(new TextTemplate((String)obj.get("name"),(String)obj.get("description"),(String)obj.get("file")));
		}else{
			item=new TreeItem();
		}
		return item;
	}
	private static class TemplateCell extends TreeTableCell<Object,Object>{
		public TemplateCell(){
		}
		@Override
		protected void updateItem(Object item,boolean empty){
			super.updateItem(item,empty);
			if(empty||item==null){
				setText(null);
				setGraphic(null);
			}else if(item instanceof Template){
				setText(((Template)item).getName());
			}else if(item instanceof String){
				setText((String)item);
			}
		}
	}
}
