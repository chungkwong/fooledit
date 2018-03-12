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
	private final ImageView view=new ImageView();
	private final Spinner<Number> curr;
	private final Spinner<Number> zoom;
	public PdfViewer(PDDocument document){
		this.document=document;
		this.renderer=new PDFRenderer(document);
		curr=new Spinner<>(0,document.getNumberOfPages()-1,0);
		zoom=new Spinner<>(0,Double.MAX_VALUE,1.0,0.5);
		setCenter(new ScrollPane(view));
		setRight(getInfoPane());
		setTop(getPagePane());
		setLeft(getOutlinePane());
	}
	private Node getPagePane(){
		curr.setEditable(true);
		zoom.setEditable(true);
		curr.valueProperty().addListener((e,o,n)->{
			showPage(n.intValue(),zoom.getValue().floatValue());
		});
		zoom.valueProperty().addListener((e,o,n)->{
			showPage(curr.getValue().intValue(),n.floatValue());
		});
		Label total=new Label("/"+document.getNumberOfPages());
		Label percent=new Label("×");
		showPage(0,1.0f);
		return new HBox(curr,total,zoom,percent);
	}
	public void showPage(int index,float dpi){
		try{
			view.setImage(SwingFXUtils.toFXImage(renderer.renderImage(index,dpi),null));
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
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
		curr.getEditor().setText(Integer.toString(page));
	}
	public void setScale(float scale){
		zoom.getEditor().setText(Float.toString(scale));
	}
	public int getPageIndex(){
		return curr.getValue().intValue();
	}
	public float getScale(){
		return zoom.getValue().floatValue();
	}
}
