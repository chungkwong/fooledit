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
package cc.fooledit.control;
import java.util.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class DraggableTabPane extends TabPane{
	private static final DataFormat TAB_FORMAT=new DataFormat("application/javafx.tab");
	private static Tab draged;
	private Object tag;
	public DraggableTabPane(){
		initEventHandlers();
	}
	public DraggableTabPane(Tab... tabs){
		super(tabs);
		initEventHandlers();
	}
	public Object getTag(){
		return tag;
	}
	public void setTag(Object tag){
		this.tag=tag;
	}
	private void initEventHandlers(){
		setOnDragDetected((e)->{
			Dragboard src=startDragAndDrop(TransferMode.MOVE);
			Map<DataFormat,Object> objects=new HashMap<>();
			objects.put(TAB_FORMAT,tag);
			draged=getSelectionModel().getSelectedItem();
			src.setContent(objects);
			e.consume();
		});
		setOnDragOver((e)->{
			if(e.getDragboard().hasContent(TAB_FORMAT)&&Objects.equals(tag,e.getDragboard().getContent(TAB_FORMAT))){
				e.acceptTransferModes(TransferMode.MOVE);
				e.consume();
			}
		});
		setOnDragDropped((e)->{
			if(e.getDragboard().hasContent(TAB_FORMAT)){
				draged.getTabPane().getTabs().remove(draged);
				getTabs().add(draged);
				getSelectionModel().select(draged);
				e.setDropCompleted(true);
				e.consume();
			}
			draged=null;
		});
	}
}
