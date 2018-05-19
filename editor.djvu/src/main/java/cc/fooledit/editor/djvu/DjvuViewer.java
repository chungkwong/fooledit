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
package cc.fooledit.editor.djvu;
import cc.fooledit.control.*;
import com.lizardtech.djvu.*;
import com.lizardtech.djvubean.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.logging.*;
import javafx.beans.property.*;
import javafx.embed.swing.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DjvuViewer extends BorderPane{
	private final Document document;
	private final Pagination pagination;
	private final ImageView view=new ImageView();
	private final FloatProperty zoom=new SimpleFloatProperty(1.0f);
	private final DoubleProperty rotate=new SimpleDoubleProperty(0.0);
	private ImageView currentPage;
	public DjvuViewer(Document document){
		this.document=document;
		this.pagination=new PaginationWrapper(document.size(),0);
		pagination.setPageFactory((index)->{
		currentPage=new ImageView(getPage(index,zoom.getValue().floatValue()));
		return currentPage;
		});
		zoom.addListener((e,o,n)->{
		if(currentPage!=null)
		currentPage.setImage(getPage(getPageIndex(),n.floatValue()));
		});
		rotate.addListener((e,o,n)->{
		if(currentPage!=null)
		currentPage.setRotate(n.doubleValue());
		});
		setCenter(new ScrollPaneWrapper(pagination));
	}
	public Image getPage(int index,float dpi){
		try{
			DjVuPage page=document.getPage(index,Document.MAX_PRIORITY,false);
			DjVuImage djVuImage=new DjVuImage(new DjVuPage[]{page},false,(int)(dpi*100+0.5),new Dimension());
			Rectangle bounds=djVuImage.getBounds();

			BufferedImage image=new BufferedImage((int)bounds.getWidth(),(int)bounds.getHeight(),BufferedImage.TYPE_INT_ARGB);
			Panel panel=new Panel();
			panel.setSize((int)bounds.getWidth(),(int)bounds.getHeight());
			djVuImage.draw(panel,image.createGraphics(),null);
			return SwingFXUtils.toFXImage(image,null);
			/*GMap bitmap=page.getMap(new GRect(0,0,page.getInfo().width,page.getInfo().height),16,null);
			int height=bitmap.rows();
			int width=bitmap.columns();
			WritableImage image=new WritableImage(width,height);
			PixelWriter pixelWriter=image.getPixelWriter();
			GPixelReference pixelRef=bitmap.createGPixelReference(0);
			for(int i=0;i<height;i++)
			for(int j=0;j<width;j++){
			pixelWriter.setColor(j,i,javafx.scene.paint.Color.rgb(pixelRef.getRed(),pixelRef.getGreen(),pixelRef.getBlue()));
			pixelRef.incOffset();
			}
			return image;*/
		}catch(IOException ex){
			Logger.getGlobal().log(Level.SEVERE,null,ex);
			return new WritableImage(0,0);
		}
	}
	public Document getDocument(){
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
	public void setPageRotate(double degree){
		zoom.setValue(degree);
	}
	public double getPageRotate(){
		return rotate.getValue();
	}
	public DoubleProperty pageRotateProperty(){
		return rotate;
	}
	@Override
	public void requestFocus(){
		pagination.requestFocus();
	}
}
