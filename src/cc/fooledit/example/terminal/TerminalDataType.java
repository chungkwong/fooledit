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
package cc.fooledit.example.terminal;
import cc.fooledit.model.*;
import java.net.*;
/**
 *
 * @author Chan Chung Kwong <1m02math@126.com>
 */
public class TerminalDataType implements DataObjectType<TerminalData>{
	public static final TerminalDataType INSTANCE=new TerminalDataType();
	private TerminalDataType(){

	}
	@Override
	public boolean canRead(){
		return false;
	}
	@Override
	public boolean canWrite(){
		return false;
	}
	@Override
	public boolean canCreate(){
		return true;
	}
	@Override
	public TerminalData create(){
		return new TerminalData();
	}
	@Override
	public String getName(){
		return "terminal";
	}
	@Override
	public void writeTo(TerminalData data,URLConnection connection) throws Exception{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public TerminalData readFrom(URLConnection connection) throws Exception{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
