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
package com.github.chungkwong.fooledit.editor.lex;

/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class Token{
	private final String text;
	private final String type;
	private final int position;
	public Token(String text,String type,int position){
		this.text=text;
		this.type=type;
		this.position=position;
	}
	public String getText(){
		return text;
	}
	public String getType(){
		return type;
	}
	public int getPosition(){
		return position;
	}
	@Override
	public String toString(){
		return type+":"+text;
	}
}