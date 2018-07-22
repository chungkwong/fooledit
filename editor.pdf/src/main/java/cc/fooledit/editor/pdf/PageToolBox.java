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
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.converter.*;
import org.apache.pdfbox.pdmodel.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PageToolBox implements ToolBox{
	public static final PageToolBox INSTANCE=new PageToolBox();
	private PageToolBox(){
	}
	@Override
	public String getName(){
		return "PAGE";
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("PAGE",Activator.NAME);
	}
	@Override
	public Node createInstance(Node viewer,Object remark,RegistryNode<String,Object> meta){
		return new PageToolBar((PdfViewer)viewer,((PdfObject)meta.get(DataObject.DATA)).getDocument());
	}
	@Override
	public Node getGraphic(){
		return null;
	}
	@Override
	public Side[] getPerferedSides(){
		return new Side[]{Side.TOP};
	}
	static class PageToolBar extends HBox{
		private final PDDocument document;
		private final PdfViewer viewer;
		public PageToolBar(PdfViewer viewer,PDDocument document){
			this.document=document;
			this.viewer=viewer;
			TextField  page=new TextField("0");
			page.setEditable(true);
			page.textProperty().bindBidirectional(viewer.pageIndexProperty(),new NumberStringConverter());

			Spinner<Number> zoom=new Spinner<>(0,Double.MAX_VALUE,1.0,0.5);
			zoom.setEditable(true);
			viewer.scaleProperty().bind(zoom.valueProperty());

			Spinner<Number> rotate=new Spinner<>(-180,180,0.0,1.0);
			rotate.setEditable(true);
			viewer.pageRotateProperty().bind(rotate.valueProperty());


			Label total=new Label("/"+document.getNumberOfPages());
			Label percent=new Label("×");
			Label degree=new Label("deg");
			getChildren().addAll(page,total,zoom,percent,rotate,degree);
		}
	}
}
