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
package com.github.chungkwong.jtk.editor;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class NumberedScrollPane extends JPanel{
	private final LineNumberSideBar sideBar=new LineNumberSideBar();
	private final JTextComponent editor;
	private final JScrollPane scrollPane;
	public NumberedScrollPane(JTextComponent editor){
		super(new BorderLayout());
		this.editor=editor;
		editor.getDocument().addUndoableEditListener((e)->sideBar.repaint());
		scrollPane=new JScrollPane(editor);
		scrollPane.getVerticalScrollBar().addAdjustmentListener((e)->sideBar.repaint());
		add(scrollPane,BorderLayout.CENTER);
		add(sideBar,BorderLayout.WEST);
	}
	private class LineNumberSideBar extends JComponent{
		private Dimension dimCache=new Dimension(1,1);
		public LineNumberSideBar(){
			setMaximumSize(new Dimension(Integer.MAX_VALUE,Integer.MAX_VALUE));
		}
		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			BoxView root=(BoxView)editor.getUI().getRootView(editor).getView(0);
			int numberWidth=g.getFontMetrics().stringWidth(Integer.toString(root.getViewCount()+1));
			int numberHeight=g.getFontMetrics().getHeight();
			if(dimCache.getWidth()!=numberWidth){
				dimCache=new Dimension(numberWidth,(int)getVisibleRect().getHeight());
				setPreferredSize(dimCache);
				setMinimumSize(dimCache);
				invalidate();
				NumberedScrollPane.this.validate();
			}
			Rectangle visibleRect=scrollPane.getViewport().getViewRect();
			int startOffset=editor.viewToModel(visibleRect.getLocation());
			int endOffset=editor.viewToModel(new Point(visibleRect.x,visibleRect.y+visibleRect.height));
			int startLine=root.getViewIndex(startOffset,Position.Bias.Forward);
			int endLine=root.getViewIndex(endOffset,Position.Bias.Forward);
			for(int i=startLine;i<=endLine;i++){
				Rectangle bounds=root.getChildAllocation(i,visibleRect).getBounds();
				g.drawString(Integer.toString(i+1),0,bounds.y+numberHeight-2*visibleRect.y);
			}
		}
	}
}
