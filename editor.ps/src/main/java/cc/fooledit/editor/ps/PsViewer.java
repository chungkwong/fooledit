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
package cc.fooledit.editor.ps;
import cc.fooledit.control.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.logging.*;
import javafx.embed.swing.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import org.freehep.postscript.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PsViewer extends BorderPane{
	private final PaginationWrapper pagination;
	private final PSInputFile document;
	public PsViewer(PSInputFile document){
		this.document=document;
		pagination=new PaginationWrapper(1,0);
		pagination.setPageFactory((index)->{
			Dimension dim=new Dimension(800,600);
			BufferedImage image=new BufferedImage((int)dim.getWidth(),(int)dim.getHeight(),BufferedImage.TYPE_INT_ARGB);
			Processor processor=new Processor(image.createGraphics(),dim);
			try{
				new PSViewer(processor,document,index+1,1.0,1.0,0.0,0.0,false);
			}catch(IOException ex){
				Logger.getLogger(PsViewer.class.getName()).log(Level.SEVERE,null,ex);
			}
			return new javafx.scene.control.ScrollPane(new ImageView(SwingFXUtils.toFXImage(image,null)));
		});
		setCenter(pagination);
	}
	public PSInputFile getDocument(){
		return document;
	}
}
