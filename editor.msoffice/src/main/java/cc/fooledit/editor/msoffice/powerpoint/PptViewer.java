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
package cc.fooledit.editor.msoffice.powerpoint;
import cc.fooledit.control.*;
import java.awt.*;
import java.awt.image.*;
import java.util.List;
import javafx.embed.swing.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import org.apache.poi.hslf.usermodel.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PptViewer extends ScrollPane{
	private final HSLFSlideShow slideShow;
	private final PaginationWrapper pagination;
	public PptViewer(HSLFSlideShow slideShow){
		this.slideShow=slideShow;
		List<HSLFSlide> slides=slideShow.getSlides();
		pagination=new PaginationWrapper(slides.size(),0);
		int width=(int)slideShow.getPageSize().getWidth();
		int height=(int)slideShow.getPageSize().getHeight();
		BufferedImage swingImage=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		WritableImage fxImage=new WritableImage(width,height);
		pagination.setPageFactory((i)->{
			Graphics2D g2d=swingImage.createGraphics();
			g2d.setBackground(Color.WHITE);
			g2d.clearRect(0,0,width,height);
			slides.get(i).draw(g2d);
			return new ImageView(SwingFXUtils.toFXImage(swingImage,fxImage));
		});
		setContent(pagination);
	}
	@Override
	public void requestFocus(){
		pagination.requestFocus();
	}
}
