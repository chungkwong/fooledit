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
import com.github.chungkwong.jtk.control.*;
import com.github.chungkwong.jtk.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class CompleteSupport implements KeyListener,FocusListener{
	private final JTextComponent comp;
	private final AutoCompleteProvider hints;
	private static final PopupAutoCompleteHint popupAutoCompleteHint=new PopupAutoCompleteHint();
	private static final RealTimeTask<AutoCompleteHintContext> task=new RealTimeTask<>((o)->{
		AutoCompleteHint[] hint=o.provider.checkForHints(o.component.getText(),o.position).toArray(AutoCompleteHint[]::new);
		SwingUtilities.invokeLater(()->popupAutoCompleteHint.showAutoCompleteHints(o.component,o.position,hint));
	});
	private CompleteSupport(JTextComponent comp,AutoCompleteProvider hints){
		this.hints=hints;
		this.comp=comp;
		comp.addKeyListener(this);
		comp.addFocusListener(this);
		comp.addCaretListener((e)->updateAutoCompleteHint());
	}
	public static final void apply(AutoCompleteProvider hints,JTextComponent comp){
		new CompleteSupport(comp,hints);
	}
	public void updateAutoCompleteHint(){
		task.summit(new AutoCompleteHintContext(hints,comp,comp.getSelectionStart()));
	}
	public static void main(String[] args){
		JFrame f=new JFrame("Test");
		JTextField field=new JTextField();
		CompleteSupport.apply(AutoCompleteProvider.createSimple(Arrays.asList(
				AutoCompleteHint.create("ln","ln","<h1>link file</h1>"),
				AutoCompleteHint.create("pwd","pwd","show working directory"),
				AutoCompleteHint.create("ps","ps","list files"))),field);
		f.add(field,BorderLayout.CENTER);
		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	@Override
	public void keyTyped(KeyEvent e){

	}
	@Override
	public void keyPressed(KeyEvent e){
		if(popupAutoCompleteHint.isShowing())
			switch(e.getKeyCode()){
				case KeyEvent.VK_UP:
					popupAutoCompleteHint.selectPrevious();
					break;
				case KeyEvent.VK_DOWN:
					popupAutoCompleteHint.selectNext();
					break;
				case KeyEvent.VK_ENTER:
					popupAutoCompleteHint.choose();
					break;
				case KeyEvent.VK_ESCAPE:
					popupAutoCompleteHint.hideAutoCompleteHints();
					break;
			}
	}
	@Override
	public void keyReleased(KeyEvent e){

	}
	@Override
	public void focusGained(FocusEvent e){
		updateAutoCompleteHint();
	}
	@Override
	public void focusLost(FocusEvent e){
		popupAutoCompleteHint.hideAutoCompleteHints();
	}
	static class AutoCompleteHintContext{
		final AutoCompleteProvider provider;
		final JTextComponent component;
		final int position;
		public AutoCompleteHintContext(AutoCompleteProvider provider,JTextComponent component,int position){
			this.provider=provider;
			this.component=component;
			this.position=position;
		}
	}
}
class PopupAutoCompleteHint extends JPanel implements MouseInputListener,ListSelectionListener{
	private final DefaultListModel<AutoCompleteHint> vec=new DefaultListModel<>();
	private final JEditorPane note=new JEditorPane();
	private final JList<AutoCompleteHint> loc=new JList<AutoCompleteHint>(vec);
	private Document doc;
	private int pos;
	private Popup popup;
	public PopupAutoCompleteHint(){
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(400,300));
		loc.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		loc.setSelectedIndex(0);
		loc.addMouseListener(this);
		loc.addListSelectionListener(this);
		loc.setCellRenderer(new DefaultListCellRenderer(){
			@Override
			public Component getListCellRendererComponent(JList arg0,Object arg1,int arg2,boolean arg3,boolean arg4){
				Component c=super.getListCellRendererComponent(arg0,arg1,arg2,arg3,arg4);
				((JLabel)c).setText(((AutoCompleteHint)arg1).getDisplayText());
				//((JLabel)c).setIcon(((AutoCompleteHint)arg1).getIcon());
				((JLabel)c).setHorizontalAlignment(SwingConstants.LEFT);
				return c;
			}
		});
		loc.setOpaque(false);
		add(new JScrollPane(loc),BorderLayout.WEST);
		note.setContentType("text/html");
		note.setEditable(false);
		add(new JScrollPane(note),BorderLayout.CENTER);
	}
	public void showAutoCompleteHints(JTextComponent comp,int pos,AutoCompleteHint[] choices){
		hideAutoCompleteHints();
		if(choices.length==0)
			return;
		this.pos=pos;
		vec.ensureCapacity(choices.length);
		for(int i=0;i<choices.length;i++)
			vec.add(i,choices[i]);
			loc.setSelectedIndex(0);
		try{
			Point loc=comp.modelToView(pos).getLocation();
			int lineheight=comp.getFontMetrics(comp.getFont()).getHeight();
			loc.translate((int)comp.getLocationOnScreen().getX(),(int)comp.getLocationOnScreen().getY()+lineheight);
			popup=PopupFactory.getSharedInstance().getPopup(comp,this,(int)loc.getX(),(int)loc.getY());
			popup.show();
			doc=comp.getDocument();
		}catch(BadLocationException|NullPointerException|IllegalComponentStateException ex){

		}
	}
	public void hideAutoCompleteHints(){
		vec.removeAllElements();
		if(popup!=null)
			popup.hide();
		popup=null;
		doc=null;
	}
	public boolean isShowing(){
		return popup!=null;
	}
	void selectPrevious(){
		if(!vec.isEmpty())
			loc.setSelectedIndex((loc.getSelectedIndex()+vec.getSize()-1)%vec.getSize());
	}
	void selectNext(){
		if(!vec.isEmpty())
			loc.setSelectedIndex((loc.getSelectedIndex()+1)%vec.getSize());
	}
	void choose(){
		choose(vec.getElementAt(loc.getSelectedIndex()).getInputText());
	}
	private void choose(String inputText){
		try{
			doc.insertString(pos,inputText,null);
		}catch(Exception ex){
			Logger.getGlobal().log(Level.FINER,inputText,ex);
		}
		hideAutoCompleteHints();
	}
	@Override
	public void mouseClicked(MouseEvent e){
		if(e.getClickCount()==2){
			choose(vec.get(loc.locationToIndex(e.getPoint())).getInputText());
		}
	}
	@Override
	public void mousePressed(MouseEvent e){}
	@Override
	public void mouseReleased(MouseEvent e){}
	@Override
	public void mouseEntered(MouseEvent e){}
	@Override
	public void mouseExited(MouseEvent e){}
	@Override
	public void mouseDragged(MouseEvent e){}
	@Override
	public void mouseMoved(MouseEvent e){}
	@Override
	public void valueChanged(ListSelectionEvent e){
		try{
			if(loc.getSelectedValue()!=null){
				note.read(loc.getSelectedValue().getDocument(),null);
			}
		}catch(IOException ex){
			note.setText("NO_DOCUMENT");
		}
	}
}