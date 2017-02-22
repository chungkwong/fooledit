/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.chungkwong.jtk.editor.ui;
import com.github.chungkwong.jtk.editor.lex.*;
import java.util.*;
import javax.swing.text.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class StyleScheme{
	private final HashMap<String,AttributeSet> style=new HashMap<>();
	private final Lex lex=new NaiveLex();//No scalable
	public void addTokenType(String type,String regex,AttributeSet set){
		lex.addType(type,regex);
		style.put(type,set);
	}
	public void updateStyle(StyledDocument doc){
		String text=null;
		try{
			text=doc.getText(0,doc.getLength());
		}catch(BadLocationException ex){

		}
		Iterator<Token> iter=lex.split(text);
		int index=0;
		while(iter.hasNext()){
			Token token=iter.next();
			doc.setCharacterAttributes(index,token.getText().length(),style.get(token.getType()),true);
			index+=token.getText().length();
		}
	}
}
