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
package com.github.chungkwong.jtk.editor.ui;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class SyntaxHighlightSupport{
	private static int editing=0;//Single thread rule
	public static void apply(StyleScheme scheme,StyledDocument doc){
		doc.addDocumentListener(
			new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e){
				++editing;
				SwingUtilities.invokeLater(()->update(scheme,doc));
			}
			@Override
			public void removeUpdate(DocumentEvent e){
				++editing;
				SwingUtilities.invokeLater(()->update(scheme,doc));
			}
			@Override
			public void changedUpdate(DocumentEvent e){

			}
		}
		);
	}
	private static void update(StyleScheme scheme,StyledDocument doc){
		--editing;
		if(editing==0){
			scheme.updateStyle(doc);
		}
	}
}
