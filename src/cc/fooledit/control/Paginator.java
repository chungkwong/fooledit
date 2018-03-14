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
import java.util.function.*;
import javafx.beans.property.*;
import javafx.scene.*;
import javafx.scene.layout.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Paginator extends VBox{
	private final Function<Integer,Node> pageFactory;
	private final IntegerProperty pageCount;
	private final IntegerProperty pageIndex;
	public Paginator(Function<Integer,Node> pageFactory,int count){
		this.pageFactory=pageFactory;
		pageCount=new SimpleIntegerProperty(count);
		pageIndex=new SimpleIntegerProperty(0);
		pageIndex.addListener((e,o,n)->showPage(n.intValue()));
	}
	public IntegerProperty pageCount(){
		return pageCount;
	}
	public IntegerProperty getPageIndex(){
		return pageIndex;
	}
	private void showPage(int page){
		
	}
}
