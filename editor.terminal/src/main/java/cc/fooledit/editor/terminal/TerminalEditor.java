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
package cc.fooledit.editor.terminal;
import cc.fooledit.core.*;
import cc.fooledit.spi.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TerminalEditor implements DataEditor<TerminalObject>{
	public static final TerminalEditor INSTANCE=new TerminalEditor();
	private TerminalEditor(){

	}
	@Override
	public Node edit(TerminalObject data,Object remark,RegistryNode<String,Object> meta){
		BorderPane borderPane=new BorderPane();
		data.getTerminal().onTerminalFxReady(()->Platform.runLater(()->borderPane.setCenter(data.getTerminal().getContent())));
		borderPane.setCenter(data.getTerminal().getContent());
		return borderPane;
	}
	@Override
	public void dispose(Node node,RegistryNode<String,Object> meta){
		
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("TERMINAL",Activator.class);
	}

}
