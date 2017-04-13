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
package com.github.chungkwong.jtk.api;
import com.github.chungkwong.jtk.*;
import com.github.chungkwong.jtk.control.*;
import com.github.chungkwong.jtk.model.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javax.script.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class MiniBuffer extends BorderPane{
	private final TextField input=new TextField();
	private final Main main;
	private final AutoCompleteProvider commandHints=new CommandComplete();
	private AutoCompleteProvider hints;
	public MiniBuffer(Main main){
		this.main=main;
		setFocusTraversable(false);
		new AutoCompleteService(input,(text,pos)->hints==null?Stream.empty():hints.checkForHints(text,pos));
		setCenter(input);
		restore();
	}
	public void setMode(Consumer<String> action,AutoCompleteProvider hints,String init,Node supp){
		this.hints=hints;
		input.setText(init);
		input.setOnAction((e)->action.accept(input.getText()));
		setRight(supp);
	}
	private void restore(){
		hints=commandHints;
		setRight(null);
		input.setOnAction((e)->{
			Command command=main.getCommandRegistry().get(input.getText());
			if(command!=null){
				main.getNotifier().notify(MessageRegistry.getString("EXECUTING")+command.getDisplayName());
				main.getCommandRegistry().get(input.getText()).accept(main);
				main.getNotifier().notify(MessageRegistry.getString("EXECUTED")+command.getDisplayName());
			}else{
				try{
					main.getNotifier().notify(Objects.toString(ScriptAPI.SCHEME_ENGINE.eval(input.getText())));
				}catch(ScriptException ex){
					Logger.getGlobal().log(Level.SEVERE,MessageRegistry.getString("FAILED"),ex);
				}
			}
		});
	}
	@Override
	public void requestFocus(){
		super.requestFocus();
		input.requestFocus();
	}
	private class CommandComplete implements AutoCompleteProvider{
		@Override
		public Stream<AutoCompleteHint> checkForHints(String text,int pos){
			String prefix=text.substring(0,pos);
			return main.getCommandRegistry().keySet().stream().filter((name)->name.startsWith(prefix)&&name.length()>pos)
					.sorted().map((name)->AutoCompleteHint.create(name,name.substring(pos),""));
		}
	}
}
