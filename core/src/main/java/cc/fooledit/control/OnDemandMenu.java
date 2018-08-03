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
package cc.fooledit.control;
import cc.fooledit.core.*;
import java.util.function.*;
import javafx.collections.*;
import javafx.scene.control.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class OnDemandMenu extends Menu{
	private final MenuItem stub=new MenuItem(MessageRegistry.getString("NO_MORE",Activator.class));
	public OnDemandMenu(String title,Consumer<ObservableList<MenuItem>> setter){
		super(title);
		stub.setDisable(true);
		getItems().add(stub);
		setOnShowing((e)->{
			getItems().clear();
			setter.accept(getItems());
			if(getItems().isEmpty()){
				getItems().add(stub);
			}
		});
		setOnHiding((e)->getItems().setAll(stub));
	}
}
