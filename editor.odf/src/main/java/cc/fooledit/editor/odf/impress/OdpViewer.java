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
package cc.fooledit.editor.odf.impress;
import cc.fooledit.control.*;
import javafx.scene.control.*;
import org.odftoolkit.simple.*;
import org.odftoolkit.simple.common.*;
import org.odftoolkit.simple.presentation.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class OdpViewer extends ScrollPane{
	private final PresentationDocument slideShow;
	private final PaginationWrapper pagination;
	public OdpViewer(PresentationDocument slideShow){
		this.slideShow=slideShow;
		pagination=new PaginationWrapper(slideShow.getSlideCount(),0);
		pagination.setPageFactory((i)->{
			Slide slide=slideShow.getSlideByIndex(i);
			/*Canvas canvas=new Canvas();
			GraphicsContext g2d=canvas.getGraphicsContext2D();
			slide.getTextboxIterator().forEachRemaining((box)->{
				g2d.strokeText(box.getTextContent(),box.getRectangle().getX(),box.getRectangle().getY());
			});
			return canvas;*/
			return new TextArea(EditableTextExtractor.newOdfEditableTextExtractor(slide.getOdfElement()).getText());
		});
		setContent(pagination);
	}
	@Override
	public void requestFocus(){
		pagination.requestFocus();
	}
}
