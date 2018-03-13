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
import java.io.*;
import java.util.logging.*;
import javafx.embed.swing.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.rendering.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PdfViewer extends BorderPane{
	private final PDDocument document;
	private final PDFRenderer renderer;
	private final Pagination pagination;
	private final ImageView view=new ImageView();
	private final Spinner<Number> zoom;
	public PdfViewer(PDDocument document){
		this.document=document;
		this.renderer=new PDFRenderer(document);
		this.pagination=new Pagination(document.getNumberOfPages(),0);
		zoom=new Spinner<>(0,Double.MAX_VALUE,1.0,0.5);
		pagination.setPageFactory((index)->new ImageView(getPage(index,zoom.getValue().floatValue())));
		setCenter(new ScrollPane(pagination));
		setRight(getInfoPane());
		setTop(getPagePane());
		setLeft(getOutlinePane());
	}
	private Node getPagePane(){
		zoom.setEditable(true);
		zoom.valueProperty().addListener((e,o,n)->{
			pagination.setCurrentPageIndex(pagination.getCurrentPageIndex());
		});
		Label total=new Label("/"+document.getNumberOfPages());
		Label percent=new Label("×");
		return new HBox(zoom,percent);
	}
	public Image getPage(int index,float dpi){
		try{
			return SwingFXUtils.toFXImage(renderer.renderImage(index,dpi),null);
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return new WritableImage(0,0);
		}
	}
	private Node getOutlinePane(){
		return null;
	}
	private Node getInfoPane(){
		return new BeanViewer(document);
	}
	public PDDocument getDocument(){
		return document;
	}
	public void moveToPage(int page){
		pagination.setCurrentPageIndex(page);
	}
	public void setScale(float scale){
		zoom.getEditor().setText(Float.toString(scale));
	}
	public int getPageIndex(){
		return pagination.getCurrentPageIndex();
	}
	public float getScale(){
		return zoom.getValue().floatValue();
	}
}
