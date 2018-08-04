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
package cc.fooledit.editor.epub;
import cc.fooledit.control.*;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.util.stream.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import nl.siegmann.epublib.domain.*;
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
		return MessageRegistry.getString("CONTENTS",Activator.class);
	}
	@Override
	public Node createInstance(Node viewer,Object remark,RegistryNode<String,Object> meta){
		return new ContentsViewer((EpubViewer)viewer,((EpubObject)meta.get(DataObject.DATA)).getDocument());
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public Side[] getPerferedSides(){
		return new Side[]{Side.LEFT};
	}
	static class ContentsViewer extends TreeViewWrapper<TOCReference>{
		private final Book document;
		private final EpubViewer viewer;
		public ContentsViewer(EpubViewer viewer,Book document){
			this.document=document;
			this.viewer=viewer;
			setCellFactory((param)->new OutlineCell());
			setShowRoot(false);
			getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
				if(n!=null)
					viewer.show(n.getValue().getResource());
			});
			TreeItem<TOCReference> root=new TreeItem<>(null);
			root.getChildren().setAll(document.getTableOfContents().getTocReferences().stream().map((ref)->createTreeItem(ref)).collect(Collectors.toList()));
			setRoot(root);
		}
		private static TreeItem<TOCReference> createTreeItem(TOCReference node){

			if(!node.getChildren().isEmpty()){
				return new LazyTreeItem<>(node,()->node.getChildren().stream().map((child)->createTreeItem(child)).collect(Collectors.toList()));
			}else{
				return new TreeItem<>(node);
			}
		}
		private static class OutlineCell extends TreeCell<TOCReference>{
			public OutlineCell(){
			}
			@Override
			protected void updateItem(TOCReference item,boolean empty){
				super.updateItem(item,empty);
				if(empty||item==null){
					setText(null);
					setGraphic(null);
				}else{
					setText(item.getTitle());
				}
			}
		}
	}
}
