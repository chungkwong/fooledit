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
package cc.fooledit.editor.pdf;
import cc.fooledit.control.*;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.*;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.*;
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
		return MessageRegistry.getString("CONTENTS",PdfModule.NAME);
	}
	@Override
	public Node createInstance(Node viewer,Object remark,RegistryNode<String,Object> meta){
		return new ContentsViewer((PdfViewer)viewer,((PdfObject)meta.get(DataObject.DATA)).getDocument());
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public Side[] getPerferedSides(){
		return new Side[]{Side.LEFT};
	}
	static class ContentsViewer extends TreeViewWrapper<PDOutlineNode>{
		private final PDDocument document;
		private final PdfViewer viewer;
		public ContentsViewer(PdfViewer viewer,PDDocument document){
			this.document=document;
			this.viewer=viewer;
			setCellFactory((param)->new OutlineCell());
			setShowRoot(false);
			getSelectionModel().selectedItemProperty().addListener((e,o,n)->{
				if(n.getValue() instanceof PDOutlineItem){
					PDDestination destination;
					try{
						destination=((PDOutlineItem)n.getValue()).getDestination();
						if(destination instanceof PDNamedDestination){
							String label=((PDNamedDestination)destination).getNamedDestination();
							destination=document.getDocumentCatalog().getDests().getDestination(label);
						}
						if(destination instanceof PDPageDestination){
							System.err.println(destination);
							System.err.println(((PDPageXYZDestination)destination).retrievePageNumber());
							viewer.setPageIndex(((PDPageDestination)destination).retrievePageNumber());
						}
					}catch(IOException ex){
						Logger.getLogger(ContentsToolBox.class.getName()).log(Level.INFO,null,ex);
					}
				}
			});

			setRoot(createTreeItem(document.getDocumentCatalog().getDocumentOutline()));
		}
		private static TreeItem<PDOutlineNode> createTreeItem(PDOutlineNode node){
			if(node.hasChildren()){
				return new LazyTreeItem<>(node,()->StreamSupport.stream(Spliterators.spliteratorUnknownSize(node.children().iterator(),0),false).map((child)->createTreeItem(child)).collect(Collectors.toList()));
			}else{
				return new TreeItem<>(node);
			}
		}
		private static class OutlineCell extends TreeCell<PDOutlineNode>{
			public OutlineCell(){
			}
			@Override
			protected void updateItem(PDOutlineNode item,boolean empty){
				super.updateItem(item,empty);
				if(empty||item==null){
					setText(null);
					setGraphic(null);
				}else if(item instanceof PDOutlineItem){
					setText(((PDOutlineItem)item).getTitle());
				}
			}
		}
	}
}
