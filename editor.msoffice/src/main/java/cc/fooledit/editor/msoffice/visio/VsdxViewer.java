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
package cc.fooledit.editor.msoffice.visio;
import cc.fooledit.control.*;
import java.io.*;
import java.util.logging.*;
import javafx.scene.image.*;
import org.apache.poi.xdgf.usermodel.*;
import org.apache.poi.xdgf.usermodel.shape.*;
import org.apache.poi.xdgf.util.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class VsdxViewer extends PaginationWrapper{
	public VsdxViewer(XmlVisioDocument document){
		File[] pagePng=document.getPages().stream().map((page)->{
			try{
				File f=File.createTempFile("vsd",".png");
				f.deleteOnExit();
				VsdxToPng.renderToPng(page,f,1.0,new ShapeRenderer());
				return f;
			}catch(IOException ex){
				Logger.getLogger(VsdxViewer.class.getName()).log(Level.SEVERE,null,ex);
				return null;
			}
		}).toArray(File[]::new);
		setPageCount(document.getPages().size());
		setPageFactory((i)->{
			return pagePng[i]!=null?new ImageView(pagePng[i].toURI().toString()):null;
		});
	}
}
