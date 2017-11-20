/*
 * Copyright (C) 2017 Chan Chung Kwong <1m02math@126.com>
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
package cc.fooledit.example.image;
import cc.fooledit.model.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ImageObject extends AbstractDataObject<ImageObject>{
	private final Canvas canvas;
	public ImageObject(Image image){
		canvas=new Canvas(image.getWidth(),image.getHeight());
		canvas.getGraphicsContext2D().drawImage(image,0,0);
	}
	public Canvas getCanvas(){
		return canvas;
	}
	@Override
	public DataObjectType<ImageObject> getDataObjectType(){
		return ImageObjectType.INSTANCE;
	}
}
