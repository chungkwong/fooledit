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
import java.net.*;
import javafx.scene.*;
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
		return new GraphicsObject(new Node[0]);
	}
	@Override
	public String getDisplayName(){
		return MessageRegistry.getString("GRAPHICS",ImageEditorModule.NAME);
	}
	@Override
	public void writeTo(GraphicsObject data,URLConnection connection,RegistryNode<String,Object,String> meta) throws Exception{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public GraphicsObject readFrom(URLConnection connection,RegistryNode<String,Object,String> meta) throws Exception{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
