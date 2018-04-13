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
import java.util.function.*;
import javafx.scene.*;
import javafx.scene.effect.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SimpleEffectTool implements EffectTool{
	private final String name;
	private final Supplier<Node> control;
	private final Function<Node,Effect> effect;
	public SimpleEffectTool(String name,Supplier<Node> control,Function<Node,Effect> effect){
		this.name=name;
		this.control=control;
		this.effect=effect;
	}
	@Override
	public String getName(){
		return name;
	}
	@Override
	public Effect getEffect(Node control){
		return effect.apply(control);
	}
	@Override
	public Node getControl(){
		return control.get();
	}
	@Override
	public String toString(){
		return MessageRegistry.getString(name,ImageModule.NAME);
	}
}
