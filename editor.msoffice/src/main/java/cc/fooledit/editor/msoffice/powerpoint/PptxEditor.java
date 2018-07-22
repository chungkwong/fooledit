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
import cc.fooledit.core.*;
import cc.fooledit.editor.msoffice.*;
import cc.fooledit.spi.*;
import javafx.scene.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PptxEditor implements DataEditor<PptxObject>{
	public static final PptxEditor INSTANCE=new PptxEditor();
	private PptxEditor(){
	}
	@Override
	public Node edit(PptxObject data,Object remark,RegistryNode<String,Object> meta){
		return new PptxViewer(data.getDocument());
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("SLIDE_SHOW",Activator.NAME);
	}
}