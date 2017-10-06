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
package cc.fooledit.example.text;
import cc.fooledit.control.*;
import java.util.*;
import java.util.stream.*;
import javafx.scene.control.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class ParseTreeViewer extends TreeView{
	public ParseTreeViewer(ParserRuleContext context){
		setCellFactory((p)->new ParserCell());
		setRoot(createTreeItem(context));
	}
	private TreeItem<ParseTree> createTreeItem(ParserRuleContext context){
		if(context.getChildCount()>0){
			return new LazyTreeItem<>(()->getChildren(context),context);
		}else{
			return new TreeItem<>(context);
		}
	}
	private TreeItem<ParseTree> createTreeItem(ParseTree tree){
		if(tree.getChildCount()>0){
			return new LazyTreeItem<>(()->getChildren(tree),tree);
		}else{
			return new TreeItem<>(tree);
		}
	}
	private Collection<TreeItem<ParseTree>> getChildren(ParserRuleContext context){
		return context.children.stream().map((tree)->createTreeItem(tree)).collect(Collectors.toList());
	}
	private Collection<TreeItem<ParseTree>> getChildren(ParseTree tree){
		return java.util.stream.IntStream.range(0,tree.getChildCount()).mapToObj((i)->createTreeItem(tree.getChild(i))).collect(Collectors.toList());
	}
	private static class ParserCell extends TreeCell{
		@Override
		protected void updateItem(Object item,boolean empty){
			super.updateItem(item,empty);
			if(item instanceof ParserRuleContext){
				setText(((ParserRuleContext)item).toStringTree());
			}else if(item instanceof ParseTree){
				setText(((ParseTree)item).toStringTree());
			}
		}
	}
}
