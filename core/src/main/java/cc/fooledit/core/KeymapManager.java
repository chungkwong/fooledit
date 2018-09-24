/*
 * Copyright (C) 2018 Chan Chung Kwong
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
package cc.fooledit.core;
import cc.fooledit.control.*;
import cc.fooledit.spi.*;
import java.util.*;
import javafx.scene.*;
import javafx.scene.input.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class KeymapManager{
	private String currKey=null;
	private boolean ignoreKey=false;
	private boolean recording=false;
	private List<KeyEvent> macro=new ArrayList<>();
	public KeymapManager(Scene scene){
		scene.addEventFilter(KeyEvent.ANY,(KeyEvent e)->{
			if(recording){
				macro.add(e.copyFor(e.getSource(),e.getTarget()));
			}
			if(e.getEventType().equals(KeyEvent.KEY_TYPED)){
				if(ignoreKey){
					e.consume();
				}
			}else if(e.getEventType().equals(KeyEvent.KEY_RELEASED)){
				if(ignoreKey){
					e.consume();
				}
			}else if(e.getEventType().equals(KeyEvent.KEY_PRESSED)){
				if(e.getCode().isModifierKey()){
					e.consume();
					return;
				}
				String code=currKey==null?encode(e):currKey+'+'+encode(e);
				Node node=scene.getFocusOwner();
				while(node!=null){
					NavigableRegistryNode<String,String> keymapRegistry=(NavigableRegistryNode<String,String>)node.getProperties().get(WorkSheet.KEYMAP_NAME);
					RegistryNode<String,Command> commandRegistry=(RegistryNode<String,Command>)node.getProperties().get(WorkSheet.COMMANDS_NAME);
					Object local=node.getProperties().get("keymap");
					if(keymapRegistry!=null&&commandRegistry!=null&&checkForKey(code,keymapRegistry,commandRegistry)){
						e.consume();
						return;
					}
					node=node.getParent();
				}
				currKey=null;
				Main.INSTANCE.getNotifier().notify("");
				ignoreKey=false;
			}
		});
	}
	public void startRecording(){
		macro.clear();
		recording=true;
	}
	public void stopRecording(){
		macro.remove(0);
		macro.remove(macro.size()-1);
		recording=false;
	}
	public boolean isRecording(){
		return recording;
	}
	public List<KeyEvent> getMacro(){
		return macro;
	}
	public void adopt(Node node,NavigableRegistryNode<String,String> keymapRegistry,RegistryNode<String,Command> commandRegistry){
		node.getProperties().put(WorkSheet.KEYMAP_NAME,keymapRegistry);
		node.getProperties().put(WorkSheet.COMMANDS_NAME,commandRegistry);
	}
	private boolean checkForKey(String code,NavigableRegistryNode<String,String> keymapRegistryNode,RegistryNode<String,Command> commandRegistry){
		Map.Entry<String,String> entry=keymapRegistryNode.ceilingEntry(code);
		if(entry!=null){
			if(code.equals(entry.getKey())){
				currKey=null;
				TaskManager.executeCommand(commandRegistry.get(entry.getValue()));
				ignoreKey=true;
				return true;
			}else if(entry.getKey().startsWith(code+' ')){
				currKey=code;
				Main.INSTANCE.getNotifier().notify(MessageRegistry.getString("ENTERED",Activator.class)+code);
				ignoreKey=true;
				return true;
			}
		}
		return false;
	}
	private final StringBuilder buf=new StringBuilder();
	private String encode(KeyEvent evt){
		buf.setLength(0);
		if(evt.isControlDown()||evt.isShortcutDown()){
			buf.append("C-");
		}
		if(evt.isAltDown()){
			buf.append("M-");
		}
		if(evt.isShiftDown()){
			buf.append("S-");
		}
		buf.append(evt.getCode().getName());
		return buf.toString();
	}
}
