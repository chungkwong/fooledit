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
package com.github.chungkwong.jtk.example.text;
import com.github.chungkwong.jtk.api.*;
import com.github.chungkwong.jtk.model.*;
import java.awt.event.*;
import javafx.application.*;
import javafx.embed.swing.*;
import javafx.scene.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CodeEditor implements DataEditor<TextObject>{
	@Override
	public Node edit(TextObject data){
		SwingNode node=new SwingNode();
		JTextPane editor=new JTextPane();
		editor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				Platform.runLater(()->{node.requestFocus();});
			}
		});
		SwingUtilities.invokeLater(()->{
			editor.setEditorKit(new StyledEditorKit());
			editor.setEditable(true);
			editor.setText(data.getText().get());
			editor.getDocument().addDocumentListener(new DocumentListener(){
				@Override
				public void insertUpdate(DocumentEvent e){
					data.getText().set(editor.getText());
				}
				@Override
				public void removeUpdate(DocumentEvent e){
					data.getText().set(editor.getText());
				}
				@Override
				public void changedUpdate(DocumentEvent e){
				}
			});
			data.getText().addListener((e,o,n)->{
				if(!editor.getText().equals(n)){
					editor.setText(n);
				}
			});
		});
		node.setContent(editor);
		node.setFocusTraversable(true);
		return node;
	}
	@Override
	public String getName(){
		return MessageRegistry.getString("CODE_EDITOR");
	}
}
