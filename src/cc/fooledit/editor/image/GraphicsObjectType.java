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
package cc.fooledit.editor.image;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import java.awt.image.*;
import java.net.*;
import javafx.embed.swing.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javax.imageio.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class GraphicsObjectType implements DataObjectType<GraphicsObject>{
	public static final GraphicsObjectType INSTANCE=new GraphicsObjectType();
	private GraphicsObjectType(){
	}

	@Override
	public boolean canRead(){
		return true;
	}
	@Override
	public boolean canWrite(){
		return true;
	}
	@Override
	public boolean canCreate(){
		return true;
	}
	@Override
	public GraphicsObject create(){
		return new GraphicsObject(new StackPane(new Canvas()));
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("GRAPHICS",ImageModule.NAME);
	}
	@Override
	public void writeTo(GraphicsObject data,URLConnection connection,RegistryNode<String,Object,String> meta) throws Exception{
		GraphicsViewer viewer=new GraphicsViewer(data);
		SnapshotParameters snapshotParameters=new SnapshotParameters();
		snapshotParameters.setViewport(data.viewportProperty().getValue());
		WritableImage snapshot=viewer.snapshot(snapshotParameters,null);
		String mime=(String)meta.getChildOrDefault(DataObject.MIME,"image/png");
		ImageIO.write(SwingFXUtils.fromFXImage(snapshot,null),mime.substring(mime.indexOf('/')+1),connection.getOutputStream());
	}
	@Override
	public GraphicsObject readFrom(URLConnection connection,RegistryNode<String,Object,String> meta) throws Exception{
		BufferedImage image=ImageIO.read(connection.getInputStream());
		return new GraphicsObject(new StackPane(GraphicsObject.imageToCanvas(SwingFXUtils.toFXImage(image,null))));
	}
}
