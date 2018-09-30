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
package cc.fooledit.editor.text;
import java.util.function.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class SimpleTooltipProvider implements TooltipProvider<Label>{
	private final BiFunction<Integer,CodeEditor,String> provider;
	public SimpleTooltipProvider(BiFunction<Integer,CodeEditor,String> provider){
		this.provider=provider;
	}
	@Override
	public Label createNode(){
		return new Label();
	}
	@Override
	public boolean updateNode(int index,CodeEditor editor,Label tooltip){
		String text=provider.apply(index,editor);
		if(text!=null){
			tooltip.setText(text);
			return true;
		}else{
			return false;
		}
	}
}
