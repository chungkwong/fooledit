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
import com.github.chungkwong.jtk.editor.ui.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Main{
	public static void main(String[] args){
		JFrame f=new JFrame("Test");
		//JEditorPane editor=new JEditorPane();
		JTextPane editor=new JTextPane();
		editor.setEditorKit(new javax.swing.text.StyledEditorKit());
		editor.setEditable(true);
		StyleScheme scheme=new StyleScheme();
		SyntaxHighlightSupport.apply(getExampleScheme(),(StyledDocument)editor.getDocument());
		f.add(new JScrollPane(editor),BorderLayout.CENTER);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		editor.requestFocusInWindow();
	}
	private static StyleScheme getExampleScheme(){
		StyleScheme scheme=new StyleScheme();
		SimpleAttributeSet italic=new SimpleAttributeSet();
		StyleConstants.setItalic(italic,true);
		scheme.addTokenType("NUM","[0-9]+",italic);
		SimpleAttributeSet bold=new SimpleAttributeSet();
		StyleConstants.setBold(bold,true);
		scheme.addTokenType("LETTER","[a-zA-Z]+",bold);
		SimpleAttributeSet red=new SimpleAttributeSet();
		StyleConstants.setForeground(red,Color.RED);
		scheme.addTokenType("OTHER","[^0-9a-zA-Z]+",red);
		return scheme;
	}
}