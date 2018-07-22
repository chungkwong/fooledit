/*
 * Copyright (C) 2018 Chan Chung Kwong <1m02math@126.com>
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
package cc.fooledit.editor.chm;
import cc.fooledit.control.*;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import cc.fooledit.util.*;
import java.util.stream.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.jchmlib.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ContentsToolBox implements ToolBox{
	public static final ContentsToolBox INSTANCE=new ContentsToolBox();
	private ContentsToolBox(){
	}
	@Override
	public String getName(){
		return "CONTENTS";
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("CONTENTS",Activator.NAME);
	}
	@Override
	public Node createInstance(Node viewer,Object remark,RegistryNode<String,Object> meta){
		return new ContentsViewer((ChmViewer)viewer,((ChmObject)meta.get(DataObject.DATA)).getDocument());
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public Side[] getPerferedSides(){
		return new Side[]{Side.LEFT};
	}
	static class ContentsViewer extends TreeViewWrapper<Pair<String,Object>>{
		private final ChmFile document;
		private final ChmViewer viewer;
		public ContentsViewer(ChmViewer viewer,ChmFile document){
			super(new TreeItem<>());
			this.document=document;
			this.viewer=viewer;
			getRoot().getChildren().add(new TreeItem<>(new Pair<>("HOME",document.getHomeFile())));
			getRoot().getChildren().add(new TreeItem<>(new Pair<>("CONTENT",document.getTopicsFile())));
			getRoot().getChildren().add(createTreeItem(document.getTopicsTree()));
			getRoot().getChildren().add(new TreeItem<>(new Pair<>("INDEX",document.getIndexFile())));
			setCellFactory((param)->new OutlineCell());
			setShowRoot(false);
			getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
				if(n!=null){
					if(n.getValue().getValue() instanceof String)
						viewer.setPath((String)n.getValue().getValue());
					else
						viewer.setPath(((ChmTopicsTree)n.getValue().getValue()).path);
				}
			});
		}
		private static TreeItem<Pair<String,Object>> createTreeItem(ChmTopicsTree node){
			if(node.children.isEmpty()){
				return new TreeItem<>(new Pair<>(node.title,node.path));
			}else{
				return new LazyTreeItem<>(new Pair<>(node.title,node),()->node.children.stream().map((child)->createTreeItem(child)).collect(Collectors.toList()));
			}
		}
		private static class OutlineCell extends TreeCell<Pair<String,Object>>{
			public OutlineCell(){
			}
			@Override
			protected void updateItem(Pair<String,Object> item,boolean empty){
				super.updateItem(item,empty);
				if(empty||item==null){
					setText(null);
					setGraphic(null);
				}else{
					setText(item.getKey());
				}
			}
		}
	}
}