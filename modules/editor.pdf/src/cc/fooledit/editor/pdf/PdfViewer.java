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
import java.io.*;
import java.util.logging.*;
import javafx.beans.property.*;
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
	private final FloatProperty zoom=new SimpleFloatProperty(1.0f);
	public PdfViewer(PDDocument document){
		this.document=document;
		this.renderer=new PDFRenderer(document);
		this.pagination=new Pagination(document.getNumberOfPages(),0);
		pagination.setPageFactory((index)->new ImageView(getPage(index,zoom.getValue().floatValue())));
		zoom.addListener((e,o,n)->pagination.setCurrentPageIndex(pagination.getCurrentPageIndex()));
		setCenter(new ScrollPane(pagination));
		setRight(getInfoPane());
		setTop(getPagePane());
		setLeft(getOutlinePane());
	}
	private Node getPagePane(){
		return new PageToolBox.PageToolBar(this,document);
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
		return new ContentsToolBox.ContentsViewer(this,document);
	}
	public PDDocument getDocument(){
		return document;
	}
	public int getPageIndex(){
		return pagination.getCurrentPageIndex();
	}
	public void setPageIndex(int page){
		pagination.setCurrentPageIndex(page);
	}
	public IntegerProperty pageIndexProperty(){
		return pagination.currentPageIndexProperty();
	}
	public void setScale(float scale){
		zoom.setValue(scale);
	}
	public float getScale(){
		return zoom.getValue();
	}
	public FloatProperty scaleProperty(){
		return zoom;
	}
}
