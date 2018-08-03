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
package cc.fooledit.control;
import cc.fooledit.core.*;
import java.util.*;
import java.util.function.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong
 */
public class DialogWrapper extends Tab{
	public DialogWrapper(String title,List<Argument> arguments,Consumer<List<Object>> onCommit){
		setText(title);
		FlowPane content=new FlowPane();
		Button commit=new Button(MessageRegistry.getString("COMMIT",Activator.class));
		content.getChildren().add(commit);
		setContent(content);
	}
}
