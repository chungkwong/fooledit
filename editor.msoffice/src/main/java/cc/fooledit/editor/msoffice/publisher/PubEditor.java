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
package cc.fooledit.editor.msoffice.publisher;
import cc.fooledit.core.*;
import cc.fooledit.editor.msoffice.*;
import cc.fooledit.spi.*;
import javafx.scene.*;
import javafx.scene.control.*;
import org.apache.poi.hpbf.extractor.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class PubEditor implements DataEditor<PubObject>{
	public static final PubEditor INSTANCE=new PubEditor();
	private PubEditor(){
	}
	@Override
	public Node edit(PubObject data,Object remark,RegistryNode<String,Object> meta){
		return new TextArea(new PublisherTextExtractor(data.getDocument()).getText());
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("DOCUMENT_EDITOR",MsOfficeModule.NAME);
	}
}